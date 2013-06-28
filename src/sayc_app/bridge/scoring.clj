(ns sayc-app.bridge.scoring)

(def declarer-score-name {:north :we :south :we :east :they :west :they})

(def defender-score-name-for-declarer {:north :they :south :they :east :we :west :we})

(defn declarer-tricks [hand]
  (get-in hand [:tricks (declarer-score-name (hand :declarer))]))

(defn score-for-declarer [bid-level declarer-tricks score-scale]
  {
   :over 0
   :under (apply + (take (max 0 (min (- declarer-tricks 6) bid-level)) score-scale))
   }
  )

(defn no-trump-score-for-declarer [bid-level declarer-tricks]
    (score-for-declarer bid-level declarer-tricks (cons 40 (repeat 30))))

(defn major-score-for-declarer [bid-level declarer-tricks]
  (score-for-declarer bid-level declarer-tricks (repeat 30)))

(defn minor-score-for-declarer [bid-level declarer-tricks]
  (score-for-declarer bid-level declarer-tricks (repeat 20)))

(defn points-for-defender [hand]
  {:over 0 :under 0 } )

(defn points-for-declarer [hand]
   (let [level (get-in hand [:bid :level])
         strain (get-in hand [:bid :strain])
         declarer-tricks (declarer-tricks hand)]
  (case strain
    :notrump (no-trump-score-for-declarer level declarer-tricks)
    :spade (major-score-for-declarer level declarer-tricks)
    :heart (major-score-for-declarer level declarer-tricks)
    :diamond (minor-score-for-declarer level declarer-tricks)
    :club (minor-score-for-declarer level declarer-tricks)
  )))

(defn declarer-key [hand]
  ((:declarer hand) declarer-score-name))

(defn defense-key [hand]
  ((:declarer hand) defender-score-name-for-declarer))

(defn score [hand]
  (let [declarer-points (points-for-declarer hand)
        defense-points (points-for-defender hand)]
    { (declarer-key hand) declarer-points (defense-key hand) defense-points}))
