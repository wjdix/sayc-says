(ns sayc-app.bridge.types
  (:require [clojure.spec :as s]))

(def minor-suits [::club ::diamond])
(def major-suits [::spade ::heart])
(def suits (concat major-suits minor-suits))
(def suit? (into #{} suits))
(s/def ::suit suit?)

(def strains (conj suits ::notrump))
(def strain? (into #{} strains))
(s/def ::strain strain?)

(def rank? (into #{:jack :queen :king :ace} (range 2 11)))
(def ranks (into [] rank?))

(s/def ::rank rank?)

(s/def ::card (s/keys :req [::rank ::suit]))

(def short-suits {:spade :sayc-app.bridge.types/spade
                  :heart :sayc-app.bridge.types/heart
                  :club :sayc-app.bridge.types/club
                  :diamond :sayc-app.bridge.types/diamond})

(def short-strains (conj short-suits {:notrump ::notrump}))

(defn card [suit rank]
  {::suit (get short-suits suit suit)
   ::rank rank})

(defn bid [level strain]
  {::level level
   ::strain (get short-strains strain strain)})

(s/def ::hand (and #(distinct? %)
                   (s/tuple ::card ::card ::card ::card
                            ::card ::card ::card ::card
                            ::card ::card ::card ::card ::card)))

(def levels (range 1 8))
(def level? (into #{} levels))
(s/def ::level (into #{} level?))
(s/def ::non-pass-bid (s/keys :req [::strain ::level]))

(s/def ::bid (s/or
              :true-bid ::non-pass-bid
              :pass-bid #(= :pass %)))
