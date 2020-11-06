(ns cozysleep.core 
  (:require 
    [cozysleep.status :as status]
    [cozysleep.storage :as storage]
    [cozysleep.report :as report]
    [cozysleep.cpanel :as cpanel]))

(defn -main
  "Start the app and create the db, if needed"
  [mode & args]
  (do
    (storage/create-db)
    (case mode
      "report" (let [output (report/nagios-output status/sample-mixed)]
                 (do
                   (println (get output :message))
                   (System/exit (get output :exitcode))))
      ;; "Assume the rest of the args were a bunch of urls"
      "urls"  (do
                (storage/upsert-statuses! (pmap cozysleep.status/check-status args))
                (shutdown-agents))
      ;; Assume the next arg is a path to the cpanel userdomains file, or default to /etc/userdomains
      "cpanel-domains"  (do
                          (storage/upsert-statuses! (pmap cozysleep.status/check-status
                                                          (cpanel/domains (first args))))
                          (shutdown-agents)))))
