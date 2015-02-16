(ns hat.validation
  (:require [vlad.validation-types :refer [validate]]))

; TODO: This pattern can probably be used for auth as well
(defn flatten-rules [rules]
  (->> rules
       (mapcat (fn [[resource methods]]
                 (map (fn [[method validator]]
                        [[resource method] validator])
                      methods)))
       (into {})))

(defn insert-valitor [rnr [[resource method] validator]]
  (update-in rnr [:resources resource :processable?]
             (fn [processable]
               (fn [req]
                 (if (= method (get-in req [:request :request-method]))
                   (let [errors (validate validator (:hat.resources/body req))]
                     [(empty? errors) ::errors errors])
                   (if (not (nil? processable)) (processable req)))))))

(defn install-validations [rnr rules]
  (->> rules
      flatten-rules
      (reduce insert-valitor rnr)))
