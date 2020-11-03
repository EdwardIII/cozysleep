(ns cozysleep.report [:require [clojure.string :as string]])

;; TODO: update status to code in all statuses

(def sample-bad-statuses
  [{
    :code 200
    :url "https://www.google.com"
    }
   {
    :code 0
    :url "http://kjaskdjhfkajhsdkjfh.com"
    }])

(defn is-bad
  "This is a bad status"
  [status]
  (not= 200 (get status :code))

(defn bad-statuses
  [statuses]
  "These are all the bad statuses"
  (filter (fn[s] (not= 200 (get s :code))) statuses))

(defn status-to-string
  "Give nice looking text representation of a status"
  [status]
  (str (get status :url) " (" (get status :code) ")"))

(defn exit-code-for
  "Returns error code 2 for bad statuses, 
  or success code 0 if not"
  [statuses]
  (if (> 0 (count (bad-statuses statuses))) 2 0))

(defn messsage-for-statuses
  "returns a success or failure message
  depending on the statuses"
  [statuses]
  (let [dodgy-statuses (bad-statuses statuses)]
    (if (> 0 (count dodgy-statuses))
              (str "OK - " (count statuses) " sites returned 200")
              (str 
                   "CRITICAL - " 
                   (count dodgy-statuses)
                   " sites reported failure: "
                   (string/join  ", " (map status-to-string dodgy-statuses))))))
                  ;; outputs "CRITICAL - 1 sites reported failure:  ()"
                  ;; why? something to do with lazyness of map?

(defn nagios-output
  [statuses]
  {:exitcode (exit-code-for statuses)
   :message (messsage-for-statuses statuses)})
