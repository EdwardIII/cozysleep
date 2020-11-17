(ns cozysleep.storage-test
  (:require [clojure.test :refer :all]
            [cozysleep.storage :as storage]))

(def status
  {:url "http://edwardiii.co.uk"
   :code "200"
   :updated-on "2020-11-16 20:04:30"})

(deftest test-date-hydrate
  (let [updated-status, (storage/hydrate-status status)]
    (is 
      (= java.time.LocalDateTime
         (type (updated-status :updated-on))))))
