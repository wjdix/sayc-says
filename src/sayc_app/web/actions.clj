(ns sayc-app.web.actions
  (:use [sayc-app.bridge.deck :as deck]
        [sayc-app.web.views :as views]
        [sayc-app.bridge.scoring :as scoring]
        [sayc-app.bridge.types]
        [sayc-app.web.validation :as validate]
        [ring.util.response :only [redirect not-found]])
  (:require [clojure.data.json :as json]))

(defn- new-uuid [] (str (java.util.UUID/randomUUID)))

(defn new-hand [request] (views/show-hand (first (deck/deal))))

(def chicago-games (atom {}))

(def key-mapping
  {"suit" :sayc-app.bridge.types/suit
   "rank" :sayc-app.bridge.types/rank})

(def rank-map
  {"ace" :ace
   "king" :king
   "queen" :queen
   "jack" :jack})

(def suit-map
  {"club" :sayc-app.bridge.types/club
   "diamond" :sayc-app.bridge.types/diamond
   "heart" :sayc-app.bridge.types/heart
   "spade" :sayc-app.bridge.types/spade})

(def strain-map
  (conj suit-map {"notrump" :sayc-app.bridge.types/notrump}))

(defn transform-value [key val]
  (cond
    (= key :sayc-app.bridge.types/rank) (get rank-map val val)
    (= key :sayc-app.bridge.types/suit) (get suit-map val)))

(defn create-bid [request]
  (let [params (:params request)
        hand (json/read-str (:hand params) :key-fn (fn [x] (get key-mapping x)) :value-fn transform-value)
        pass? (= (:bid params) "Pass")
        sayc-bid (sayc-app.bridge.bidding/open hand)]
    (if pass?
      (views/show-bid hand sayc-bid :pass)
      (let [level (Integer. (get-in request [:params :level]))
            strain (-> request :params :strain strain-map)
            bid {:sayc-app.bridge.types/strain strain :sayc-app.bridge.types/level level}]
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
      (views/display-chicago-game id game {})
      (not-found "NOT FOUND"))))

(defn extract-params [request]
  {:id (get-in request [:params :id])
   :hand (get-in request [:params :hand])})

(defn enrich-with-prereqs [request]
  (assoc request :game (get @chicago-games (:id request))))

(defn validate-prereqs [prereqs]
  (assoc prereqs :errors (validate/hand (:hand prereqs))))

(defn derive-status [prereqs]
  (assoc prereqs :status
         (cond
           (nil? (:game prereqs)) 404
           (not (empty? (:errors prereqs))) 422
           :else 201)))

(defn persist [request]
  (let [new-game (conj (:game request) (process-hand-params (:hand request)))]
    (swap! chicago-games assoc (:id request) new-game)
    (assoc request :game new-game)))

(defn conditionally-persist [request]
  (if (= (:status request) 201)
    (persist request)
    request))

(defn render [request]
  (cond
    (= (:status request) 404) (not-found "NOT FOUND")
    :else (views/display-chicago-game (:id request) (:game request) (:errors request))))

(defn add-hand-to-chicago-game [request]
  (-> request extract-params enrich-with-prereqs validate-prereqs
      derive-status conditionally-persist render))
