(ns sayc-app.system
  (:use [sayc-app.web])
  (:require [ring.adapter.jetty :as ring]))

(defn system []
  {:handler (sayc-app.web/create-handler)})

(defn start [system]
  (assoc system :server (ring/run-jetty (:handler system) {:port 8080 :join? false})))



(defn stop [system] (.stop (:server system)))
