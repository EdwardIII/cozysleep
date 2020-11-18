(ns cozysleep.report
  [:require
   [clojure.string :as string]
   [java-time :as time]])

(defn is-bad
  "This is a bad status"
  [status]
  (not= "200" (get status :code)))

(defn bad-statuses
  [statuses]
  "These are all the bad statuses"
  (filter (fn[s] (not= 200 (get s :code))) statuses))

(defn code-or-message
  [status]
  "If status is 0, use the message instead"
  (if (= 0 (status :code))
    (status :message)
    (status :code)))

(defn status-to-string
  "Give nice looking text representation of a status"
  [status]
  (str (get status :url) " (" (code-or-message status) ")"))

(defn exit-code-for
  "Returns error code 2 for bad statuses,
  or success code 0 if not"
  [statuses]
  (if (seq (bad-statuses statuses)) 2 0))

(defn now
  []
  (time/local-date-time))

(defn hours-ago
  [hours]
  (time/minus (now) (time/hours hours)))

;; TODO: Update the messaging to warn if
;; the results are stale
(defn is-stale
  [status]
  (time/before? (status :updated-on) (hours-ago 1)))

(defn bad-status-message
  [dodgy-statuses]
  (str
    "CRITICAL - "
    (count dodgy-statuses)
    " sites reported failure: "
    (string/join  ", " (map status-to-string dodgy-statuses))))

(defn good-status-message
  [statuses]
  (str "OK - " (count statuses) " sites returned 200"))

(defn stale-to-bad
  "if status is stale, turn it bad"
  [status]
  (if (is-stale status)
    (assoc status
           :code 0
           :message (str "was not updated since " (status :updated-on) ". Maybe your runner isn't working?"))
    status))

(defn gimmie-bad-statuses
  [statuses]
  (->> statuses
      (map stale-to-bad)
      (filter is-bad)))

(defn messsage-for-statuses
  "returns a success or failure message
  depending on the statuses"
  [statuses]
  ;(let [dodgy-statuses (bad-statuses (map stale-to-bad statuses))]
  (let [dodgy-statuses (gimmie-bad-statuses statuses)]
    (if (seq dodgy-statuses)
      (bad-status-message dodgy-statuses)
      (good-status-message statuses))))

(defn nagios-output
  "returns a map with the :exitcode to quit with
  and :message to print out. Example:
  {:exitcode 2, :message \"1 sites had failures: https://www.badsite.com (500)"
  [statuses]
  {:exitcode (exit-code-for statuses)
   :message (messsage-for-statuses statuses)})
