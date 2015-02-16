(ns hat.controllers_test
  (:require [clojure.test :refer :all]
            [hat.common :refer [request]]))

(defn assert-ok [response]
  (is (= 200 (:status response)))
  response)

(defn body-contains [response pattern]
  (is (re-find pattern (:body response)))
  response)

(deftest index
  (-> (request :get "/")
      assert-ok
      (body-contains #"index")))

(deftest listing
  (-> (request :get "/hosts")
      assert-ok
      (body-contains #"Seed host")))

(deftest detail
  (-> (request :get "/hosts/1")
      assert-ok
      (body-contains #"Seed host")))

