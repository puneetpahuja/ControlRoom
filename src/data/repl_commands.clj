(ns data.repl-commands)

;;; ============================================================================

(require '[datomic.api :as d])
(require '[data.test :as t])
(require '[dtm.schema :as s])
(require '[clojure.pprint :refer [pprint]])

(def uri "datomic:sql://realtest?jdbc:postgresql://localhost:5432/datomic?user=datomic&password=datomic")

(d/delete-database uri)
(d/create-database uri)

(def conn (d/connect uri))

@(d/transact conn s/schema)
@(d/transact conn t/census1)
@(d/transact conn t/census1-linking)

(def db (d/db conn))
