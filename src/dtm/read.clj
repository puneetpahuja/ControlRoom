(ns dtm.read
  (:require [dtm.convert :as convert]
            [dtm.util :as util]))


;;; ================================org-units===================================


(defn org-unit [id]
  (convert/org-unit (util/get-details :org-unit/id id (util/get-db))))

(defn org-units [_ ids]
  (let [org-unit-ids (util/get-ids :org-unit/id (util/get-db))
        diff         (util/diff ids (mapv str org-unit-ids))
        {:keys
         [insert
          delete]}   diff
        insert-uuids (mapv util/str->uuid insert)]
    {:insert (mapv org-unit insert-uuids)
     :delete delete}))


;;; ================================tasks/pending===============================


(defn task-pending [id]
  (convert/task-pending (util/get-details :task/id id (util/get-db))))

(defn tasks-pending [username ids]
  (let [tasks-pending-ids (util/get-pending-tasks-ids username (util/get-db))
        diff              (util/diff ids (mapv str tasks-pending-ids))
        {:keys
         [insert
          delete]}        diff
        insert-uuids      (mapv util/str->uuid insert)]
    {:insert (mapv task-pending insert-uuids)
     :delete delete}))


;;; ================================tasks/assigned==============================


(defn task-assigned [id]
  (convert/task-assigned (util/get-details :task/id id (util/get-db))))

(defn tasks-assigned [username ids]
  (let [tasks-assigned-ids (util/get-assigned-tasks-ids username (util/get-db))
        diff               (util/diff ids (mapv str tasks-assigned-ids))
        {:keys
         [insert
          delete]}         diff
        insert-uuids       (mapv util/str->uuid insert)]
    {:insert (mapv task-assigned insert-uuids)
     :delete delete}))


;;; ================================tasks/completed=============================


(defn task-completed [id]
  (convert/task-completed (util/get-details :task/id id (util/get-db))))

(defn tasks-completed [username ids]
  (let [tasks-completed-ids (util/get-completed-tasks-ids username (util/get-db))
        diff                (util/diff ids (mapv str tasks-completed-ids))
        {:keys
         [insert
          delete]}          diff
        insert-uuids        (mapv util/str->uuid insert)]
    {:insert (mapv task-completed insert-uuids)
     :delete delete}))
