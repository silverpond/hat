(defproject hat "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
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