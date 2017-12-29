(defproject controlroom-api-poc "0.1.0-SNAPSHOT"
  :description "To make the api with datomic incrementally(one endpoint after another) from controlroom-dummy-api"
  :dependencies [[camel-snake-kebab "0.4.0"]
                 [com.datomic/datomic-pro "0.9.5561.62"]
                 [datomic-schema "1.3.0"]
                 [metosin/compojure-api "1.1.11"]
                 [org.clojure/clojure "1.8.0"]
                 [org.clojure/tools.namespace "0.2.11"]
                 [org.clojure/tools.trace "0.7.9"]
                 [org.postgresql/postgresql "9.3-1102-jdbc41"]]
  :repositories {"my.datomic.com" {:url "https://my.datomic.com/repo"
                                   :creds :gpg}}
  :ring {:handler api.handler/app}
  :uberjar-name "server.jar"
  :profiles {:dev {:dependencies [[javax.servlet/javax.servlet-api "3.1.0"]]
                   :plugins [[lein-ring "0.12.0"]]}})
