(ns dtm.convert
  (:refer-clojure :exclude [keys])
  (:require [camel-snake-kebab.core :as convert-case]
            [clojure.string :as s]
            [dtm.util :as util]))

(defn entity-map [emap]
  (into {} emap))

(defn remove-namespace-str [namespaced-keyword]
  (-> namespaced-keyword
      name
      convert-case/->camelCase))

(def remove-namespace (comp keyword remove-namespace-str))

(defn transform-keys [cmap transformer & args]
  (into {}
        (for [[k v] cmap]
          [(apply transformer k args) v])))

(defn keys [cmap]
  (transform-keys cmap remove-namespace))

(def keys-emap (comp keys entity-map))


;;; ================================login=======================================


(defn user-auth [emap]
  (when emap
    (let [keys-converted  (keys-emap emap)

          same-vals       (select-keys keys-converted
                                       [:firstName :lastName
                                        :title :phone :email
                                        :password])
          {:keys
           [id username
            apiKey photo]} keys-converted

          db               (util/get-db)

          org-unit-eid     (util/get-org-unit-eid username db)
          org-unit-details (if org-unit-eid (keys-emap (util/get-details org-unit-eid db)))

          state-eid        (if org-unit-eid (util/get-eid :state/verticals org-unit-eid db))
          state-details    (if state-eid (keys-emap (util/get-details state-eid db)))

          user             (util/filter-nil
                             (assoc same-vals
                                    :id        (str id)
                                    :orgUnit   (:name org-unit-details)
                                    :state     (:name state-details)
                                    :photo     (or photo "")))

          user-auth        {:user user
                            :apiKey apiKey}]
      user-auth)))


;;; ================================org-units===================================


(defn org-unit-user [emap]
  (when emap
    (let [keys-converted (keys-emap emap)

          same-vals      (select-keys keys-converted
                                      [:username])

          name           (util/full-name keys-converted)
          photo          (or (:photo keys-converted) "")

          org-unit-user  (assoc same-vals
                                :name name
                                :photo photo)]
      org-unit-user)))

(defn vertical [emap]
  (when emap
    (let [keys-converted    (keys-emap emap)

          same-vals         (select-keys keys-converted
                                         [:name])

          {:keys
           [id users]}      keys-converted

          users             (mapv (comp org-unit-user util/get-details) users)

          vertical (assoc same-vals
                                   :id (str id)
                                   :users users)]
      vertical)))

(defn state [emap]
  (when emap
    (let [keys-converted (keys-emap emap)

          same-vals      (select-keys keys-converted
                                      [:name])

          {:keys
           [id
            verticals]}  keys-converted

          verticals      (mapv (comp vertical util/get-details) verticals)

          state (assoc same-vals
                                :id (str id)
                                :vertical/departments verticals)]
      state)))

(defn project [emap]
  (when emap
    (let [keys-converted   (keys-emap emap)

          same-vals        (select-keys keys-converted [:name])

          {:keys
           [id states]}     keys-converted

          states           (mapv (comp state util/get-details) states)

          project (assoc same-vals
                                  :id   (str id)
                                  :states states)]
      project)))


;;; ================================tasks/pending===============================


(defn measurement-template [emap]
  (when emap
    (let [keys-converted    (keys-emap emap)

          same-vals         (dissoc keys-converted :id :measurement :valueType)

          {:keys
           [id valueType]}  keys-converted

          m-template        (assoc same-vals
                                   :id (str id)
                                   :valueType (remove-namespace-str valueType))]
      m-template)))

(defn task-pending [emap]
  (when emap
    (let [keys-converted    (keys-emap emap)

          same-vals         (select-keys keys-converted
                                         [:name :dueDate])
          {:keys
           [id type
            measurementTemplates]}    keys-converted

          db                (util/get-db)

          project-eid       (util/get-project-eid id db)
          project-details   (keys-emap (util/get-details project-eid db))

          assigner-details  (-> id
                                (util/get-assigner-eid db)
                                (util/get-details db)
                                keys-emap)

          m-templates-emaps (util/sort-by-position (mapv util/get-details measurementTemplates)
                                                   :measurement-template/position)

          m-templates       (mapv measurement-template m-templates-emaps)

          pending-task      (assoc
                              same-vals
                              :id                   (str id)
                              :projectName          (:name project-details)
                              :type                 (remove-namespace-str type)
                              :assignerName         (util/full-name assigner-details)
                              :assignerPhone        (:phone assigner-details)
                              :assignerOrgUnit      (util/org-unit-name assigner-details)
                              :measurementTemplates m-templates)]
      pending-task)))


;;; =========================tasks/assigned/pending=============================


(defn assigned-pending-task [emap]
  (let [keys-converted          (keys-emap emap)
        same-vals               (select-keys keys-converted [:name :dueDate])
        {:keys
         [id status
          assignedTo]}           keys-converted

        assignee-details         (keys-emap (util/assignee-details assignedTo))
        assigned-pending-task    (assoc
                                   same-vals
                                   :id (str id)
                                   :assigneeName (util/full-name assignee-details)
                                   :assigneePhone (:phone assignee-details)
                                   :assigneeOrgUnit (util/org-unit-name assignee-details)
                                   :status (remove-namespace-str status))]
    assigned-pending-task))

(defn m-template->assigned-pending-task [m-template-emap]
  (let [keys-converted             (keys-emap m-template-emap)
        measurement-eid            (:db/id (:measurement keys-converted))
        db                         (util/get-db)
        assigned-pending-task-eid  (util/get-task-assigned-to-eid measurement-eid db)
        assigned-pending-task-emap (util/get-details assigned-pending-task-eid db)]
    (assigned-pending-task assigned-pending-task-emap)))

(defn task-assigned-pending [emap]
  (when emap
    (let [keys-converted        (keys-emap emap)

          same-vals             (select-keys keys-converted
                                             [:name])
          {:keys
           [id
            measurementTemplates
            assignedTo]}        keys-converted

          db                    (util/get-db)

          project-eid           (util/get-project-eid id db)
          project-details       (keys-emap (util/get-details project-eid db))

          assignee-details      (keys-emap (util/assignee-details assignedTo))

          m-templates-emaps     (util/sort-by-position
                                  (mapv util/get-details measurementTemplates)
                                  :measurement-template/position)

          assigned-tasks        (mapv m-template->assigned-pending-task
                                      m-templates-emaps)

          task-assigned-pending (assoc same-vals
                                       :id            (str id)
                                       :projectName   (:name project-details)
                                       :assignedTasks assigned-tasks)]
      task-assigned-pending)))


;;; ============================tasks/assigned/completed========================


(defn assigned-completed-task [emap]
  (let [keys-converted          (keys-emap emap)
        same-vals               (select-keys keys-converted [:name
                                                             :completedAt])
        {:keys
         [id
          assignedTo]}           keys-converted

        assignee-details         (keys-emap (util/assignee-details assignedTo))
        assigned-completed-task    (assoc
                                     same-vals
                                     :id (str id)
                                     :assigneeName (util/full-name assignee-details)
                                     :assigneePhone (:phone assignee-details)
                                     :assigneeOrgUnit (util/org-unit-name assignee-details))]
    assigned-completed-task))

(defn m-template->assigned-completed-task [m-template-emap]
  (let [keys-converted               (keys-emap m-template-emap)
        measurement-eid              (:db/id (:measurement keys-converted))
        db                           (util/get-db)
        assigned-completed-task-eid  (util/get-task-assigned-to-eid measurement-eid db)
        assigned-completed-task-emap (util/get-details assigned-completed-task-eid db)]
    (assigned-completed-task assigned-completed-task-emap)))

(defn task-assigned-completed [emap]
  (when emap
    (let [keys-converted          (keys-emap emap)

          same-vals               (select-keys keys-converted
                                               [:name])
          {:keys
           [id
            measurementTemplates
            assignedTo]}        keys-converted

          db                      (util/get-db)

          project-eid             (util/get-project-eid id db)
          project-details         (keys-emap (util/get-details project-eid db))

          assignee-details        (keys-emap (util/assignee-details assignedTo))

          m-templates-emaps       (util/sort-by-position
                                    (mapv util/get-details measurementTemplates)
                                    :measurement-template/position)

          assigned-tasks          (mapv m-template->assigned-completed-task
                                        m-templates-emaps)

          task-assigned-completed (assoc same-vals
                                         :id            (str id)
                                         :projectName   (:name project-details)
                                         :assignedTasks assigned-tasks)]
      task-assigned-completed)))


;;; ================================tasks/completed=============================


(defn task-completed [emap]
  (when emap
    (let [keys-converted    (keys-emap emap)

          same-vals         (select-keys keys-converted
                                         [:name :completedAt])
          {:keys
           [id]}            keys-converted

          db                (util/get-db)

          project-eid       (util/get-project-eid id db)
          project-details   (keys-emap (util/get-details project-eid db))

          assigner-details  (-> id
                                (util/get-assigner-eid db)
                                (util/get-details db)
                                keys-emap)

          completed-task    (assoc
                              same-vals
                              :id                   (str id)
                              :projectName          (:name project-details)
                              :assignerPhone        (:phone assigner-details))]
      completed-task)))


;;; ================================templates/projects==========================


(defn template-activity [emap]
  (when emap
    (let [keys-converted (keys-emap emap)
          same-vals (select-keys keys-converted
                                 [:title :description])

          {:keys
           [id]}   keys-converted

          activity-template (assoc
                              same-vals
                              :id  (str id))]
      activity-template)))
