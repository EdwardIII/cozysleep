(ns cozysleep.cpanel)

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

(defn domains
  "Get a list of domain names from /etc/userdomains

  Will turn:
  
  edwardiii.co.uk: edwardiii
  openedweb.co.uk: openedwe
  
  into:
  
  [http://edwardiii.co.uk http://openedweb.co.uk]"
  [path]
  (lines-to-urls (file-to-list (or path "/etc/userdomains"))))
