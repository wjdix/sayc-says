(ns sayc-app.bridge.bidding-test
  (:require [clojure.test :refer :all]
            [sayc-app.bridge.types :refer (card bid)]
            [sayc-app.bridge.bidding :refer :all]
            [sayc-app.bridge.hand :refer (hand)]
            [clojure.test.check]
            [clojure.spec.test :as stest]))

(deftest generative-testing
  (testing "respond conforms to the spec"
    (let [results (-> (stest/check `sayc-app.bridge.bidding/respond) first :clojure.spec.test.check/ret :result)]
      (if (true? results)
        (is results)
        (is (= {} (ex-data results))))))
  (testing "open conforms to the spec"
    (let [results (-> (stest/check `sayc-app.bridge.bidding/open) first :clojure.spec.test.check/ret :result)]
      (if (true? results)
        (is results)
        (is (= {} (ex-data results)))))))

(deftest open-bidding
  (testing "bids 2NT hands"
    (is (= (bid 2 :notrump)
           (open (hand S A K J
                       H A 10 9 8
                       C A K Q
                       D 9 8 7)))))
  (testing "bids 1NT hands"
    (testing "opens 15 HCP balanced hands"
      (is (= (bid 1 :notrump)
             (open (hand S A K 5 4
                         H A 10 9
                         D A 10 8
                         C 4 3 2))))))
  (testing "bids level 1 majors correctly"
    (testing "bids 1S with opening points and 5 spades"
      (is (= (bid 1 :spade)
             (open (hand S A K 9 5 4
                         H A
                         D A 10 8
                         C 10 4 3 2)))))
    (testing "bids 1H with opening points and 5 hearts"
      (is (= (bid 1 :heart)
             (open (hand H A K 9 5 4
                         S A
                         D A 10 8
                         C 10 4 3 2)))))
    (testing "bids 1S if five spades and five hearts"
      (is (= (bid 1 :spade)
             (open (hand S A K 9 5 4
                         H A 10 4 3 2
                         D A 10 8))))))
  (testing "opens minor suits"
    (is (= (bid 1 :club)
           (open (hand C A K 9 5 4
                       H A 10 4 3
                       S 2
                       D A 10 8))))
    (is (= (bid 1 :diamond)
           (open (hand D A K 9 5 4
                       H A 10 4 3
                       S 2
                       C A 10 8))))))

(deftest respond-bidding
  (testing "responds to major bids at the 1 level"
    (is (= :pass
           (respond (hand S 4 3 2
                          H 9 8 7
                          D 9 8 7 5
                          C 4 3 2)
                    (bid 1 :spade))))
    (is (= (bid 2 :spade)
           (respond (hand S K Q 3
                          H 9 5
                          D 9 8 6 2
                          C A 7 3 2)
                    (bid 1 :spade))))
    (is (= (bid 2 :spade)
           (respond (hand S Q 8 3
                          H 9 5
                          D 9 6 4 2
                          C K Q J 3)
                    (bid 1 :spade))))))
