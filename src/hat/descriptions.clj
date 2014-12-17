(ns hat.descriptions)

(defn generate-description [{:keys [singular-name singular-titlecase
                                    plural-name plural-titlecase
                                    fields
                                    conn db-entity-type db-search-attr]}]
  {;Singular resource
   :singular-class        singular-name

   :delete-action-name    (str "delete-" singular-name)
   :delete-action-title   (str "Delete " singular-titlecase)

   :replace-action-name   (str "replace-" singular-name)
   :replace-action-title  (str "Replace " singular-titlecase)
   :replace-action-fields fields

   ; Collection resource
   :collection-class      plural-name

   :add-action-name       (str "add-" singular-name)
   :add-action-title      (str "Add " singular-titlecase)
   :add-action-fields     fields

   :search-action-name    (str "search-" plural-name)
   :search-action-title   (str "Search " plural-titlecase)

   ; Entity
   :entity-classes        [singular-name]
   :entity-rels           [singular-name]

   ; Shared
   :collection-ref        (keyword plural-name)
   :singular-ref          (keyword singular-name)

   ; Database
   :conn                  conn
   :db-entity-type        db-entity-type
   :db-search-attr        db-search-attr

   ; Routes
   :routes-entries        {plural-name (keyword plural-name)
                           [(str plural-name "/") :id] (keyword singular-name)}

   ; Index
   :index-rel             [plural-name]})

