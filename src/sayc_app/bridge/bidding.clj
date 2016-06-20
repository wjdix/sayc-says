(ns sayc-app.bridge.bidding
  (:require [sayc-app.bridge.bido :refer [openo]]
            [clojure.core.logic :refer [run]]
            [clojure.spec :as s]))

(s/fdef open
        :args (s/cat :hand :sayc-app.bridge.types/hand)
        :ret :sayc-app.bridge.types/bid)

(defn open [hand]
  (first (run 1 [q] (openo hand q))))
