(ns hat.controllers_test
  (:require [clojure.test :refer :all]
            [hat.controllers :as c]
            [hat.descriptions :as d]
            [hat.persistance.protocols :refer :all]))

(def seed-entity
  {:hat/id "1"
   :host/name "Seed host"})

(defrecord TestConnection []
  Browseable
  (all-entities [conn]
    [seed-entity])
  (entity [conn id]
    seed-entity))

(def description
  (d/generate-description
    {:singular-name      "host"
     :singular-titlecase "Host"
     :plural-name        "hosts"
     :plural-titlecase   "Hosts"
     :conn               (TestConnection.)
     :db-search-attr     :host/name
     :fields
     [{:title "Name" :name :host/name :type :text}]}))

(def handler
  (c/start (c/the-intermediate-step [description])))

(defn request [method uri]
  (handler {:scheme :http
            :request-method method
            :uri uri}))

(defn assert-ok [response]
  (is (= 200 (:status response)))
  response)

(defn body-contains [response pattern]
  (is (re-find pattern (:body response)))
  response)

(deftest index
  (-> (request :get "/")
      assert-ok))

(deftest listing
  (-> (request :get "/hosts")
      assert-ok
      (body-contains #"Seed host")))

(deftest detail
  (-> (request :get "/hosts/1")
      assert-ok
      (body-contains #"Seed host")))
