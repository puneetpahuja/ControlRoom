(ns dtm.util
  (:require [datomic.api :as d]
            [dtm.config :as config]))

(defn get-conn []
  (d/connect config/uri))

(defn get-db []
  (d/db (get-conn)))

(defn str->uuid [s]
  (java.util.UUID/fromString s))

(defn concat-lists [collection-of-lists]
  (vec (reduce concat [] collection-of-lists)))

(defn get-eid
  "Gives the `eid` of entity whose attribute `attr` has value `value`.
  Assumes that `attr` is unique otherwise gives the first `eid` from the set
  returned by datomic."
  [attr val db]
  (let [q `[:find ?eid
            :where [?eid ~attr ~val]]]
    (ffirst (d/q q db))))

(defn get-all-vals
  "Gets all the values of attribute `attr` in all entities."
  [attr db]
  (let [q `[:find ?val
            :where
            [?eid ~attr]
            [?eid ~attr ?val]]]
    (concat-lists (d/q q db))))

(defn get-details
  "Gets the details of any entity `e`; `eid`; e having value of `attr` as `val`."
  ([e]
   (when e
     (d/touch e)))

  ([eid db]
   (when (and eid db)
     (d/touch (d/entity db eid))))

  ([attr value db]
   (let [eid (get-eid attr value db)]
     (get-details eid db))))

(defn get-attr
  "Gets a particular attribute `attr-to-get` of an entity."
  [attr-to-get & get-details-params]
  (attr-to-get (apply get-details get-details-params)))

(defn diff [manifest all]
  (let [not-contains?    (complement contains?)

        manifest-set     (set manifest)
        all-set          (set all)

        not-in-manifest? (partial not-contains? manifest-set)
        not-in-all?      (partial not-contains? all-set)

        insert (filterv not-in-manifest? all)
        delete (filterv not-in-all?  manifest)]
    {:insert insert
     :delete delete}))


;;; ================================user========================================


(defn get-org-unit-eid
  "Gets the eid of the orgUnit of any user having username as `username`."
  [username db]
  (let [q  `[:find ?e-org-unit
             :where
             [?eu         :user/username ~username]
             [?e-org-unit :org-unit/users ?eu]]]
    (ffirst (d/q q db))))


;;; ================================tasks=======================================


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

(defn get-root-eid [task-id db]
  (let [task-eid (get-eid :task/id task-id db)]
    (root task-eid db)))

(defn get-project-eid [task-id db]
  (let [root-eid (get-root-eid task-id db)]
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

(defn get-assigned-tasks-ids [username db]
  (get-tasks-ids :task.status/assigned username db))

(defn get-task-assigned-to-eid [assignment-measurement-id db]
  (get-eid :task/assigned-to assignment-measurement-id db))

(defn full-name [user-details]
  (let [{:keys
         [firstName
          lastName]} user-details]
    (str firstName " " lastName)))

(defn org-unit-name [user-details]
  (let [db (get-db)]
    (-> user-details
        :username
        (get-org-unit-eid db)
        (get-details db)
        :org-unit/name)))

(defn assignee-details [assignment-measurement-entity]
  (-> assignment-measurement-entity
      get-details
      :assignment-measurement/value
      get-details))

(defn get-project-templates-ids [db]
  (get-all-vals :project-template/id db))
