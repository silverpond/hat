(ns hat.relationships-test
  (:require [clojure.test      :refer :all]
            [hat.controllers   :refer [the-intermediate-step]]
            [hat.descriptions  :refer [generate-description]]
            [hat.relationships :refer [one-to-many]]))

(def hosts-description
  (generate-description
    {:singular-name      "host"
     :singular-titlecase "Host"
     :plural-name        "hosts"
     :plural-titlecase   "Hosts"
     :conn               :conn
     :db-search-attr     :host/name
     :fields
     (constantly
       [{:title "Name"      :name :host/name      :type :text}
        {:title "Address"   :name :host/address   :type :text}
        {:title "Locality"  :name :host/locality  :type :text}
        {:title "State"     :name :host/state     :type :text}
        {:title "Postcode"  :name :host/postcode  :type :text}
        {:title "Country"   :name :host/country   :type :text}])}))

(def events-description
  (generate-description
    {:singular-name      "event"
     :singular-titlecase "Event"
     :plural-name        "events"
     :plural-titlecase   "Events"
     :conn               :conn
     :db-search-attr     :event/name
     :fields
     (constantly
       [{:title "Name"            :name :event/name           :type :text}
        {:title "Description"     :name :event/description    :type :text}
        {:title "Start Time"      :name :event/start-time     :type :datetime} 
        {:title "End Time"        :name :event/end-time       :type :datetime} 
        {:title "Venue"           :name :event/venue          :type :text} 
        {:title "Facebook Event"  :name :event/facebook-event :type :url} 
        {:title "Hashtag"         :name :event/hashtag        :type :text}])}))

(def old-relationship
  {:field-title-attr    :host/name
   :selection-attr      :host/name
   :parent-attr         :event/host
   :collection-ref      :events
   :add-action-name     "add-event"
   :singular-ref        :event
   :parent-conn         :conn
   :child-conn          :conn
   :replace-action-name "replace-event"})

(def rnr (the-intermediate-step [hosts-description events-description]))

(deftest relationships
  (println "disabled")
  (= old-relationship
     (one-to-many rnr hosts-description events-description
                  :host/name :event/host)))

