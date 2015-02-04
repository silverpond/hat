(ns hat.persistance.datomic
  (:require [datomic.api :as d]
            [hat.persistance.protocols :refer :all]))

(defn seed [uri schema]
  (d/create-database uri)
  (let [conn (d/connect uri)]
    @(d/transact conn schema)
    conn))

(defn- id-of-created [transaction]
  (first (vals (:tempids transaction))))

(defn- actual-entity [db id]
  (let [datomic-entity (d/entity db id)]
    (if (keys datomic-entity)
      (-> (into {} datomic-entity)
          (assoc :db/id (:db/id datomic-entity))
          (assoc :hat/id (str (:db/id datomic-entity)))))))

(defn- entities-query [query db & sources]
  (for [[id] (apply d/q (concat [query db] sources))]
    (actual-entity db id)))

(extend-type datomic.peer.Connection
  Creatable
  (insert [conn data]
    (->> data
         (map #(assoc % :db/id #db/id[:db.part/user]))
         (d/transact conn)
         deref
         id-of-created))

  Deleteable
  (delete [conn id]
    @(d/transact conn [[:db.fn/retractEntity (Long/parseLong id)]]))

  Updateable
  (swap [conn id data]
    (delete conn id)
    (id-of-created @(d/transact conn [(assoc data :db/id (Long/parseLong id))])))

  Browseable
  (all-entities [conn type]
    (entities-query `[:find ?e :where [?e ~type]] (d/db conn)))

  (entity [conn type id]
    (actual-entity (d/db conn) (Long/parseLong id)))

  Searchable
  (search [conn query attr]
    (entities-query
      '[:find ?e
        :in $ ?attr ?query
        :where [(fulltext $ ?attr ?query) [[?e]]]]
      (d/db conn)
      attr
      query)))

