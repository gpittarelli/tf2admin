(ns tf2admin.core-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [tf2admin.core :as core]))

(deftest fake-test
  (testing "fake description"
    (is (= 1 2))))
