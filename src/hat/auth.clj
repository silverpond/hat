(ns hat.auth
  (:require [clojure.string :as s]
            [clojure.data.codec.base64 :as base64]
            [clojure.set :refer [intersection]]))

; Based off https://github.com/remvee/ring-basic-authentication
(defn- decode-base64 [string]
  (apply str (map char (base64/decode (.getBytes string)))))

(defn- creds [request]
  (if-let [auth ((:headers request) "authorization")] 
    (if-let [cred (decode-base64 (last (re-find #"^Basic (.*)$" auth)))]
      (s/split (str cred) #":" 2))))

(defn build-authorized? [reference login access-rules]
  (fn [context]
    (if-let [user (if-let [[user pass] (creds (:request context))]
                    (login user pass)
                    (login))]
      (let [acceptable-roles (get-in access-rules [reference :get])]
        (if (not (empty? (intersection (:roles user) acceptable-roles)))
          user)))))

(defn insert-auth [login access-rules [reference resource]]
  (let [authorized? (build-authorized? reference login access-rules)]
    [reference (assoc resource :authorized? authorized?)]))

(defn install-auth [rnr login access-rules]
  (update-in rnr [:resources] #(into {} (map (partial insert-auth login access-rules) %))))

