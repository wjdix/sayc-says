(ns sayc-app.bridge.bido
  (:refer-clojure :exclude [==])
  (:use [clojure.core.logic])
  (:require [clojure.core.logic.fd :as fd]))

(def suits [:spades :hearts :clubs :diamonds])
(def minor-suits [:clubs :diamonds])
(def major-suits [:spades :hearts])
(def ranks [:2 :3 :4 :5 :6 :7 :8 :9 :10 :jack :queen :king :ace])

(defn ranko [card rank]
  (featurec card {:rank rank}))

(defn suito [card suit]
  (featurec card {:suit suit}))

(defn card? [card]
  (fresh [s r]
         (all
          (membero r ranks)
          (membero s suits)
          (ranko card r)
          (suito card s))))

(defn- not-membero [x l]
  (fresh [head tail]
         (conde
          [(== l [])]
          [(conso head tail l)
           (!= x head)
           (not-membero x tail)])))

(def face-cards [:jack :queen :king :ace])

(defn pointo [rank point]
  (conde
   [(== rank :jack)                             (== point 1)]
   [(== rank :queen)                            (== point 2)]
   [(== rank :king)                             (== point 3)]
   [(== rank :ace)                              (== point 4)]
   [(not-membero rank face-cards)               (== point 0)]))

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
         (suit-counto hand :hearts heart-count)
         (suit-counto hand :spades spade-count)
         (suit-counto hand :diamonds diamond-count)
         (suit-counto hand :clubs club-count)
         (== distribution {:hearts heart-count :clubs club-count :diamonds diamond-count :spades spade-count})))

(defn more-thano [hand count suit]
  (fresh [suit-count]
         (membero suit suits)
         (suit-counto hand suit suit-count)
         (fd/>= suit-count count)))

(defn more-than-fouro [hand suit]
  (more-thano hand 4 suit))

(defn more-than-fiveo [hand suit]
  (more-thano hand 5 suit))

(defn bido [strain level bid]
  (== {:strain strain :level level} bid))

(defn at-least-pointso [hand points]
  (fresh [hcp]
         (hand-pointo hand hcp)
         (fd/>= hcp points)))

(defn between-pointso [hand low high]
  (fresh [hcp]
         (hand-pointo hand hcp)
         (fd/<= hcp high)
         (fd/>= hcp low)))

(defn balanced? [hand]
  (fresh [distribution spade-count heart-count club-count diamond-count]
         (hand-distributo hand distribution)
         (== spade-count   (:spades distribution))
         (== heart-count   (:hearts distribution))
         (== club-count    (:clubs distribution))
         (== diamond-count (:diamonds distribution))

         (fd/< 2 heart-count)   (fd/>= 5 heart-count)
         (fd/< 2 diamond-count) (fd/>= 5 diamond-count)
         (fd/< 2 club-count)    (fd/>= 5 club-count)
         (fd/< 2 spade-count)   (fd/>= 5 spade-count)))

(defn five-card-majoro [hand bid]
  (fresh [suit]
         (membero suit major-suits)
         (more-than-fiveo hand suit)
         (at-least-pointso hand 13)
         (bido suit 1 bid)))

(defn four-card-minoro [hand bid]
  (fresh [suit]
         (membero suit minor-suits)
         (more-than-fouro hand suit)
         (at-least-pointso hand 13)
         (bido suit 1 bid)))

(defn no-trumpo [hand bid]
  (balanced? hand)
  (between-pointso hand 15 17)
  (bido :notrump 1 bid))

(defn openo [hand bid]
  (condu
   [(five-card-majoro hand bid)]
   [(no-trumpo hand bid)]
   [(four-card-minoro hand bid)]))
