(ns cozysleep.core 
  (:require 
    [cozysleep.status :as status]
    [cozysleep.storage :as storage]
    [cozysleep.report :as report]))

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
      "save"  (let [urls (map :url status/sample-mixed)]
                  ;; TODO: Take from stdin
                  "Assume the rest of the args were a bunch of urls"
                  (storage/upsert-statuses! (map cozysleep.status/check-status args))))))
