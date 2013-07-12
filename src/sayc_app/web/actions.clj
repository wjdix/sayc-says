(ns sayc-app.web.actions
  (:use [sayc-app.bridge.deck :as deck]
        [sayc-app.web.views :as views]
        [sayc-app.bridge.scoring :as scoring])
  (:require [clojure.data.json :as json]))

(defn new-hand [request] (views/show-hand (first (deck/deal))))

(def rubbers {})

(defn create-bid [request]
  (let [params (:params request)
        hand (json/read-str (:hand params) :key-fn (fn [x] (keyword x)) :value-fn (fn [key val] (keyword val)))
        pass? (= (:bid params) "Pass")
        sayc-bid (sayc-app.bridge.bidding/open hand)]
    (if pass?
      (views/show-bid hand sayc-bid :pass)
      (let [level (Integer. (get-in request [:params :level]))
            strain (-> request :params :strain keyword)
            bid {:strain strain :level level}]
        (views/show-bid hand sayc-bid bid)))))

(defn new-scored-hand [request]
  (views/hand-form))

(defn declarer-as-keyword [hand]
  (update-in hand [:declarer] keyword))

(defn strain-as-keyword [hand]
  (update-in hand [:bid :strain] keyword))

(defn level-as-int [hand]
  (update-in hand [:bid :level] #(. Integer parseInt %)))

(defn tricks-as-ints [hand]
  (update-in (update-in hand [:tricks :we] #(. Integer parseInt %))
             [:tricks :they]
             #(. Integer parseInt %)))

(defn create-scored-hand [request]
  (let [hand (get-in request [:params :hand])]
    (views/show-score (sayc-app.bridge.scoring/score
           (-> hand declarer-as-keyword strain-as-keyword level-as-int tricks-as-ints)))))

