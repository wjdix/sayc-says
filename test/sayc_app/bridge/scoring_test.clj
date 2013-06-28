(ns sayc-app.bridge.scoring-test
  (:require [clojure.test :refer :all]
            [sayc-app.bridge.scoring :refer :all]))


(deftest score-round
  (testing "scores a made 1H contract with no overtricks"
    (let [expected {:we {:over 0 :under 30} :they {:over 0 :under 0 }}
          hand {:declarer :north :bid {:strain :heart :level 1} :tricks {:we 7 :they 6}}]
    (is (= expected  (score hand))))
  )
  (testing "scores a made 1H contract for east with no overtricks"
    (let [expected {:they {:over 0 :under 30} :we {:over 0 :under 0 }}
          hand {:declarer :east :bid {:strain :heart :level 1} :tricks {:we 6 :they 7}}]
    (is (= expected  (score hand))))
  )
  (testing "scores a 1NT contract for north with no overtricks"
    (let [expected {:we {:over 0 :under 40} :they {:over 0 :under 0 }}
          hand {:declarer :north :bid {:strain :notrump :level 1} :tricks {:we 7 :they 6}}]
    (is (= expected  (score hand))))
  )
  (testing "scores a 1D contract for north with no overtricks"
    (let [expected {:we {:over 0 :under 20} :they {:over 0 :under 0 }}
          hand {:declarer :north :bid {:strain :diamond :level 1} :tricks {:we 7 :they 6}}]
    (is (= expected  (score hand))))
  )
)
