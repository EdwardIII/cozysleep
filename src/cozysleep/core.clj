(ns cozysleep.core 
  (:require 
    [cozysleep.status :as status] 
    [cozysleep.storage :as storage]))

(defn -main
  "Start the app and create the db, if needed"
  []
  (storage/create-db)
                       ;; TODO: Take from stdin
  (let [urls (map :url status/sample-mixed)]
    (storage/upsert-statuses! (map cozysleep.status/check-status urls))))
