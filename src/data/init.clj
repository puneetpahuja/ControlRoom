(ns data.init
  (:require [data.test :as t]
            [dtm.schema :as s]
            [datomic.api :as d]
            [config.dtm :as config]
            [data.parse-users-csv :as csv]
            [ring.util.http-response :as response]
            [api.util :as api-util]
            [dtm.util :as dtm-util]
            [debug.trace :as trace]))

;; (defn init
;;   ([demo _]
;;    (let [uri  config/uri]
;;      (d/delete-database uri)
;;      (d/create-database uri)
;;      (let [conn (d/connect uri)]
;;        @(d/transact conn s/schema)
;;        @(d/transact conn t/users)
;;        @(d/transact conn t/verticals)
;;        @(d/transact conn t/states)
;;        @(d/transact conn t/projects)
;;        @(d/transact conn t/org-units)
;;        @(d/transact conn t/clients)
;;        @(d/transact conn t/task-tags)
;;        @(d/transact conn t/activity-templates)
;;        ;; debug
;;        @(d/transact conn t/multimedia-sample-activity)
;;        @(d/transact conn t/multimedia-sample-activity-linking)

;;        (if demo
;;          (do @(d/transact conn t/ro-activity)
;;              @(d/transact conn t/ro-linking)
;;              @(d/transact conn t/tree-activity)
;;              @(d/transact conn t/tree-linking)
;;              @(d/transact conn t/school-activity)
;;              @(d/transact conn t/school-linking))))))


(defn init
  ([demo _]
   (let [uri config/uri]
     ;; (d/delete-database uri)
     ;; (d/create-database uri)
     (let [conn (d/connect uri)]
       ;; @(d/transact conn s/schema)
       @(d/transact conn t/task-tags)
       ;; @(d/transact conn t/org-units)
       (if (= demo "create-data-for-hih")
         (do
           @(d/transact conn (csv/projects-tx))
           (csv/users-tx (csv/users "hih_users.csv")))
         []))))


  ([{:keys [username password demo]}]
   (if (api-util/admin? username password)
     (do
       (response/ok (init demo true))
       ;;(response/ok {:result true})
       )
     (response/unauthorized {:error "wrong credentials"}))))


(defn add-user-photo-attribute [creds]
  (if (api-util/admin? creds)
    (do
      (dtm-util/transact [{:db/id #db/id[:db.part/db]
                           :db/ident :user/photo
                           :db/valueType :db.type/string
                           :db/cardinality :db.cardinality/one
                           :db.install/_attribute :db.part/db}])
      (response/ok {:result true}))
    (response/unauthorized {:error "wrong credentials"})))

(defn retract-entity [type id]
  (dtm-util/retract-entity (keyword (str type "/id")) (dtm-util/str->uuid id)))

(defn retract-entities
  ([{:keys [creds type ids]}]
   (trace/trace 'api.write 'data.init 'api.init)
   (if (api-util/admin? creds)
     (do
       (retract-entities type ids)
       (response/ok {:result true}))
     (response/unauthorized {:error "wrong credentials"})))

  ([type ids]
   (map #(retract-entity type %) ids)))
