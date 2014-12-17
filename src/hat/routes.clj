(ns hat.routes
  (:require [bidi.bidi :refer [path-for match-route]]))

(def ^:dynamic url-generator nil)

(defn make-url-generator [routes request]
  (let [server (str (name (:scheme request)) "://"
                    (get-in request [:headers "host"]))]
    (fn [& ref-and-keys]
      (str server (apply path-for (conj ref-and-keys routes))))))

(defn router [routes refs {:keys [uri] :as request}]
  (let [match   (match-route routes uri)
        handler (refs (:handler match))
        req     (merge request match)]
    (when handler (handler req))))

