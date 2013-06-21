(ns sayc-app.bridge.bidding
  (:require [sayc-app.bridge.hand-score :as score]))

(def levels (range 1 8))

(def strains [:club :diamond :heart :spade :notrump])

(defn opening-points? [hand]
  (some #{(score/total-hand-score hand)} (range 13 25)))

(defn one-nt-conditions [hand]
  (and (some #{(score/hcp-score-hand hand)} (range 15 18))
       (score/balanced? hand)))

(defn five-card-major-conditions [hand suit]
  (and (opening-points? hand)
       (= 5 (count (suit (group-by :suit hand))))))

(defn four-card-minor-conditions [hand suit]
  (and (opening-points? hand)
       (<= 4 (count (suit (group-by :suit hand))))))

(defn one-spade-conditions [hand] (five-card-major-conditions hand :spade))

(defn one-heart-conditions [hand] (five-card-major-conditions hand :heart))

(defn one-diamond-conditions [hand] (four-card-minor-conditions hand :diamond))

(defn one-club-conditions [hand] (four-card-minor-conditions hand :club))

(defn open [hand]
  (cond (one-nt-conditions hand) {:strain :notrump :level 1}
        (one-spade-conditions hand) {:strain :spade :level 1}
        (one-heart-conditions hand) {:strain :heart :level 1}
        (one-diamond-conditions hand) {:strain :diamond :level 1}
        (one-club-conditions hand) {:strain :club :level 1}
        :else :pass))
