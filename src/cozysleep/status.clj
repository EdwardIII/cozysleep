(ns cozysleep.status 
  (:import java.net.UnknownHostException)
  (:import java.net.ConnectException)
  (:import java.net.SocketTimeoutException)
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
               (get (client/get url {:socket-timeout 5000 :connection-timeout 5000}) :status)
               (catch UnknownHostException _ 0)
               (catch ConnectException _ 0)
               (catch SocketTimeoutException _ 0))})
