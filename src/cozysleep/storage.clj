(ns cozysleep.storage
  (:import java.sql.BatchUpdateException)
  (:require [clojure.java.jdbc :refer :all]))

(def db
  {:classname   "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname     "db/database.db"
   })

(defn create-db
  "create db and table"
  []
  (try (db-do-commands db
                       (create-table-ddl :statuses
                                         [[:timestamp :datetime :default :current_timestamp]
                                          [:url :text :UNIQUE]
                                          [:code :text]]))
       (catch BatchUpdateException e
         (when (not= (.getMessage e) "batch entry 0: [SQLITE_ERROR] SQL error or missing database (table statuses already exists)") (throw e))
       )))

(defn statement
  [status]
  ["INSERT INTO statuses (url, code) VALUES (?, ?) ON CONFLICT(url) DO UPDATE SET code=?" (get status :url) (get status :code) (get status :code)])

(defn upsert-statuses!
  [statuses]
  (doseq [a-statement (map statement statuses)]
    (execute! db a-statement)))

(defn get-statuses
  []
  (query db "SELECT url, code FROM statuses"))
