(ns api.handler
  (:require [api.auth :as auth]
            [api.read :as read]
            [api.schema :as schema]
            [compojure.api.sweet :as c]))

(comment TODO
         * have a parent-child heirarchy for entities like tasks, activities
         districts so that when you design an api, you know what comes first and
         what comes second and so on)

(def app
  (c/api
    {:swagger
     {:ui "/"
      :spec "/swagger.json"
      :data {:info {:title "ControlRoom API POC"
                    :description "Provides API endpoints to various front ends like app and dashboard."}
             :tags [{:name "ControlRoom", :description "All APIS"}]}}}

    (c/context "/api" []
               :tags ["ControlRoom"]


;;; ================================login=======================================


               (c/POST "/v0.1/login" []
                       :return schema/UserAuth
                       :body [credentials schema/Credentials]
                       :summary "Returns the user data if the password is correct."
                       (auth/login credentials))


;;; ================================logout======================================


               (c/POST "/v0.1/logout" []
                       :return schema/Result
                       :body [auth schema/Auth]
                       :summary "Logs out the user if auth is correct."
                       (auth/logout auth))


;;; ================================org-units===================================


               (c/POST "/v0.1/org-units" []
                       :return schema/OrgUnitsDiff
                       :body [org-units-manifest schema/Manifest]
                       :summary "Returns all the org units."
                       (read/org-units org-units-manifest))


;;; ================================tasks/pending===============================


               (c/POST "/v0.1/tasks/pending" []
                       :return schema/PendingTasksDiff
                       :body [pending-tasks-manifest schema/Manifest]
                       :summary "Returns all the unsynced pending tasks of the user."
                       (read/tasks-pending pending-tasks-manifest))


;;; ================================tasks/completed=============================


               (c/POST "/v0.1/tasks/completed" []
                       :return [schema/CompletedTask]
                       :body [completed-tasks-manifest schema/Manifest]
                       :summary "Returns all the unsynced completed tasks of the user."
                       (read/tasks-completed completed-tasks-manifest)))))
