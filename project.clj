(defproject controlroom-api-poc "0.1.0-SNAPSHOT"
  :description "To make the api with datomic incrementally(one endpoint after another) from controlroom-dummy-api"
  :dependencies [[amazonica "0.3.121"]
                 [camel-snake-kebab "0.4.0"]
                 [clj-time "0.14.2"]
                 [com.datomic/datomic-pro "0.9.5561.62"]
                 [com.draines/postal "2.0.2"]
                 [datomic-schema "1.3.0"]
                 [metosin/compojure-api "1.1.11"]
                 ;; [metosin/compojure-api "2.0.0-alpha20"]
                 [org.clojure/clojure "1.8.0"]
                 [org.clojure/data.csv "0.1.4"]
                 [org.clojure/tools.namespace "0.2.11"]
                 [org.clojure/tools.trace "0.7.9"]
                 [org.postgresql/postgresql "9.4-1200-jdbc41"]
                 ;;[org.postgresql/postgresql "9.3-1102-jdbc41"]
                 [org.clojure/java.jdbc "0.7.5"]
                 [toucan "1.1.4"]]
  :repositories {"my.datomic.com" {:url "https://my.datomic.com/repo"
                                   :creds :gpg}}
  :ring {:handler api.handler/app}
  :uberjar-name "server.jar"
  :profiles {:dev {:dependencies [[javax.servlet/javax.servlet-api "3.1.0"]]
                   :plugins [[lein-ring "0.12.0"]]}})
