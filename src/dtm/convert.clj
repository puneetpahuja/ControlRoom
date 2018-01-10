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
      (s/split #"/")
      last
      convert-case/->camelCase))

(def remove-namespace (comp keyword remove-namespace-str))

(defn keys [cmap]
  (let [transformer (comp convert-case/->camelCase
                          remove-namespace)]
    (into {}
          (for [[k v] cmap]
            [(transformer k) v]))))

(def keys-emap (comp keys entity-map))


;;; ================================login=======================================


(defn user-auth [emap]
  (when emap
    (let [keys-converted  (keys-emap emap)

          keys-same       (select-keys keys-converted
                                       [:firstName :lastName
                                        :title :username :phone :email])
          {:keys
           [id role
            username
            channels
            apiKey]}       keys-converted

          db               (util/get-db)

          org-unit-eid     (util/get-org-unit-eid username db)

          org-unit-details (keys-emap (util/get-details org-unit-eid db))

          user             (assoc keys-same
                                  :id        (str id)
                                  :role      (remove-namespace-str role)
                                  :channels  (mapv remove-namespace-str channels)
                                  :orgUnit   (:name org-unit-details)
                                  :orgUnitId (str (:id org-unit-details)))

          user-auth        {:user user
                            :apiKey apiKey}]
      user-auth)))


;;; ================================org-units===================================


(defn org-unit [emap]
  (when emap
    (let [keys-converted   (keys-emap emap)

          keys-same        (select-keys keys-converted [:name])
          {:keys
           [id users]}     keys-converted

          users            (mapv (comp :user user-auth util/get-details) users)
          org-unit         (assoc keys-same
                                  :id   (str id)
                                  :users users)]
      org-unit)))


;;; ================================tasks/common================================


(defn measurement-template [emap]
  (when emap
    (let [keys-converted    (keys-emap emap)

          keys-same         (dissoc keys-converted :id :measurement :valueType)

          {:keys
           [id valueType]}  keys-converted

          m-template        (assoc keys-same
                                   :id (str id)
                                   :valueType (remove-namespace-str valueType))]
      m-template)))

(defn task [emap]
  (when emap
    (let [keys-converted    (keys-emap emap)

          keys-same         (select-keys keys-converted
                                         [:name :description :createdAt
                                          :updatedAt :dueDate :completedAt])
          {:keys
           [id type status
            measurementTemplates
            assignedTo
            assignedBy]}    keys-converted

          db                (util/get-db)

          project-eid       (util/get-project-eid id db)
          project-details   (keys-emap (util/get-details project-eid db))

          assignee-details  (keys-emap (util/get-details assignedTo))
          assigner-details  (keys-emap (util/get-details assignedBy))

          m-templates-emaps (mapv util/get-details measurementTemplates)
          m-templates       (mapv measurement-template m-templates-emaps)

          task      (assoc keys-same
                           :id                   (str id)
                           :projectId            (str (:id project-details))
                           :projectName          (:name project-details)
                           :type                 (remove-namespace-str type)
                           :status               (remove-namespace-str status)
                           :assignedTo           (-> assignee-details
                                                     :id
                                                     str)
                           :assignerName         (str (:firstName assigner-details)
                                                      " "
                                                      (:lastName assigner-details))
                           :assignerPhone        (:phone assigner-details)
                           :assignerOrgUnit      (-> assigner-details
                                                     :username
                                                     (util/get-org-unit-eid db)
                                                     (util/get-details db)
                                                     keys-emap
                                                     :name)
                           :measurementTemplates m-templates)]
      task)))


;;; ================================tasks/pending===============================


(defn task-pending [emap]
  (dissoc (task emap) :completedAt))


;;; ================================tasks/completed=============================


(defn task-completed [emap]
  (select-keys (task emap) [:id :name :projectName :completedAt :assignerPhone]))
