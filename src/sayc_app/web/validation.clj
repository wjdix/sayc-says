(ns sayc-app.web.validation
  (:use [metis.core]))

(defvalidator :tricks-validator
  [[:we :they] :formatted {:pattern #"^[0-9]+$" :message "must be numeric"}])

(defvalidator :hand
  [[:tricks] :tricks-validator])
