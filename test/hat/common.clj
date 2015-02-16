(ns hat.common
  (:require [hat.controllers :as c]
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
    seed-entity)
  
  Creatable
  (insert [conn data]
    "1"))

(def description
  (d/generate-description
    {:singular-name      "host"
     :singular-titlecase "Host"
     :plural-name        "hosts"
     :plural-titlecase   "Hosts"
     :conn               (TestConnection.)
     :db-search-attr     :host/name
     :fields
     #(do [{:title "Name" :name :host/name :type :text}])}))

(def handler
  (c/start (c/the-intermediate-step [description])))

(defn request [method uri]
  (handler {:scheme :http
            :request-method method
            :uri uri}))
