(ns cozysleep.status-test
  (:require [clojure.test :refer :all]
            [cozysleep.status :as status]))

(deftest check-status-200
  (is (= 200
         (get (status/check-status "https://www.google.com") :code))))
