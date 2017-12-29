(ns dtm.util
  (:require [datomic.api :as d]
            [dtm.config :as config]))

(defn get-conn []
  (d/connect config/uri))

(defn get-db []
  (d/db (get-conn)))

(defn get-eid [attr value db]
  (let [q `[:find ?eid
            :where [?eid ~attr ~value]]]
    (ffirst (d/q q db))))

(defn get-details
  ([e]
   (when e
     (d/touch e)))

  ([eid db]
   (when (and eid db)
     (d/touch (d/entity db eid))))

  ([attr value db]
   (get-details (get-eid attr value db) db)))
