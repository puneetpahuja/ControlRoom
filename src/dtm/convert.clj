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


;;; ================================login=======================================


(defn user-auth [emap]
  (when emap
    (let [cmap            (entity-map emap)
          keys-converted  (keys cmap)

          keys-same       (select-keys keys-converted
                                       [:firstName :lastName
                                        :title :username :phone :email])
          {:keys
           [id role
            orgUnit
            channels
            apiKey]}       keys-converted

          org-unit-details (keys (entity-map (util/get-details orgUnit)))

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
    (let [cmap             (entity-map emap)
          keys-converted   (keys cmap)

          keys-same        (select-keys keys-converted [:name])
          {:keys
           [id users]}     keys-converted

          users            (mapv (comp :user user-auth util/get-details) users)
          org-unit         (assoc keys-same
                                  :id   (str id)
                                  :users users)]
      org-unit)))


;;; ================================tasks/common================================


(defn task [emap]
  (when emap
    (let [cmap              (entity-map emap)
          keys-converted    (keys cmap)

          keys-same         (select-keys keys-converted
                                         [:name :description :createdAt
                                          :updatedAt :dueDate :completedAt])
          {:keys
           [id project
            type status
            assignedTo
            assignedBy]}    keys-converted

          project-details   (keys (entity-map (util/get-details project)))
          assignee-details  (keys (entity-map (util/get-details assignedTo)))
          assigner-details  (keys (entity-map (util/get-details assignedBy)))

          task      (assoc keys-same
                           :id               (str id)
                           :projectId        (str (:id project-details))
                           :projectName      (:name project-details)
                           :type             (remove-namespace-str type)
                           :status           (remove-namespace-str status)
                           :assignedTo       (-> assignee-details
                                                 :id
                                                 str)
                           :assignerName     (str (:firstName assigner-details)
                                                  " "
                                                  (:lastName assigner-details))
                           :assignerPhone   (:phone assigner-details)
                           :assignerOrgUnit (-> assigner-details
                                                :orgUnit
                                                util/get-details
                                                entity-map
                                                keys
                                                :name))]
      task)))


;;; ================================tasks/pending===============================


(defn task-pending [emap]
  (dissoc (task emap) :completedAt))


;;; ================================tasks/completed=============================


(defn task-completed [emap]
  (select-keys (task emap) [:id :name :projectName :completedAt :assignerPhone]))
