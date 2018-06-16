(ns data.init
  (:require [data.demo.test :as t]
            [dtm.schema :as s]
            [datomic.api :as d]
            [config.dtm :as config]
            [ring.util.http-response :as response]))

(defn init
  ([demo _]
   (let [uri  config/uri]
     (d/delete-database uri)
     (d/create-database uri)
     (let [conn (d/connect uri)]
       @(d/transact conn s/schema)
       @(d/transact conn t/users)
       @(d/transact conn t/verticals)
       @(d/transact conn t/states)
       @(d/transact conn t/projects)
       @(d/transact conn t/org-units)
       @(d/transact conn t/clients)
       @(d/transact conn t/task-tags)
       @(d/transact conn t/activity-templates)
       (if demo
         (do @(d/transact conn t/ro-activity)
             @(d/transact conn t/ro-linking)
             @(d/transact conn t/tree-activity)
             @(d/transact conn t/tree-linking)
             @(d/transact conn t/school-activity)
             @(d/transact conn t/school-linking))))))

  ([{:keys [username password demo]}]
   (if (and (= username "admin")
            (= password "fancyLikeThat"))
     (do
       (init demo true)
       (response/ok {:result true}))
     (response/unauthorized {:error "wrong credentials"}))))

(defn init-plus [{:keys [username password]}]
  ;;   (if (and (= username "admin")
  ;;            (= password "fancyLikeThat"))
  ;;     (do
  ;;       (init)
  ;;       (let [uri config/uri
  ;;             conn (d/connect uri)]
  ;;         @(d/transact conn t/ro1-linking)
  ;;         ;; @(d/transact conn t/ro2-linking)
  ;;         )
  ;;       (response/ok {:result true}))
  ;;     (response/unauthorized {:error "wrong credentials"}))
  )
