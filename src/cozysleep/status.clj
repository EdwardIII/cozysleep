(ns cozysleep.status 
  (:import java.net.UnknownHostException)
  (:require [clj-http.client :as client]))

(def sample-mixed
  [{
    :code 200
    :url "https://www.google.com"
    }
   {
    :code 0
    :url "http://kjaskdjhfkajhsdkjfh.com"
    }])

(defn check-status
  "Gets the status for a url by calling out to it over http"
  [url]
    {:url url
     :code (try
               (get (client/get url) :status)
               (catch UnknownHostException _ 0))})

(defn check-statuses
  "Check the status of multiple urls"
  [urls]
  (map check-status urls))
