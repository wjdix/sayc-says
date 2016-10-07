(ns sayc-app.bridge.bidding
  (:require [sayc-app.bridge.bido :refer [openo respondo]]
            [clojure.core.logic :refer [run]]
            [clojure.spec :as s]))

(s/fdef open
        :args (s/cat :hand :sayc-app.bridge.types/hand)
        :ret :sayc-app.bridge.types/bid)

(defn open [hand]
  (first (run 1 [q] (openo hand q))))

(s/fdef respond
        :args (s/cat :hand :sayc-app.bridge.types/hand :partner-bid :sayc-app.bridge.types/non-pass-bid)
        :ret :sayc-app.bridge.types/bid)

(defn respond [hand partner-bid]
  (first (run 1 [q] (respondo hand partner-bid q))))
