(ns sayc-app.web.actions
  (:use [sayc-app.bridge.deck :as deck]
        [sayc-app.web.views :as views]
        [sayc-app.bridge.scoring :as scoring]
        [ring.util.response :only [redirect not-found]]
        )
  (:require [clojure.data.json :as json]))

(defn- new-uuid [] (str (java.util.UUID/randomUUID)))

(defn new-hand [request] (views/show-hand (first (deck/deal))))

(def chicago-games (atom {}))

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

(defn process-hand-params [hand-params]
  (-> hand-params declarer-as-keyword strain-as-keyword level-as-int tricks-as-ints))

(defn create-scored-hand [request]
  (let [hand (get-in request [:params :hand])]
    (views/show-score (sayc-app.bridge.scoring/score (process-hand-params hand)))))

(defn new-chicago-game [request] (views/chicago-form))

(defn create-chicago-game [request]
  (let [uuid (new-uuid)]
    (swap! chicago-games assoc uuid [])
    (redirect (str "/chicago_games/" uuid))))

(defn show-chicago-game [id]
  (let [game (get @chicago-games id)]
    (if game
      (views/display-chicago-game id game)
      (not-found "NOT FOUND"))))

(defn add-hand-to-chicago-game [request]
  (let [id (get-in request [:params :id])
        game (get @chicago-games id)
        hand (get-in request [:params :hand])]
    (if (not game) (not-found "NOT FOUND"))
    (swap! chicago-games assoc id (conj game (process-hand-params hand)))
    (redirect (str "/chicago_games/" id))))
