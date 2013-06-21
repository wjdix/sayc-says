(ns sayc-app.bridge.hand-score)

(def hcps {:jack 1 :queen 2 :king 3 :ace 4})

(defn high-card-point [{rank :rank}] (rank hcps 0))

(defn hcp-score-hand [hand] (apply + (map high-card-point hand)))

(defn distribution-score [hand]
  (apply +
    (map (fn [x] (- (max 4 (count x)) 4)) (vals (group-by :suit hand)))))

(defn total-hand-score [hand]
  (+ (hcp-score-hand hand) (distribution-score hand)))

(defn balanced? [hand]
  (let [distribution (map count (vals (group-by :suit hand)))
        singletons   (some #(= % 1) distribution)
        doubletons   (filter #(= % 2) distribution)]
    (and (= (count distribution) 4)
      (not singletons)
      (not (<= 2 (count doubletons))))))
