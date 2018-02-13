(ns dtm.read
  (:require [dtm.convert :as convert]
            [dtm.util :as util]))


;;; ================================org-units===================================


(defn org-unit-project [id]
  (convert/org-unit-project (util/get-details :org-unit-project/id id (util/get-db))))

(defn org-units [_username version]
  (let [db                (util/get-db)
        org-units-version (first (util/get-all-vals :org-units/version db))]
    (if (= org-units-version version)
      {:version  version}
      (let [org-unit-uuids (util/get-all-vals :org-unit-project/id db)]
        {:version  org-units-version
         :projects (mapv org-unit-project org-unit-uuids)}))))


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


;;; ================================templates/projects==========================


(defn template-project [id]
  (convert/template-project (util/get-details :project-template/id id (util/get-db))))

(defn templates-projects [_ ids]
  (let [project-templates-ids  (util/get-project-templates-ids (util/get-db))
        diff                   (util/diff ids (mapv str project-templates-ids))
        {:keys
         [insert
          delete]}             diff
        insert-uuids           (mapv util/str->uuid insert)]
    {:insert (mapv template-project insert-uuids)
     :delete delete}))
