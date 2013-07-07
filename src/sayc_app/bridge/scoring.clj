(ns sayc-app.bridge.scoring)

(def declarers [:north :south :east :west])
(def vulnerabilities [:we :they :both :none])
(def declarer-score-name {:north :we :south :we :east :they :west :they})
(def defender-score-name-for-declarer {:north :they :south :they :east :we :west :we})

(def base-score-scale
  {:notrump (cons 40 (repeat 30))
   :spade (repeat 30)
   :heart (repeat 30)
   :diamond (repeat 20)
   :club (repeat 20)})

(defn declarer-key [hand]
  ((:declarer hand) declarer-score-name))

(defn defense-key [hand]
  ((:declarer hand) defender-score-name-for-declarer))

(defn declarer-vulnerable [hand]
  (let [vulnerability (:vulnerable hand)
        declarer (declarer-key hand)]
    (or (= vulnerability :both)
        (= declarer vulnerability))))

(defn penalty-score-scale-for-bid [{:keys [doubled redoubled]} declarer-vulnerable?]
  (if declarer-vulnerable?
    (cond redoubled (cons 400 (repeat 600))
          doubled   (cons 200 (repeat 300))
          :else     (repeat 100))
    (cond redoubled (concat [200 400 400] (repeat 600))
          doubled (concat [100 200 200] (repeat 300))
          :else (repeat 50))))

(defn score-scale-for-bid [{:keys [strain doubled redoubled]}]
  (cond redoubled (map #(* 4 %) (strain base-score-scale))
        doubled (map #(* 2 %) (strain base-score-scale))
        :else (strain base-score-scale)))

(defn overtrick-score-scale-for-bid [{:keys [strain doubled redoubled]}]
  (cond
    doubled (repeat 100)
    redoubled (repeat 200)
    :else (case strain
            :notrump (repeat 30)
            :spade (repeat 30)
            :heart (repeat 30)
            :diamond (repeat 20)
            :club (repeat 20))))

(defn declarer-tricks [hand]
  (get-in hand [:tricks (declarer-score-name (hand :declarer))]))


(defn under-tricks [hand]
  (let [level (get-in hand [:bid :level])]
    (max 0 (- (+ 6 level) (declarer-tricks hand)))))

(def small-slam-bonuses
  {false 500 true 750})

(def grand-slam-bonuses
  {false 1000 true 1500})

(defn slam-points [bid declarer-tricks declarer-vulnerable?]
  (cond
    (and (= (:level bid) 6) (<= 12 declarer-tricks)) (small-slam-bonuses declarer-vulnerable?)
    (and (= (:level bid) 7) (= 13 declarer-tricks)) (grand-slam-bonuses declarer-vulnerable?)
    :else 0))

(defn score-for-declarer [bid declarer-tricks vulnerable?]
  (let [bid-level (:level bid)
        score-scale (score-scale-for-bid bid)
        over-score-scale (overtrick-score-scale-for-bid bid)
        over-trick-points (apply + (take (max 0 (- declarer-tricks (+ bid-level 6))) over-score-scale))
        slam-points (slam-points bid declarer-tricks vulnerable?)]
    {:over (+ over-trick-points slam-points)
     :under (apply + (take (max 0 (min (- declarer-tricks 6) bid-level)) score-scale))}))

(defn points-for-defender [hand]
  (let [undertricks (under-tricks hand)]
    {:over (apply + (take undertricks (penalty-score-scale-for-bid (:bid hand) (declarer-vulnerable hand))))
     :under 0 }))

(defn points-for-declarer [hand]
   (let [bid (:bid hand)
         level (:level bid)
         strain (:strain bid)
         doubled (:doubled bid)
         redoubled (:redoubled bid)
         declarer-tricks (declarer-tricks hand)
         vulnerable? (declarer-vulnerable hand)
         ]
     (score-for-declarer bid declarer-tricks vulnerable?)))

(defn score [hand]
  (let [declarer-points (points-for-declarer hand)
        defense-points (points-for-defender hand)]
    { (declarer-key hand) declarer-points (defense-key hand) defense-points}))
