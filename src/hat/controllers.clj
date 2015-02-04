(ns hat.controllers
  (:require [hat.resources :as r]
            [liberator.representation   :refer [render-map-generic]]
            [liberator.core             :refer [resource]]
            [hat.routes                 :refer [router] :as routes]
            [ring.middleware.resource   :refer [wrap-resource]]
            [ring.middleware.file-info  :refer [wrap-file-info]]
            [ring.middleware.params     :refer [wrap-params]]))

(def index-route
  {"" :index})

(defn- make-routes [descriptions]
  ["/" (reduce merge (conj (map :routes-entries descriptions) index-route))])

(defn- make-base-handler [routes resources]
  (fn [request] (router routes resources request)))

(defn- wrap-routes-binding [handler routes]
  (fn [request]
    (binding [routes/url-generator (routes/make-url-generator routes request)]
      (handler request))))

(defmethod render-map-generic "text/plain" [data context]
  (with-out-str (clojure.pprint/pprint data)))

(defn map-values [m f]
  (into {} (map (fn [[k v]] [k (f v)]) m)))

(defn the-intermediate-step [descriptions]
  {:routes    (make-routes descriptions)
   :resources (r/resources descriptions)})

(defn wrap-cors [handler]
  (fn [request]
    (-> (handler request)
        (assoc-in [:headers "Access-Control-Allow-Origin"]
                  "http://localhost:8080")
        (assoc-in [:headers "Access-Control-Allow-Methods"]
                  "POST, GET, OPTIONS, PUT, DELETE"))))

(defn start [{:keys [routes resources]}]
  (let [resourcified-resources (map-values resources resource)]
    (-> (make-base-handler routes resourcified-resources)
                            (wrap-resource "public")
                            wrap-file-info
                            wrap-params
                            wrap-cors
                            (wrap-routes-binding routes))))

