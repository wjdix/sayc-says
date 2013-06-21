(ns sayc-app.bridge.deck)

(def ranks
  [:2, :3, :4, :5, :6, :7, :8, :9, :10, :jack, :queen, :king, :ace])


(def suits
  [:club, :diamond, :heart, :spade])

(defn card-rank [{rank :rank}]
  (rank (apply hash-map (interleave (reverse ranks) (range)))))

(defn suit-rank [suit]
  (suit (apply hash-map (interleave (reverse suits) (range)))))

(def deck
  (for [rank ranks suit suits]
    {:suit suit :rank rank}))

(defn deal []
  (partition 13 (shuffle deck)))
