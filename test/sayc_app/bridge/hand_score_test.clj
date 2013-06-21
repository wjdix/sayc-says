(ns sayc-app.bridge.hand-score-test
  (:require [clojure.test :refer :all]
            [sayc-app.bridge.hand-score :refer :all]))

(deftest card-score
  (testing "assigns correct hcp values for face cards"
    (are [value rank] (= value (high-card-point {:rank rank}))
         4 :ace 3 :king 2 :queen 1 :jack)))

(deftest handscore-test
  (let [hand [{:suit :spade, :rank :jack}
                {:suit :spade, :rank :king}
                {:suit :spade, :rank :5}
                {:suit :heart, :rank :5}
                {:suit :heart, :rank :3}
                {:suit :diamond, :rank :queen}
                {:suit :diamond, :rank :10}
                {:suit :diamond, :rank :5}
                {:suit :club, :rank :10}
                {:suit :club, :rank :5}
                {:suit :club, :rank :ace}
                {:suit :club, :rank :jack}
                {:suit :club, :rank :7}]]
    (testing "calculating High Card Point score"
      (is (= 11 (hcp-score-hand hand))))
    (testing "distribution score"
      (is (= 1 (distribution-score hand))))
    (testing "total hand score"
      (is (= 12 (total-hand-score hand))))))

(deftest balance-test
  (let [balanced [
                  {:suit :spade, :rank :jack}
                  {:suit :spade, :rank :king}
                  {:suit :spade, :rank :5}
                  {:suit :heart, :rank :5}
                  {:suit :heart, :rank :3}
                  {:suit :diamond, :rank :queen}
                  {:suit :diamond, :rank :10}
                  {:suit :diamond, :rank :5}
                  {:suit :club, :rank :10}
                  {:suit :club, :rank :5}
                  {:suit :club, :rank :ace}
                  {:suit :club, :rank :jack}
                  {:suit :club, :rank :7}]
        unbalanced [
                  {:suit :spade, :rank :jack}
                  {:suit :spade, :rank :5}
                  {:suit :heart, :rank :5}
                  {:suit :heart, :rank :3}
                  {:suit :diamond, :rank :king}
                  {:suit :diamond, :rank :queen}
                  {:suit :diamond, :rank :10}
                  {:suit :diamond, :rank :5}
                  {:suit :club, :rank :10}
                  {:suit :club, :rank :5}
                  {:suit :club, :rank :ace}
                  {:suit :club, :rank :jack}
                  {:suit :club, :rank :7}]
        voided [
                  {:suit :heart, :rank :jack}
                  {:suit :heart, :rank :6}
                  {:suit :heart, :rank :5}
                  {:suit :heart, :rank :3}
                  {:suit :diamond, :rank :king}
                  {:suit :diamond, :rank :queen}
                  {:suit :diamond, :rank :10}
                  {:suit :diamond, :rank :5}
                  {:suit :club, :rank :10}
                  {:suit :club, :rank :5}
                  {:suit :club, :rank :ace}
                  {:suit :club, :rank :jack}
                  {:suit :club, :rank :7}]]
    (testing "balanced hands do not include multiple doubletons"
      (is (not (balanced? unbalanced))))
    (testing "a 5-3-3-2 hand is balanced"
      (is (balanced? balanced)))
    (testing "a hand with a void is not balanced"
      (is (not (balanced? voided))))))
