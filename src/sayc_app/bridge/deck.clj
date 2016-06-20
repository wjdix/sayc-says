(ns sayc-app.bridge.deck
  (:require [clojure.spec :as s]
            [sayc-app.bridge.types :refer [rank? suit?]]))

(def deck (for [suit suit? rank rank?]
            {:sayc-app.bridge.types/rank rank :sayc-app.bridge.types/suit suit}))

(s/fdef deal
        :args (s/spec empty?)
        :ret (s/tuple
              :sayc-app.brdige.types/hand
              :sayc-app.bridge.types/hand
              :sayc-app.bridge.types/hand
              :sayc-app.bridge.types/hand))

(defn deal []
  (into [] (map #(into [] %) (partition 13 (shuffle deck)))))
