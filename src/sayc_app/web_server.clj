(ns sayc-app.web_server
  (:require [sayc-app.web])
  (:use [ring.adapter.jetty]))

(defn -main []
    (let [port (Integer/parseInt (System/getenv "PORT"))]
          (run-jetty (sayc-app.web/create-handler) {:port port})))

