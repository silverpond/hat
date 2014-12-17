# HAT ( The Hypermedia Application Toolkit )

<img src="http://i.imgur.com/LqW3FEI.jpg"
 alt="HAT logo" title="Hypermedia application toolkit" align="right" />

HAT (The hypermedia application toolkit) lets you build RESTful HTTP CRUD APIs
that compose cleanly with your ring application.

## Usage

```clojure
(generate-description {:singular-name      "host"
                       :singular-titlecase "Host"
                       :plural-name        "hosts"
                       :plural-titlecase   "Hosts"
                       :conn               database-connection
                       :db-entity-type     :host/name
                       :db-search-attr     :host/name
                       :fields             [{:title "Name"
                                             :name  :host/name
                                             :type  :text}]})
```

## Todo

- Improve documentation.
- Drop httpkit.
- Add validation support.
- Possibly split out the datomic persistance into an adapter library.

## License

Copyright © 2014 Silverpond

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

