(ns cozysleep.storage
  (:import 
    (java.sql BatchUpdateException)
    (java.text SimpleDateFormat)
    (java.util Date))
           
  (:require [clojure.java.jdbc :refer :all]
            [java-time :as time]))

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

(def datetime-format "yyyy-MM-dd HH:mm:ss")

(defn now
  "Returns the current time as a formatted string."
  []
  (time/format datetime-format (time/local-date-time)))

(defn with-updated-on
  "Add the updated-on key to this status
  with the current time as it's value"
  [status]
  (assoc status :updated-on (now)))

(defn statement
  [status]
  ["INSERT INTO statuses
      (url, code, updated_on)
    VALUES
      (?, ?, ?)
   ON CONFLICT(url) DO UPDATE SET code=?, updated_on=?"
    (get status :url)
    (get status :code)
    (get status :updated-on)
    (get status :code)
    (get status :updated-on)])

(defn to-date
  [string]
  (time/local-date-time datetime-format string))

(defn hydrate-status
  [status]
  (update-in status [:updated-on] to-date))

(defn to-statement-with-updated-on
  "Add the current date and turn
  into an SQL statement"
  [status]
  (-> status
      with-updated-on
      statement))

(def identifiers {:identifiers #(.replace % \_ \-)})

(defn hours-ago
  [hours]
  (time/minus (time/local-date-time) (time/hours hours)))

(defn upsert-statuses!
  "Insert new status, or, if the url already exists,
  update the status and updated_on time"
  [statuses]
  (doseq [a-statement (map to-statement-with-updated-on statuses)]
    (execute! db a-statement identifiers)))

(defn get-statuses
  []
  (map hydrate-status (query db "SELECT url, code, updated_on FROM statuses" identifiers)))
