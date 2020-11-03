(ns cozysleep.core 
  (:import java.net.UnknownHostException)
  (:import java.sql.BatchUpdateException)
  (:require [clj-http.client :as client]
            [clojure.java.jdbc :refer :all])
  )

(defn check-status
  "Gets the status for a url"
  [url]
    {:url url
     :code (try
               (get (client/get url) :code)
               (catch UnknownHostException _ 0))})

(defn check-statuses
  "Check the status of multiple urls"
  [urls]
  (map check-status urls))


(def db
  {:classname   "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname     "db/database.db"
   })

(defn create-db
  "create db and table"
  []
  (try (db-do-commands db
                       (create-table-ddl :codees
                                         [[:timestamp :datetime :default :current_timestamp]
                                          [:url :text]
                                          [:code :text]]))
       (catch BatchUpdateException e
         (when (not= (.getMessage e) "batch entry 0: [SQLITE_ERROR] SQL error or missing database (table statuses already exists)") (throw e))
       )))


(defn -main
  "Setup and start the app"
  []
  (create-db)
  (insert-multi! db :codees (map check-status
                             ["https://example.com" "https://google.com" "https://lkjasfdlkjasdflkjafsdljksad.com"])))
