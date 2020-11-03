(ns cozysleep.core 
  (:require 
    [cozysleep.status :as status] 
    [cozysleep.storage :as storage]))

(defn -main
  "Setup and start the app"
  []
  (storage/create-db)
  (storage/insert-statuses! (map cozysleep.status/check-status
                                 ["https://example.com" "https://google.com" "https://lkjasfdlkjasdflkjafsdljksad.com"])))
