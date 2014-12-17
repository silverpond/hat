(ns hat.relationships
  (:require [hat.controllers           :as controllers]
            [hat.persistance.protocols :as db]))

(defn add-fields [response action-name fields]
  (let [actions (:actions response)
        action  (first (filter #(= action-name (:name %)) actions))
        index   (.indexOf actions action)]
    (update-in response [:actions index :fields] concat fields)))

(defn modify-output [r-n-r ref step f]
  (update-in r-n-r [:resources ref step]
             (fn [handler]
               (fn [ctx]
                 (f (handler ctx))))))

(defn add-resource [r-n-r relationship]
  (modify-output
    r-n-r (:collection-ref relationship) :handle-ok
    (fn [response]
      (let [parent-entities (db/all-entities (:parent-conn relationship)
                                             (:selection-attr relationship))]
        (add-fields
          response (:add-action-name relationship)
          (map #(do {:name  (:parent-attr relationship)
                     :type  :radio
                     :value (:db/id %)
                     :title ((:field-title-attr relationship) %)})
               parent-entities))))))

(defn replace-resource [r-n-r relationship]
  (update-in r-n-r [:resources (:singular-ref relationship) :handle-ok]
    (fn [handler]
      (fn [ctx]
        (let [parent          ((:parent-attr relationship)
                               (:clj-admin.resources/resource ctx))
              response        (handler ctx)
              parent-entities (db/all-entities (:parent-conn relationship)
                                               (:selection-attr relationship))]
          (add-fields
            response (:replace-action-name relationship)
            (map #(do {:name (:parent-attr relationship)
                       :type :radio
                       :value (:db/id %)
                       :title ((:field-title-attr relationship) %)
                       :checked (= (:db/id parent) (:db/id %))})
                 parent-entities)))))))


(defn install-relationship [rnr relationship]
  (-> rnr
      (add-resource     relationship)
      (replace-resource relationship)))

(defn one-to-many [rnr one many field-title-attr parent-attr]
  (let [many-subset (select-keys many [:collection-ref :add-action-name
                                       :singular-ref   :replace-action-name])]
    (install-relationship
      rnr
      (assoc many-subset
             :field-title-attr  field-title-attr
             :selection-attr    (:db-entity-type one)
             :parent-attr       parent-attr
             :parent-conn       (:conn one)
             :child-conn        (:conn many)))))

