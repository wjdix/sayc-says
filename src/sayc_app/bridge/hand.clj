(ns sayc-app.bridge.hand)

(def quote-map {'A :ace 'K :king 'Q :queen 'J :jack})

(def suit-mapping {'H :heart 'S :spade 'C :club 'D :diamond})

(def suit-symbols #{'H 'S 'C 'D})
(def card-symbols #{'A 'K 'Q 'J 10 9 8 7 6 5 4 3 2})

(defn parse-hand [symbols current grouped]
  (if (empty? symbols)
    grouped
    (let [head (first symbols)
          rest (rest symbols)]
      (cond
        (suit-symbols head) (parse-hand rest (get suit-mapping head) (conj grouped [(get suit-mapping head) []]))
        (card-symbols head) (parse-hand rest current (update-in grouped [current] #(conj % head)))))))

(defmacro hand [& cards]
  (let [parsed (parse-hand cards nil {})
        process-suit (fn [suit rank] (let [rank (get quote-map rank rank)] `(sayc-app.bridge.types/card ~suit ~rank)))]
    (into [] (apply concat
           (map (fn [[suit ranks]] (map #(process-suit suit %) ranks)) parsed)))))
