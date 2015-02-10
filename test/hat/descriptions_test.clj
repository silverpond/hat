(ns hat.descriptions-test
  (:require [clojure.test           :refer :all]
            [hat.descriptions :refer [generate-description]]))

(def description
  (generate-description {:singular-name      "host"
                         :singular-titlecase "Host"
                         :plural-name        "hosts"
                         :plural-titlecase   "Hosts"
                         :conn               :conn
                         :db-search-attr     :host/name
                         :fields
                         [{:title "Name" :name :host/name :type :text}]}))

(deftest expansion
  (is (= {;Singular resource
          :singular-class        "host"

          :delete-action-name    "delete-host"
          :delete-action-title   "Delete Host"

          :replace-action-name   "replace-host"
          :replace-action-title  "Replace Host"
          :replace-action-fields [{:title "Name" :name :host/name :type :text}]

          ; Collection resource
          :collection-class      "hosts"

          :add-action-name       "add-host"
          :add-action-title      "Add Host"
          :add-action-fields     [{:title "Name" :name :host/name :type :text}]

          :search-action-name    "search-hosts"
          :search-action-title   "Search Hosts"

          ; Entity
          :entity-classes        ["host"]
          :entity-rels           ["host"]

          ; Shared
          :collection-ref        :hosts
          :singular-ref          :host

          ; Database
          :conn                  :conn
          :db-search-attr        :host/name

          ; Routes
          :routes-entries        {"hosts" :hosts
                                  ["hosts/" :id] :host}

          ; Index
          :index-rel             ["hosts"]}
         description)))
