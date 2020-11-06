(ns cozysleep.core 
  (:require 
    [cozysleep.status :as status]
    [cozysleep.storage :as storage]
    [cozysleep.report :as report]
    [cozysleep.cpanel :as cpanel]))

; TODO: warn about stale results
; TODO: allow a configurable amount of failures

(def usage
"# cozysleep: Sleep better knowing your sites are up

## Usage

**report** report on previously saved statuses. Outputs in Nagios format.
Example: 
  ```
  lein run report
  ```

**urls** Get the status of some urls from the cli and save them for later
Example: 
  ```
  lein run urls https://google.com https://google.fr
  ```

**cpanel-domains** Get the status of some urls from `/etc/userdomains` and save them for later
Example:
  ```
  lein run cpanel-domains
  ```
")

(defn update-readme!
  []
  (spit "README.md" usage))

(defn -main
  [& args]
  (do
    (storage/create-db)
    (case (first args)
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
                                                          (cpanel/domains (second args))))
                          (shutdown-agents))
      (println usage) 
      )))
