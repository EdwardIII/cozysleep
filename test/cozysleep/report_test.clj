(ns cozysleep.report-test
  (:require [clojure.test :refer :all]
            [cozysleep.report :as report :refer :all]
            [cozysleep.status :as status :refer :all]
            [java-time :as time]))

(def sample-mixed
  [{
    :code "200"
    :url "https://www.google.com"
    :updated-on (time/local-date-time 2015 10 01)}
   {
    :code "0"
    :url "http://kjaskdjhfkajhsdkjfh.com"
    :updated-on (time/local-date-time 2015 10 01)
    :message "ECONNECTFAILED"}])

(deftest nagios-output-reports-on-failing-sites
  (testing "nagios-output Reports on failing sites"
    (with-redefs [report/is-stale (fn [st] false)]
      (let [output (report/nagios-output sample-mixed)]
        (is (= "CRITICAL - 1 sites reported failure: http://kjaskdjhfkajhsdkjfh.com (ECONNECTFAILED)" (get output :message)))
        (is (= 2 (get output :exitcode)))))))

(def thenish (time/local-date-time 2015 10 01))

(def good-status
  {
    :code "200"
    :url "https://www.google.com"
    :updated-on thenish})

(deftest stale-to-bad-should-return-bad-result
    (is (=
          {:code "0"
            :url "https://www.google.com"
            :updated-on thenish
            :message "was not updated since 2015-10-01T00:00. Maybe your runner isn't working?"}
          (report/stale-to-bad good-status))))

(deftest stale-to-bad-should-leave-fresh-results-untouched
  (with-redefs [report/is-stale (fn [st] false)]
    (is (=
          good-status
          (report/stale-to-bad good-status)))))

(deftest is-stale-if-older-than-an-hour
  (is (= true (report/is-stale good-status))))
