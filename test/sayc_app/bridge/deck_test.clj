(ns sayc-app.bridge.deck-test
  (:require [clojure.test :refer :all]
            [sayc-app.bridge.deck :refer :all]))

(deftest deal-score
  (testing "divides deck into 4 13 card hands"
    (is (= 4 (count (deal))))
    (is (= 13 (count (first (deal)))))))
