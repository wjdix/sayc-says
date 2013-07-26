(ns sayc-app.web.validation-test
  (:require [clojure.test :refer :all]
            [sayc-app.web.validation :refer :all]))


(deftest trick-validation
  (testing "validates numeric"
    (is (= {:they ["must be numeric"]} (tricks-validator {:we "7" :they "hello"})))
    (is (= {:we ["must be numeric"]} (tricks-validator {:we "" :they "7"})))))

(deftest hand-validation
  (testing "includes trick validations"
    (let [test-hand {:tricks {:we "" :they "6"}}
          errors (hand test-hand)]
     (is (= ["must be numeric"] (get-in errors [:tricks :we]))))))

