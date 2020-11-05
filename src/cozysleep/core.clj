(ns cozysleep.core 
  (:require 
    [cozysleep.status :as status]
    [cozysleep.storage :as storage]
    [cozysleep.report :as report]))

(defn file-to-list
  "Take a file and turn it into a list, with one entry per line"
  [filename]
  (clojure.string/split-lines (slurp filename)))

(defn line-to-url
  [line]
  (str "http://" (clojure.string/replace-first line #":.*" "")))

(defn lines-to-urls
  [lines]
  (map line-to-url lines))

(defn cpanel-domains
  "Get a list of domain names from /etc/userdomains

  Will turn:
  
  edwardiii.co.uk: edwardiii
  openedweb.co.uk: openedwe
  
  into:
  
  [http://edwardiii.co.uk http://openedweb.co.uk]"
  [path]
  (lines-to-urls (file-to-list path)))

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
      "urls"  (storage/upsert-statuses! (map cozysleep.status/check-status args))
      ;; Assume the next arg is a path to the cpanel userdomains file, or default to /etc/userdomains
      "cpanel-domains"  (storage/upsert-statuses! (map cozysleep.status/check-status (cpanel-domains (first args)))))))
