(ns sayc-app.web.actions
  (:use [sayc-app.bridge.deck :as deck]
        [sayc-app.web.views :as views])
  (:require [clojure.data.json :as json]))

(defn new-hand [request] (views/show-hand (first (deck/deal))))

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
