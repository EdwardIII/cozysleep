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
                                          [:url :text]
                                          [:code :text]]))
       (catch BatchUpdateException e
         (when (not= (.getMessage e) "batch entry 0: [SQLITE_ERROR] SQL error or missing database (table statuses already exists)") (throw e))
       )))

(defn insert-statuses! 
  [statuses]
  insert-multi! db :statuses statuses)
