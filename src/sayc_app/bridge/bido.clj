(ns sayc-app.bridge.bido
  (:refer-clojure :exclude [==])
  (:require [clojure.core.logic.fd :as fd]
            [sayc-app.bridge.deck :as deck]
            [clojure.core.logic :refer :all]
            [clojure.spec :as s]))

(defn- not-membero [x l]
  (fresh [head tail]
         (conde
          [(== l [])]
          [(conso head tail l)
           (!= x head)
           (not-membero x tail)])))

(defnu lengtho [l n]
  ([[] 0])
  ([[_ . ?rst] _]
   (fresh [n1]
          (lengtho ?rst n1)
          (project [n n1]
                   (== n (+ n1 1))))))

(defn ranko [card rank]
  (featurec card {:sayc-app.bridge.types/rank rank}))

(defn suito [card suit]
  (featurec card {:sayc-app.bridge.types/suit suit}))

(defn cardo [rank suit card]
  (== {:sayc-app.bridge.types/rank rank :sayc-app.bridge.types/suit suit} card)
  (membero rank sayc-app.bridge.types/ranks)
  (membero suit sayc-app.bridge.types/suits))

(defn card? [card]
  (s/conform :sayc-app.bridge.types/card card))

(def face-cards [:jack :queen :king :ace])

(defn pointo [rank point]
  (conde
   [(== rank :jack)               (== point 1)]
   [(== rank :queen)              (== point 2)]
   [(== rank :king)               (== point 3)]
   [(== rank :ace)                (== point 4)]
   [(not-membero rank face-cards) (== point 0)]))

(defn card-pointo [card point]
  (fresh [r]
         (ranko card r)
         (pointo r point)))

(defn hand-pointo [hand points]
  (conde
   [(== hand ()) (== points 0)]
   [(fresh [card points-of-card t points-of-t]
           (conso card t hand)
           (card-pointo card points-of-card)
           (fd/+ points-of-card points-of-t points)
           (hand-pointo t points-of-t))]))

(defn count-points [hand]
  (first (run 1 [q]
              (hand-pointo hand q))))

(defn suit-counto [hand suit count]
  (conde
   [(== hand ()) (== count 0)]
   [(fresh [card suit-of-card t count-of-t]
           (conso card t hand)
           (suito card suit-of-card)
           (suit-counto t suit count-of-t)
           (conde
            [(== suit suit-of-card)
             (fd/+ 1 count-of-t count)]
            [(!= suit suit-of-card)
             (fd/+ 0 count-of-t count)]))]))

(defn hand-distributo [hand distribution]
  (fresh [heart-count spade-count diamond-count club-count]
         (suit-counto hand :sayc-app.bridge.types/heart heart-count)
         (suit-counto hand :sayc-app.bridge.types/spade spade-count)
         (suit-counto hand :sayc-app.bridge.types/diamond diamond-count)
         (suit-counto hand :sayc-app.bridge.types/club club-count)
         (== distribution {:sayc-app.bridge.types/heart heart-count
                           :sayc-app.bridge.types/club club-count
                           :sayc-app.bridge.types/diamond diamond-count
                           :sayc-app.bridge.types/spade spade-count})))

(defn more-thano [hand count suit]
  (fresh [suit-count]
         (membero suit sayc-app.bridge.types/suits)
         (suit-counto hand suit suit-count)
         (fd/>= suit-count count)))

(defn more-than-fouro [hand suit]
  (more-thano hand 4 suit))

(defn more-than-fiveo [hand suit]
  (more-thano hand 5 suit))

(defn more-than-sixo [hand suit]
  (more-thano hand 6 suit))

(defn bido [strain level bid]
  (membero strain sayc-app.bridge.types/strains)
  (membero level sayc-app.bridge.types/levels)
  (== {:sayc-app.bridge.types/strain strain :sayc-app.bridge.types/level level} bid))

(defn at-least-pointso [hand points]
  (fresh [hcp]
         (hand-pointo hand hcp)
         (fd/>= hcp points)))

(defn between-pointso [hand low high]
  (fresh [hcp]
         (hand-pointo hand hcp)
         (fd/<= hcp high)
         (fd/>= hcp low)))

(defn betweeno [n l h]
  (fd/<= n h)
  (fd/>= n l))

(defn balanced-distributo [distribution]
  (fresh [sc hc cc dc]
         (featurec distribution {:sayc-app.bridge.types/spade sc
                                 :sayc-app.bridge.types/club cc
                                 :sayc-app.bridge.types/diamond dc
                                 :sayc-app.bridge.types/heart hc})
         (betweeno sc 2 5)
         (betweeno hc 2 5)
         (betweeno cc 2 5)
         (betweeno sc 2 5)))

(defn balanced? [hand]
  (fresh [d]
         (hand-distributo hand d)
         (balanced-distributo d)))

(defn strong-two-clubso [hand bid]
  (conde
   [(at-least-pointso hand 22) (bido :sayc-app.bridge.types/club 2 bid)]
   [(== 1 0)]))

(defn five-card-majoro [hand bid]
  (fresh [suit]
         (membero suit sayc-app.bridge.types/major-suits)
         (more-than-fiveo hand suit)
         (at-least-pointso hand 13)
         (bido suit 1 bid)))

(defn four-card-minoro [hand bid]
  (conde
   [(fresh [suit]
           (membero suit sayc-app.bridge.types/minor-suits)
           (more-than-fouro hand suit)
           (at-least-pointso hand 13)
           (bido suit 1 bid))]
   [(== 1 0)]))

(defn no-trumpo [hand bid]
  (conde
   [(balanced? hand)
    (between-pointso hand 15 17)
    (bido :sayc-app.bridge.types/notrump 1 bid)]
   [(== 1 0)]))

(defn strong-two-no-trumpo [hand bid]
  (conde
   [(balanced? hand)
    (between-pointso hand 20 22)
    (bido :sayc-app.bridge.types/notrump 2 bid)]
   [(== 1 0)]))

(defn weak-twoso [hand bid]
  (let [weak-two-suits [:sayc-app.bridge.types/heart
                        :sayc-app.bridge.types/diamond
                        :sayc-app.bridge.types/spade]]
    (conde
     [(fresh [suit]
             (between-pointso hand 5 11)
             (membero suit weak-two-suits)
             (more-than-sixo hand suit)
             (bido suit 2  bid))]
     [(== 1 0)])))

(defn openo [hand bid]
  (condu
   [(strong-two-no-trumpo hand bid)]
   [(strong-two-clubso hand bid)]
   [(weak-twoso hand bid)]
   [(five-card-majoro hand bid)]
   [(no-trumpo hand bid)]
   [(four-card-minoro hand bid)]
   [(== bid :pass)]))
