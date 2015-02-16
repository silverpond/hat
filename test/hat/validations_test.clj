(ns hat.validations_test
  (:require [clojure.test :refer :all]
            [vlad.core :refer :all]
            [hat.common :refer [description]]
            [hat.validation :as v]
            [hat.controllers :as c]
            [ring.util.io :as rio]))

(def age-required
  (equals-value 1 [:age]))

(def validation-rules
  {:hosts {:put  age-required
           :post age-required}})

; TODO: Parameterise and unify with other handler.
(def handler
  (-> [description]
      c/the-intermediate-step
      (v/install-validations validation-rules)
      c/start))

; TODO: Unify with other implementation.
(defn request [method uri body]
  (handler {:scheme         :http
            :request-method method
            :uri            uri
            :body           (rio/string-input-stream body)}))

(deftest should-allow-creation-of-valid-object
  (is (= 303 (:status (request :post "/hosts" "{:age 1}")))))

(deftest should-block-creation-of-invalid-object
  (is (= 422 (:status (request :post "/hosts" "{:rage 1}")))))

