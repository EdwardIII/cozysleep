(ns cozysleep.status-test
  (:require [clojure.test :refer :all]
            [cozysleep.status :as status]))

(deftest check-statuses-tests
  (testing "check-statuses with mixed statuses"
    (is (= status/sample-mixed 
           (status/check-statuses 
             ["https://www.google.com"
              "http://kjaskdjhfkajhsdkjfh.com"])))))
