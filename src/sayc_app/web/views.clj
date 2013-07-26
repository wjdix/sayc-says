(ns sayc-app.web.views
  (:require [clojure.string :as string]
            [clojure.data.json :as json]
            [hiccup.core :as hiccup]
            [hiccup.form :as form]
            [sayc-app.bridge.bidding]
            [sayc-app.bridge.scoring]
            )
  (:use [hiccup.page :only [include-css]]
        [sayc-app.bridge.deck :only [card-rank suit-rank]]))

(def glyph-for-suit {:diamond "&diams;" :club "&clubs;" :heart "&hearts;" :spade "&spades;"})
(def color-for-suit {:diamond "red" :heart "red" })
(def display-strain {:diamond "D" :club "C" :heart "H" :spade "S" :notrump "NT"})


(defn- display-card [{rank :rank}]
  (rank {:ace "A" :king "K" :queen "Q" :jack "J"} (name rank)))

(defmulti display-bid class)
(defmethod display-bid nil [_] "")
(defmethod display-bid clojure.lang.Keyword [pass] "Pass")
(defmethod display-bid clojure.lang.PersistentArrayMap [{level :level strain :strain}]
  (str level (strain display-strain)))

(defn option-for-symbol [value]
  [:option {:value (name value)} (name value)])

(defn option-for-int [value]
  [:option {:value (str value)} (str value)])

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

(defn layout [& contains]
  (hiccup/html
    [:head
      (include-css "/css/bridge.css")
      (include-css "/css/bootstrap/css/bootstrap.css")
     [:style {:type "text/css"} "body { padding-top: 80px; }"]
     ]
    [:body
     [:div.navbar.navbar-fixed-top
      [:div.navbar-inner
       [:div.container
         [:span [:a.brand "SAYC Says"]]
         [:ul.nav
           [:li [:a {:href "/"} "Practice Bidding"]]
           [:li [:a {:href "/chicago_games/new"} "Chicago Match"]]]
        ]]]
     [:div.container contains]]))

(defn show-hand [hand]
  (layout (hiccup/html
    [:h1 "Bid This Hand"]
     (display-hand hand)
     [:form {:action "/bids" :method "POST"}
      [:input {:type "hidden" :name "hand" :value (json/write-str hand)}]
      [:select {:name "strain"} (map option-for-symbol sayc-app.bridge.bidding/strains)]
      [:select {:name "level"} (map option-for-int sayc-app.bridge.bidding/levels)]
      [:input {:type "submit" :value "Bid"}]]
    [:form {:action "/bids" :method "POST"}
     [:input {:type "hidden" :name "hand" :value (json/write-str hand)}]
     [:input {:type "hidden" :name "bid" :value "Pass"}]
     [:input {:type "submit" :value "Pass"}] ]
    )))

(defn show-bid [hand sayc-bid user-bid]
  (layout
    [:h1 "SAYC Says"]
     (display-hand hand)
     [:div.sayc-bid (str "SAYC says: " (display-bid sayc-bid))]
     [:div.user-bid (str "You bid: " (display-bid user-bid))]
     [:a {:href "/hands/new"} "View a New Hand"]))

(defn nested-field-name [fields] (apply str (first fields) (map #(str "[" % "]") (rest fields))))

(defn- labelled-text-field [label fields errors]
  (let [field-name (nested-field-name fields)
        error-location (map keyword fields)
        errors-for-field (get-in errors (rest error-location))]
    (hiccup/html
      [:label {:for field-name} label]
      (if (not (nil? errors)) [:div.errors (str errors-for-field)])
      [:input {:type "text" :name field-name}])))

(defn- labelled-checkbox [label fields errors]
  (let [field-name (nested-field-name fields)
        error-location (map keyword fields)
        errors (get-in errors (rest error-location)) ]
    (hiccup/html
      [:label {:for field-name} label]
      (if (not (nil? errors)) [:div.errors (str errors)])
      [:div.errors ]
      [:input {:type "checkbox" :name field-name}])))

(defn new-hand-form [route errors]
  (hiccup/html
    [:form {:action route :method "POST"}
     [:label {:for "hand[declarer]"} "Declarer"]
     [:select {:name "hand[declarer]"} (map option-for-symbol sayc-app.bridge.scoring/declarers)]
     [:label {:for "hand[vulnerable]"} "Vulnerable"]
     [:select {:name "hand[vulnerable]"} (map option-for-symbol sayc-app.bridge.scoring/vulnerabilities)]
     [:br]
     [:h4 "Bid"]
     [:label {:for "hand[bid][level]"} "Level"]
     [:select {:name "hand[bid][level]"} (map option-for-int sayc-app.bridge.bidding/levels)]
     [:label {:for "hand[bid][strain]"} "Strain"]
     [:select {:name "hand[bid][strain]"} (map option-for-symbol sayc-app.bridge.bidding/strains)]
     (labelled-checkbox "Doubled?" ["hand" "bid" "doubled"] errors)
     (labelled-checkbox "Redoubled?" ["hand" "bid" "redoubled"] errors)
     [:h4 "Tricks Taken"]
     (labelled-text-field "We" ["hand" "tricks" "we"] errors)
     (labelled-text-field "They" ["hand" "tricks" "they"] errors)
     [:br]
     [:input {:type "submit" :value "Score"}]]))

(defn hand-form []
  (layout (new-hand-form "/scored_hands" nil)))

(defn display-score [score]
  (hiccup/html
    [:table
     [:tr [:th ""] [:th "We"] [:th "They"]]
     [:tr [:th "Over"] [:td (get-in score [:we :over])] [:td (get-in score [:they :over])]]
     [:tr [:th "Under"] [:td (get-in score [:we :under])] [:td (get-in score [:they :under])]]]))

(defn show-score [score]
  (layout [:h2 "Score"] (display-score score)))

(defn chicago-form []
  (layout
    [:h2 "New Chicago Game"]
    [:form {:action "/chicago_games" :method "POST"} [:input {:type "submit" :value "Start"}]]))

(defn- header-for-game [game]
  (if (= 4 (count game)) "Game Completed" "Game In Progress"))

(defn display-scored-hand [hand]
  (hiccup/html
    [:div.row
     [:div.span6 [:h4 "Bid"] [:p (str (:declarer hand) " bid " (display-bid (:bid hand)))]]
     [:div.span6 [:h4 "Score"] (display-score (sayc-app.bridge.scoring/score hand))]]))

(defn display-total-score [game]
  (if (= 4 (count game))
    (hiccup/html [:h4 "Total Score"] [:div (display-score (apply sayc-app.bridge.scoring/add-score (map sayc-app.bridge.scoring/score game)))])
    ))

(defn display-chicago-game [id game errors]
  (layout
     [:h2 (header-for-game game)]
     [:br]
     (map display-scored-hand game)
     [:br]
     (display-total-score game)
     (new-hand-form (str "/chicago_games/" id "/hands") errors)))
