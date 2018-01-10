(ns dtm.util
  (:require [datomic.api :as d]
            [dtm.config :as config]))

(defn get-conn []
  (d/connect config/uri))

(defn get-db []
  (d/db (get-conn)))

(defn concat-lists [collection-of-lists]
  (vec (reduce concat [] collection-of-lists)))


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

(defn get-attr [attr-to-get & get-details-params]
  (attr-to-get (apply get-details get-details-params)))

(defn get-org-unit-eid [username db]
  (let [q  `[:find ?e-org-unit
             :where
             [?eu :user/username ~username]
             [?e-org-unit :org-unit/users ?eu]]]
    (ffirst (d/q q db))))

(defn diff [manifest all]
  (filterv #((complement contains?) (set manifest) (str %)) all))

(defn parent [task-eid db]
  (:db/id (get-attr :task/parent task-eid db)))

(defn sibling [task-eid db]
  (:db/id (get-attr :task/sibling task-eid db)))

(defn root [task-eid db]
  (if-let [p (parent task-eid db)]
    (root p db)
    (if-let [s (sibling task-eid db)]
      (root s db)
      task-eid)))

(defn get-root [task-id db]
  (let [task-eid (get-eid :task/id task-id db)]
    (root task-eid db)))

(defn get-project-eid [task-id db]
  (let [root-eid (get-root task-id db)]
    (get-eid :project/root root-eid db)))

(defn get-tasks-ids [status username db]
  (let [q `[:find ?tid
            :where
            [?eu :user/username ~username]
            [?em :assignment-measurement/value ?eu]
            [?et :task/assigned-to ?em]
            [?et :task/status ~status]
            [?et :task/id ?tid]]]
    (concat-lists (d/q q db))))

(defn get-pending-tasks-ids [username db]
  (get-tasks-ids :task.status/pending username db))

(defn get-completed-tasks-ids [username db]
  (get-tasks-ids :task.status/completed username db))
