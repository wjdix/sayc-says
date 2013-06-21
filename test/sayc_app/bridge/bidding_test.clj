(ns sayc-app.bridge.bidding-test
  (:require [clojure.test :refer :all]
            [sayc-app.bridge.bidding :refer :all]))

(deftest open-bidding
  (testing "bids 1NT hands"
    (testing "opens 15 HCP balanced hands"
      (let [hand [
                   {:suit :spade :rank :ace}
                   {:suit :spade :rank :king}
                   {:suit :spade :rank :5}
                   {:suit :spade :rank :4}
                   {:suit :heart :rank :ace}
                   {:suit :heart :rank :10}
                   {:suit :heart :rank :9}
                   {:suit :diamond :rank :ace}
                   {:suit :diamond :rank :10}
                   {:suit :diamond :rank :8}
                   {:suit :club :rank :4}
                   {:suit :club :rank :3}
                   {:suit :club :rank :2} ]]
        (is (= {:strain :notrump :level 1} (open hand))))))
  (testing "bids level 1 majors correctly"
    (testing "bids 1S with opening points and 5 spades"
      (let [hand [
                   {:suit :spade :rank :ace}
                   {:suit :spade :rank :king}
                   {:suit :spade :rank :9}
                   {:suit :spade :rank :5}
                   {:suit :spade :rank :4}
                   {:suit :heart :rank :ace}
                   {:suit :diamond :rank :ace}
                   {:suit :diamond :rank :10}
                   {:suit :diamond :rank :8}
                   {:suit :club :rank :10}
                   {:suit :club :rank :4}
                   {:suit :club :rank :3}
                   {:suit :club :rank :2} ]]
      (is (= {:strain :spade :level 1} (open hand)))))
    (testing "bids 1H with opening points and 5 hearts"
      (let [hand [
                   {:suit :heart :rank :ace}
                   {:suit :heart :rank :king}
                   {:suit :heart :rank :9}
                   {:suit :heart :rank :5}
                   {:suit :heart :rank :4}
                   {:suit :spade :rank :ace}
                   {:suit :diamond :rank :ace}
                   {:suit :diamond :rank :10}
                   {:suit :diamond :rank :8}
                   {:suit :club :rank :10}
                   {:suit :club :rank :4}
                   {:suit :club :rank :3}
                   {:suit :club :rank :2}]]
        (is (= {:strain :heart :level 1} (open hand)))))
    (testing "bids 1S if five spades and five hearts"
      (let [hand [
                  {:suit :spade :rank :ace}
                  {:suit :spade :rank :king}
                  {:suit :spade :rank :9}
                  {:suit :spade :rank :5}
                  {:suit :spade :rank :4}
                  {:suit :heart :rank :ace}
                  {:suit :heart :rank :10}
                  {:suit :heart :rank :4}
                  {:suit :heart :rank :3}
                  {:suit :heart :rank :2}
                  {:suit :diamond :rank :ace}
                  {:suit :diamond :rank :10}
                  {:suit :diamond :rank :8}]]
      (is (= {:strain :spade :level 1} (open hand)))))
    (testing "opens minor suits"
      (let [club_hand [
                  {:suit :club :rank :ace}
                  {:suit :club :rank :king}
                  {:suit :club :rank :9}
                  {:suit :club :rank :5}
                  {:suit :club :rank :4}
                  {:suit :heart :rank :ace}
                  {:suit :heart :rank :10}
                  {:suit :heart :rank :4}
                  {:suit :heart :rank :3}
                  {:suit :spade :rank :2}
                  {:suit :diamond :rank :ace}
                  {:suit :diamond :rank :10}
                  {:suit :diamond :rank :8}]
            diamond_hand [
                  {:suit :diamond :rank :ace}
                  {:suit :diamond :rank :king}
                  {:suit :diamond :rank :9}
                  {:suit :diamond :rank :5}
                  {:suit :diamond :rank :4}
                  {:suit :heart :rank :ace}
                  {:suit :heart :rank :10}
                  {:suit :heart :rank :4}
                  {:suit :heart :rank :3}
                  {:suit :spade :rank :2}
                  {:suit :club :rank :ace}
                  {:suit :club :rank :10}
                  {:suit :club :rank :8}]
            ]
        (is (= {:strain :club :level 1} (open club_hand)))
        (is (= {:strain :diamond :level 1} (open diamond_hand)))))))
