(ns sayc-app.web
  (:use [compojure.core :only [defroutes GET POST]]
        [compojure.route :only [resources]]
        [ring.middleware.stacktrace :as st]
        [ring.middleware.params :as params]
        [ring.middleware.keyword-params :as keyword-params]
        [ring.middleware.nested-params :as nested-params]
        [sayc-app.bridge.deck :as deck]
        [sayc-app.web.actions :as actions])
  (:require [ring.adapter.jetty]))

(defroutes routes
  (GET "/" [] actions/new-hand)
  (GET "/hands/new" [] actions/new-hand)
  (POST "/bids" [] actions/create-bid)
  (resources "/"))

(defn create-handler []
  (-> (var routes)
      (keyword-params/wrap-keyword-params)
      (params/wrap-params)
      (st/wrap-stacktrace)))
