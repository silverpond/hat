; Need to remove the use of :db/id or generalise the name so that it's
; appropriate for use with all persistance
(ns hat.views
  (:require [hat.routes :refer [url-generator]]
            [hat.persistance.protocols :refer
             [Creatable Updateable Deleteable Searchable]]))

(defn add-entries-to-index [index entries]
  (update-in index [:links] #(concat % entries)))

(defn index []
  {:class "index"
   :links [{:rel ["self"]
            :href (url-generator :index)}]})

(defn index-entries [view-definition]
  [{:rel  (:index-rel view-definition)
    :href (url-generator (:collection-ref view-definition))}])

(defn singular [view-definition resource]
  (let [self-url (url-generator (:singular-ref view-definition)
                                :id (:db/id resource))]
    {:class (:singular-class view-definition)
     :properties resource
     :links [{:rel  ["self"]
              :href self-url}
             {:rel  ["listing"]
              :href (url-generator (:collection-ref view-definition))}]
     :actions
     (into []
           (filter identity
                   [(if (satisfies? Deleteable (:conn view-definition))
                      {:name   (:delete-action-name view-definition)
                       :title  (:delete-action-title view-definition)
                       :method "DELETE"
                       :href   self-url})
                    (if (satisfies? Deleteable (:conn view-definition))
                      {:name   (:replace-action-name view-definition)
                       :title  (:replace-action-title view-definition)
                       :method "PUT"
                       :href   self-url
                       :type   "application/edn"
                       :fields ((:replace-action-fields view-definition))})]))}))

(defn entity [view-definition resource]
  (let [self-url (url-generator (:singular-ref view-definition)
                                :id (:db/id resource))]
    {:class      (:entity-classes view-definition)
     :rel        (:entity-rels view-definition)
     :href       self-url
     :properties resource
     :actions
     (into []
           (filter identity
                   [(if (satisfies? Deleteable (:conn view-definition))
                      {:name   (:delete-action-name view-definition)
                       :title  (:delete-action-title view-definition)
                       :method "DELETE"
                       :href   self-url})
                    (if (satisfies? Updateable (:conn view-definition))
                      {:name   (:replace-action-name view-definition)
                       :title  (:replace-action-title view-definition)
                       :method "PUT"
                       :href   self-url
                       :type   "application/edn"
                       :fields ((:replace-action-fields view-definition))})]))
     :links [{:rel ["self"]
              :href self-url}]}))

(defn collection [view-definition resources]
  {:class    (:collection-class view-definition)
   :entities (map (partial entity view-definition) resources)
   :links [{:rel  ["self"]
            :href (url-generator (:collection-ref view-definition))}]
   :actions
   (into []
         (filter identity
                 [(if (satisfies? Creatable (:conn view-definition))
                    {:name   (:add-action-name view-definition)
                     :title  (:add-action-title view-definition)
                     :method "POST"
                     :href   (url-generator (:collection-ref view-definition))
                     :type   "application/edn"
                     :fields ((:add-action-fields view-definition))})
                  (if (satisfies? Searchable (:conn view-definition))
                    {:name   (:search-action-name view-definition)
                     :title  (:search-action-title view-definition)
                     :method "GET"
                     :href   (url-generator (:collection-ref view-definition))
                     :type   "application/x-www-form-urlencoded"
                     :fields [{:title "Query" :name :query :type :search}]})]))})

