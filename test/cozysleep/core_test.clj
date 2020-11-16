(ns cozysleep.core-test
  (:require [clojure.test :refer :all]
            [cozysleep.core :refer :all]))

(deftest main-runs-ok
  (testing "The app runs"
    (with-redefs-fn {#'cozysleep.core/usage (fn [] "fake usage message")}
      #(is (= nil (-main))))))
