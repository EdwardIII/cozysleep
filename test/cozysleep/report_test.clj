(ns cozysleep.report-test
  (:require [clojure.test :refer :all]
            [cozysleep.report :as report :refer :all]
            [cozysleep.status :as status :refer :all]
            ))

(deftest a-test
  (testing "nagios-output Reports on failing sites"
    (let [output (report/nagios-output status/sample-mixed)]
      (is (= "CRITICAL - 1 sites reported failure: http://kjaskdjhfkajhsdkjfh.com (0)" (get output :message)))
      (is (= 2 (get output :exitcode))))))
