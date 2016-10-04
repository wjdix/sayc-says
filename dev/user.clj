(ns user
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.pprint :refer (pprint)]
            [clojure.repl :refer :all]
            [clojure.test :as test]
            [sayc-app.bridge.types :refer (card bid)]
            [clojure.tools.namespace.repl :refer (refresh refresh-all)]
            [sayc-app.system :as system]))

(def system nil)

(defn init
  "Constructs the current development system."
  []
  (alter-var-root #'system
    (constantly (system/system))))

(def balanced-hand [(card :spade :ace)
                    (card :spade :king)
                    (card :spade 5)
                    (card :spade 4)
                    (card :heart :ace)
                    (card :heart 10)
                    (card :heart 9)
                    (card :diamond :ace)
                    (card :diamond 10)
                    (card :diamond 8)
                    (card :club 4)
                    (card :club 3)
                    (card :club 2)])

(defn start
    "Starts the current development system."
    []
    (alter-var-root #'system system/start))

(defn stop
    "Shuts down and destroys the current development system."
    []
    (alter-var-root #'system
                        (fn [s] (when s (system/stop s)))))

(defn go
    "Initializes the current development system and starts it running."
    []
    (init)
    (start))

(defn reset []
    (stop)
    (refresh :after 'user/go))

(defn hand []
  (into [] (first (sayc-app.bridge.deck/deal))))

(defn points [hand]
  (clojure.core.logic/run 1 [q] (sayc-app.bridge.bido/hand-pointo hand q)))

(defn test-it []
  (let [test-hand (hand)]
    (println (points test-hand))
    (println (clojure.core.logic/run 1 [q] (sayc-app.bridge.bido/hand-distributo test-hand q)))
    (sayc-app.bridge.bidding/open test-hand)))
