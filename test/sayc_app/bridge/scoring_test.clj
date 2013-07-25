(ns sayc-app.bridge.scoring-test
  (:require [clojure.test :refer :all]
            [sayc-app.bridge.scoring :refer :all]))


(deftest score-round
  (testing "scores a made 1H contract with no overtricks"
    (let [expected {:we {:over 0 :under 30} :they {:over 0 :under 0 }}
          hand {:declarer :north :bid {:strain :heart :level 1} :tricks {:we 7 :they 6}}]
    (is (= expected  (score hand)))))
  (testing "scores a made 1H contract for east with no overtricks"
    (let [expected {:they {:over 0 :under 30} :we {:over 0 :under 0 }}
          hand {:declarer :east :bid {:strain :heart :level 1} :tricks {:we 6 :they 7}}]
    (is (= expected  (score hand)))))
  (testing "scores a 1NT contract for north with no overtricks"
    (let [expected {:we {:over 0 :under 40} :they {:over 0 :under 0 }}
          hand {:declarer :north :bid {:strain :notrump :level 1} :tricks {:we 7 :they 6}}]
    (is (= expected  (score hand)))))
  (testing "scores a 1D contract for north with no overtricks"
    (let [expected {:we {:over 0 :under 20} :they {:over 0 :under 0 }}
          hand {:declarer :north :bid {:strain :diamond :level 1} :tricks {:we 7 :they 6}}]
    (is (= expected  (score hand)))))
  (testing "scores a made 1H contract doubled"
    (let [expected {:we {:over 0 :under 60} :they {:over 0 :under 0 }}
          hand {:declarer :north :bid {:strain :heart :level 1 :doubled true} :tricks {:we 7 :they 6}}]
    (is (= expected  (score hand)))))
  (testing "scores a made 1H contract redoubled"
    (let [expected {:we {:over 0 :under 120} :they {:over 0 :under 0 }}
          bid {:strain :heart :level 1 :doubled true :redoubled true}
          hand {:declarer :north :bid bid :tricks {:we 7 :they 6}}]
    (is (= expected  (score hand)))))
  (testing "scores a made 1H contract with 1 overtrick not doubled"
    (let [expected {:we {:over 30 :under 30} :they {:over 0 :under 0 }}
          hand {:doubled false :declarer :north :bid {:strain :heart :level 1} :tricks {:we 8 :they 5}}]
    (is (= expected  (score hand)))))
  (testing "scores a made 1H contract with 1 overtrick doubled"
    (let [expected {:we {:over 100 :under 60} :they {:over 0 :under 0 }}
          hand {:declarer :north :bid {:strain :heart :level 1 :doubled true} :tricks {:we 8 :they 5}}]
    (is (= expected (score hand)))))
  (testing "scores a made 1H contract with 1 overtrick re-doubled"
    (let [expected {:we {:over 200 :under 120} :they {:over 0 :under 0 }}
          hand {:declarer :north :bid {:strain :heart :level 1 :redoubled true} :tricks {:we 8 :they 5}}]
    (is (= expected (score hand)))))
  (testing "scores a successful defense correctly"
    (let [expected {:we {:over 100 :under 0} :they {:over 0 :under 0 }}
          hand {:declarer :east :bid {:strain :heart :level 1} :tricks {:we 8 :they 5}}]
    (is (= expected (score hand)))))
  (testing "scores a successful defense when declarer is vulnerable correctly"
    (let [expected {:we {:over 200 :under 0} :they {:over 0 :under 0 }}
          bid {:strain :heart :level 1}
          hand {:declarer :east :bid bid :tricks {:we 8 :they 5} :vulnerable :they}]
    (is (= expected (score hand)))))
  (testing "slam bonuses are awarded"
    (let [expected {:we {:over 500 :under 180} :they {:over 0 :under 0 }}
          hand {:declarer :north :bid {:strain :heart :level 6} :tricks {:we 12 :they 1}}]
    (is (= expected (score hand)))))
  (testing "vulnerable slam bonuses are awarded"
    (let [expected {:we {:over 750 :under 180} :they {:over 0 :under 0 }}
          hand {:declarer :north :bid {:strain :heart :level 6} :tricks {:we 12 :they 1} :vulnerable :we}]
    (is (= expected (score hand))))))

(deftest add-bridge-scores
  (testing "it correctly adds bridge scores"
    (is (= {:we {:over 100 :under 50} :they {:over 400 :under 60}}
           (add-score {:we {:over 40 :under 25} :they {:over 300 :under 10}}
                      {:we {:over 60 :under 25} :they {:over 100 :under 50}})))))
