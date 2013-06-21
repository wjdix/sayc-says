(ns sayc-app.web.views
  (:require [clojure.string :as string]
            [clojure.data.json :as json]
            [hiccup.core :as hiccup]
            [hiccup.form :as form]
            [sayc-app.bridge.bidding])
  (:use [hiccup.page :only [include-css]]
        [sayc-app.bridge.deck :only [card-rank suit-rank]]))

(def glyph-for-suit {:diamond "&diams;" :club "&clubs;" :heart "&hearts;" :spade "&spades;"})
(def color-for-suit {:diamond "red" :heart "red" })
(def display-strain {:diamond "D" :club "C" :heart "H" :spade "S" :notrump "NT"})


(defn- display-card [{rank :rank}]
  (rank {:ace "A" :king "K" :queen "Q" :jack "J"} (name rank)))

(defmulti display-bid class)
(defmethod display-bid clojure.lang.Keyword [pass] "Pass")
(defmethod display-bid clojure.lang.PersistentArrayMap [{level :level strain :strain}]
  (str level (strain display-strain)))

(defn option-for-strain [strain]
  [:option {:value (name strain)} (name strain)])

(defn option-for-level [level]
  [:option {:value (str level)} (str level)])

(defn suit-div [[suit cards]]
  (hiccup/html
    [:div {:class (str "holding " (name suit))}
     [:span {:class (str "suitsymbol " (suit color-for-suit))} (suit glyph-for-suit)] (string/join " " (map display-card (sort-by card-rank cards)))]))

(defn- display-hand [hand]
  (hiccup/html
    [:div.diagram
     [:div.hand.north
      (map suit-div (sort-by #(suit-rank (first %)) (group-by :suit hand)))]]
    )
  )

(defn show-hand [hand]
  (hiccup/html
    [:head
      (include-css "/css/bridge.css") ]
    [:body [:h1 "Bid This Hand"]
     (display-hand hand)
     [:form {:action "/bids" :method "POST"}
      [:input {:type "hidden" :name "hand" :value (json/write-str hand)}]
      [:select {:name "strain"} (map option-for-strain sayc-app.bridge.bidding/strains)]
      [:select {:name "level"} (map option-for-level sayc-app.bridge.bidding/levels)]
      [:input {:type "submit" :value "Bid"}]]]
    [:form {:action "/bids" :method "POST"}
     [:input {:type "hidden" :name "hand" :value (json/write-str hand)}]
     [:input {:type "hidden" :name "bid" :value "Pass"}]
     [:input {:type "submit" :value "Pass"}] ]
    ))

(defn show-bid [hand sayc-bid user-bid]
  (hiccup/html
    [:head
     (include-css "/css/bridge.css")]
    [:body [:h1 "SAYC Says"]
     (display-hand hand)
     [:div.sayc-bid (str "SAYC says: " (display-bid sayc-bid))]
     [:div.user-bid (str "You bid: " (display-bid user-bid))] 
     [:a {:href "/hands/new"} "View a New Hand"] 
     ]
  ))
