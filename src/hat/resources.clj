(ns hat.resources
  (:require [hat.persistance.protocols :as db]
            [hat.views                 :as v]
            [clojure.edn               :as edn]
            [hat.routes                :refer [router url-generator]])
  (:import java.io.PushbackReader))

(defn- id [ctx]
  (get-in ctx [:request :route-params :id]))

(defn- edn-body [request]
  (-> request :body clojure.java.io/reader PushbackReader. edn/read))

(defn- get? [request]
  (= :get (get-in request [:request :request-method])))

(def available-media-types
  ["text/plain" "application/edn" "application/json"])

(defn make-index [descriptions]
  {:index
     {:allowed-methods [:options :get]
      :available-media-types available-media-types

      :handle-ok (fn [_] (reduce v/add-entries-to-index (v/index)
                                         (map v/index-entries descriptions)))}})

(defn make-admin-area [{:keys [conn] :as description}]
  {(:collection-ref description)
     {:allowed-methods [:options :get :post]
      :available-media-types available-media-types

      :post! #(do {::id (db/insert conn [(edn-body (:request %))])})
      :post-redirect? #(do {:location (url-generator (:singular-ref description)
                                                     :id (::id %))})

      :malformed?
      #(if (get? %)
         [false {::resource
                 (if-let [query (get-in % [:request :query-params "query"])]
                   (db/search conn query (:db-search-attr description))
                   (db/all-entities conn))}])

      :handle-ok #(v/collection description (::resource %))}

   (:singular-ref description)
     {:allowed-methods [:options :put :delete :get]
      :available-media-types available-media-types

      :malformed? #(if (get? %)
                     (if-let [resource (db/entity conn (id %))]
                       [false {::resource resource}]))

      :put! #(db/swap conn (id %) (edn-body (:request %)))
      :new? #(not (::resource %))

      :delete! #(db/delete conn (id %))

      :exists? #(::resource %)
      :handle-ok #(v/singular description (::resource %))}})

(defn resources [descriptions]
  (reduce merge (conj (map make-admin-area descriptions)
                      (make-index descriptions))))

