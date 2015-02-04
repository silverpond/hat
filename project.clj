(defproject au.com.silverpond/hat "0.1.0"
  :description "HAT (The Hypermedia API Toolkit) lets you build RESTful
               HTTP CRUD APIs that compose cleanly with your ring application."
  :url "https://github.com/silverpond/hat"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [com.datomic/datomic-pro "0.9.4815.12"] 
                 [org.clojure/data.codec "0.1.0"]
                 [bidi "1.10.4"]
                 [http-kit "2.1.16"]
                 [liberator "0.11.1"]]
  :test-matcher #".*test"
  :repositories {"my.datomic.com" {:url "https://my.datomic.com/repo"}})
