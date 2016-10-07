(ns sayc-app.bridge.bidding-test
  (:require [clojure.test :refer :all]
            [sayc-app.bridge.types :refer (card bid)]
            [sayc-app.bridge.bidding :refer :all]
            [clojure.test.check]
            [clojure.spec.test :as stest]))

(deftest open-bidding
  (testing "it conforms to the spec"
    (let [results (-> (stest/check `sayc-app.bridge.bidding/open) first :clojure.spec.test.check/ret :result)]
      (if (true? results)
        (is results)
        (is (= {} (ex-data results))))))
  (testing "bids 2NT hands"
    (is (= (bid 2 :notrump)
           (open [(card :spade :ace)
                  (card :spade :king)
                  (card :spade :jack)
                  (card :heart :ace)
                  (card :heart 10)
                  (card :heart 9)
                  (card :heart 8)
                  (card :club :ace)
                  (card :club :king)
                  (card :club :queen)
                  (card :diamond 9)
                  (card :diamond 8)
                  (card :diamond 7)]))))
  (testing "bids 1NT hands"
    (testing "opens 15 HCP balanced hands"
      (let [hand [(card :spade :ace)
                  (card :spade :king)
                  (card :spade 5)
                  (card :spade 4)
                  (card :heart :ace)
                  (card :heart 10)
                  (card :heart 9)
                  (card :diamond :ace)
                  (card :diamond 10)
                  (card :diamond 8)
                  (card :club 4)
                  (card :club 3)
                  (card :club 2)]]
        (is (= (bid 1 :notrump) (open hand))))))
  (testing "bids level 1 majors correctly"
    (testing "bids 1S with opening points and 5 spades"
      (let [hand [(card :spade :ace)
                  (card :spade :king)
                  (card :spade 9)
                  (card :spade 5)
                  (card :spade 4)
                  (card :heart :ace)
                  (card :diamond :ace)
                  (card :diamond 10)
                  (card :diamond 8)
                  (card :club 10)
                  (card :club 4)
                  (card :club 3)
                  (card :club 2)]]
        (is (= (bid 1 :spade) (open hand)))))
    (testing "bids 1H with opening points and 5 hearts"
      (let [hand [(card :heart :ace)
                  (card :heart :king)
                  (card :heart 9)
                  (card :heart 5)
                  (card :heart 4)
                  (card :spade :ace)
                  (card :diamond :ace)
                  (card :diamond 10)
                  (card :diamond 8)
                  (card :club 10)
                  (card :club 4)
                  (card :club 3)
                  (card :club 2)]]
        (is (= (bid 1 :heart) (open hand)))))
    (testing "bids 1S if five spades and five hearts"
      (let [hand [(card :spade :ace)
                  (card :spade :king)
                  (card :spade 9)
                  (card :spade 5)
                  (card :spade 4)
                  (card :heart :ace)
                  (card :heart 10)
                  (card :heart 4)
                  (card :heart 2)
                  (card :diamond :ace)
                  (card :diamond 10)
                  (card :diamond 8)]]
        (is (= (bid 1 :spade) (open hand))))))
  (testing "opens minor suits"
    (let [club_hand [(card :club :ace)
                     (card :club :king)
                     (card :club 9)
                     (card :club 5)
                     (card :club 4)
                     (card :heart :ace)
                     (card :heart 10)
                     (card :heart 4)
                     (card :heart 3)
                     (card :spade 2)
                     (card :diamond :ace)
                     (card :diamond 10)
                     (card :diamond 8)]
          diamond_hand [(card :diamond :ace)
                        (card :diamond :king)
                        (card :diamond 9)
                        (card :diamond 5)
                        (card :diamond 4)
                        (card :heart :ace)
                        (card :heart 10)
                        (card :heart 4)
                        (card :heart 3)
                        (card :spade 2)
                        (card :club :ace)
                        (card :club 10)
                        (card :club 8)]]
      (is (= (bid 1 :club) (open club_hand)))
      (is (= (bid 1 :diamond) (open diamond_hand))))))

(deftest respond-bidding
  (testing "conforms to the spec"
    (let [results (-> (stest/check `sayc-app.bridge.bidding/respond) first :clojure.spec.test.check/ret :result)]
      (if (true? results)
        (is results)
        (is (= {} (ex-data results))))))
  (testing "responds to major bids at the 1 level"
    (let [responder-hand [(card :spade :queen)
                          (card :spade 8)
                          (card :spade 3)
                          (card :heart 9)
                          (card :heart 5)
                          (card :diamond 9)
                          (card :diamond 6)
                          (card :diamond 4)
                          (card :diamond 2)
                          (card :club :king)
                          (card :club :queen)
                          (card :club :jack)
                          (card :club 3)]]
      (is (= (bid 2 :spade) (respond responder-hand (bid 1 :spade)))))))
