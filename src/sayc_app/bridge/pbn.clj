(ns sayc-app.bridge.pbn
  (:require [instaparse.core :as insta]))

(def hand
  (insta/parser
   "HAND = \"    \""
   ))
(def deal
  (insta/parser
   "DEAL = '[' DEAL_TOKEN '\"' HAND '.' HAND '.' HAND '.' HAND ']'
    DEAL_TOKEN = \"Deal \"
"
    ))
