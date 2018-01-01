(ns dtm.util
  (:require [datomic.api :as d]
            [dtm.config :as config]))

(defn concat-lists [collection-of-lists]
  (vec (reduce concat [] collection-of-lists)))

(defn get-conn []
  (d/connect config/uri))

(defn get-db []
  (d/db (get-conn)))

(defn get-eid [attr value db]
  (let [q `[:find ?eid
            :where [?eid ~attr ~value]]]
    (ffirst (d/q q db))))

(defn get-ids [id-attr db]
  (let [q `[:find ?id
            :where
            [?eid ~id-attr]
            [?eid ~id-attr ?id]]]
    (concat-lists (d/q q db))))

(defn get-details
  ([e]
   (when e
     (d/touch e)))

  ([eid db]
   (when (and eid db)
     (d/touch (d/entity db eid))))

  ([attr value db]
   (get-details (get-eid attr value db) db)))

(defn diff [manifest all]
  (filterv #((complement contains?) (set manifest) (str %)) all))
