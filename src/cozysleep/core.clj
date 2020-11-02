(ns cozysleep.core 
  (:import java.net.UnknownHostException)  
  (:require [clj-http.client :as client])
  )

(defn check-status
  "Gets the status for a url"
  [url]
  (try 
    (def status
      {:url url
       :status (get (client/get url) :status)})
    (catch UnknownHostException e 
      (def status {:url url 
                   :status 0})))
  )


(defn check-statuses
  "Check the status of multiple urls"
  [urls]
  (map check-status urls)
  )
