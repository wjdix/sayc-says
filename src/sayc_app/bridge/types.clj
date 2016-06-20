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
