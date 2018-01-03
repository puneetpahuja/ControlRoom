(ns dtm.read
  (:require [dtm.convert :as convert]
            [dtm.util :as util]))


;;; ================================org-units===================================


(defn org-unit [id]
  (convert/org-unit (util/get-details :org-unit/id id (util/get-db))))

(defn org-units [_ ids]
  (let [org-unit-ids  (util/get-ids :org-unit/id (util/get-db))
        diff          (util/diff ids org-unit-ids)]
    (mapv org-unit diff)))


;;; ================================tasks/pending===============================


(defn task-pending [id]
  (convert/task-pending (util/get-details :task/id id (util/get-db))))

(defn tasks-pending [username ids]
  (let [tasks-pending-ids (util/get-pending-tasks-ids username (util/get-db))
        diff              (util/diff ids tasks-pending-ids)]
    (mapv task-pending diff)))


;;; ================================tasks/completed=============================


(defn task-completed [id]
  (convert/task-completed (util/get-details :task/id id (util/get-db))))

(defn tasks-completed [username ids]
  (let [tasks-completed-ids (util/get-completed-tasks-ids username (util/get-db))
        diff                (util/diff ids tasks-completed-ids)]
    (mapv task-completed diff)))
