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
                                         [[:created_on :datetime :default :current_timestamp]
                                          [:updated_on :datetime]
                                          [:url :text :UNIQUE]
                                          [:code :text]]))

       (catch BatchUpdateException e
         (when (not= (.getMessage e) "batch entry 0: [SQLITE_ERROR] SQL error or missing database (table statuses already exists)") (throw e)))))

(defn now
  "JDBC can't translate Date into someting
  sqlite understands, so we must do it ourselves.
  
  Returns the current time as a formatted string."
  []
  (let [d (java.util.Date.)]
    (.format (new java.text.SimpleDateFormat "yyyy-MM-dd HH:mm:ssZ") (new java.util.Date))))

(defn with-updated-on
  "Add the updated-on key to this status
  with the current time as it's value"
  [status]
  (assoc status :updated_on (now)))

(defn statement
  [status]
  ["INSERT INTO statuses
      (url, code, updated_on)
    VALUES
      (?, ?, ?)
   ON CONFLICT(url) DO UPDATE SET code=?, updated_on=?"
    (get status :url)
    (get status :code)
    (get status :updated_on)
    (get status :code)
    (get status :updated_on)])

(defn to-statement-with-updated-on
  "Add the current date and turn
  into an SQL statement"
  [status]
  (-> status
      with-updated-on
      statement))

(defn upsert-statuses!
  "Insert new status, or, if the url already exists,
  update the status and updated_on time"
  [statuses]
  (doseq [a-statement (map to-statement-with-updated-on statuses)]
    (execute! db a-statement)))

(defn get-statuses
  []
  (query db "SELECT url, code FROM statuses"))
