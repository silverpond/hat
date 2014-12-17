# HAT - Hypermedia API Toolkit

<img src="http://i.imgur.com/jwc0hRy.jpg"
 alt="HAT logo" title="Hypermedia API toolkit" align="right" />

HAT (The Hypermedia API Toolkit) lets you build RESTful HTTP CRUD APIs
that compose cleanly with your ring application.

<br />
<br />
<br />
<br />
<br />
<br />
<br />

## Usage

```clojure
; Resource description
(defn hosts-description [database-connection]
  (generate-description {:singular-name      "host"
                         :singular-titlecase "Host"
                         :plural-name        "hosts"
                         :plural-titlecase   "Hosts"
                         :conn               database-connection
                         :db-entity-type     :host/name
                         :db-search-attr     :host/name
                         :fields             [{:title "Name"
                                               :name  :host/name
                                               :type  :text}]}))

; Auth
(def all-roles #{:public :user :admin})

(def admin-only #{:admin})

(def authorisation-rules
  {:index  {:get all-roles}
   :hosts  {:get all-roles :post admin-only}
   :host   {:get all-roles :put  admin-only :delete admin-only}
   :events {:get all-roles :post admin-only}
   :event  {:get all-roles :put  admin-only :delete admin-only}})

(defn authenticate
  ([]
   {:roles #{:public}})
  ([username password]
   (if (= password "adminpassword")
     {:username username
      :roles    all-roles})))

; Compose and start
(defn -main []
  (let [conn   (db/connect "datomic:dev://datomic:4334/some-database")
        hosts  (hosts-description conn)
        events (events-description conn)]
    (-> (controllers/the-intermediate-step [hosts events])
        (one-to-many hosts events :host/name :event/host)
        (install-auth authenticate authorisation-rules)
        controllers/start)))
```

## Todo

- Improve documentation.
- Drop httpkit.
- Add validation support.
- Possibly split out the datomic persistance into an adapter library.
- Write persistance layers for PostgreSQL, MySQL, SQLite.

## License

Copyright © 2014 Silverpond

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

