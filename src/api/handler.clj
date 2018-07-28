(ns api.handler
  (:require [api.auth :as auth]
            [api.read :as read]
            [api.schema :as schema]
            [api.write :as write]
            [compojure.api.sweet :as c]
            [ring.swagger.upload :as upload]
            [schema.core :as s]
            [dashboard.postgres :as dashboard]))

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
                       :summary "Returns the user data."
                       (auth/login credentials))


;;; ================================PUT user====================================


               (c/PUT "/v0.1/user" []
                      :return schema/Credentials
                      :body [user-details schema/AddUser]
                      :summary "Returns the user data."
                      (write/user user-details))


;;; ================================logout======================================


               (c/POST "/v0.1/logout" []
                       :return schema/Result
                       :body [auth schema/Auth]
                       :summary "Logs out the user."
                       (auth/logout auth))


;;; ================================org-units===================================


               (c/POST "/v0.1/org-units" []
                       :return schema/OrgUnitsDiff
                       :body [org-units-manifest schema/VersionManifest]
                       :summary "Returns all the org units."
                       (read/org-units org-units-manifest))


;;; ================================tasks/pending===============================


               (c/POST "/v0.1/tasks/pending" []
                       :return schema/PendingTasksDiff
                       :body [pending-tasks-manifest schema/Manifest]
                       :summary "Returns all the unsynced pending tasks of the user."
                       (read/tasks-pending pending-tasks-manifest))


;;; =============================tasks/assigned/pending=========================


               (c/POST "/v0.1/tasks/assigned/pending" []
                       :return schema/AssignmentPendingTasksDiff
                       :body [assigned-pending-tasks-manifest schema/Manifest]
                       :summary "Returns all the unsynced pending assigned tasks of the user."
                       (read/tasks-assigned-pending assigned-pending-tasks-manifest))


;;; =============================tasks/assigned/completed=======================


               (c/POST "/v0.1/tasks/assigned/completed" []
                       :return schema/AssignmentCompletedTasksDiff
                       :body [assigned-completed-tasks-manifest schema/Manifest]
                       :summary "Returns all the unsynced completed assigned tasks of the user."
                       (read/tasks-assigned-completed assigned-completed-tasks-manifest))


;;; ================================tasks/completed=============================


               (c/POST "/v0.1/tasks/completed" []
                       :return schema/CompletedTasksDiff
                       :body [completed-tasks-manifest schema/Manifest]
                       :summary "Returns all the unsynced completed tasks of the user."
                       (read/tasks-completed completed-tasks-manifest))


;;; ================================tasks/tags==================================


               (c/POST "/v0.1/tasks/tags" []
                       :return schema/TagsDiff
                       :body [tags-manifest schema/VersionManifest]
                       :summary "Returns all the tasks tags."
                       (read/tasks-tags tags-manifest))


;;; ================================PUT tasks===================================


               (c/PUT "/v0.1/tasks" []
                      :return schema/Result
                      :body [task-submissions schema/TaskSubmissions]
                      :summary "Submits completed or rejected tasks."
                      (write/tasks task-submissions))


;;; ================================PUT activities==============================


               (c/PUT "/v0.1/activities" []
                      :return schema/Result
                      :body [activity-submissions schema/ActivitySubmissions]
                      :summary "Creates new activities in a project."
                      (write/activities activity-submissions))


;;; ===========================PUT activities/dynamic===========================


               (c/PUT "/v0.1/activities/dynamic" []
                      :return [s/Str]
                      :body [activity-submissions schema/DynamicActivitySubmissions]
                      :summary "Creates new activities in a project."
                      (write/activities-dynamic activity-submissions))




;;; ================================templates/activities========================


               (c/POST "/v0.1/templates/activities" []
                       :return schema/ActivityTemplatesDiff
                       :body [activity-templates-manifest schema/Manifest]
                       :summary "Returns all the activity templates."
                       (read/templates-activities activity-templates-manifest))


;;; ==============================PUT templates/activities======================


               (c/PUT "/v0.1/templates/activities" []
                      :return schema/Result
                      :body [activity-templates schema/ActivityTemplateSubmissions]
                      :summary "Creates new activity templates."
                      (write/templates-activities activity-templates))


;;; ================================init========================================


               (c/POST "/v0.1/init" []
                       :return [[s/Str]]
                       :body [init schema/Init]
                       :summary "Initializes projects. Used for testing."
                       (write/init init))

;;; ================================upload======================================


               ;; stops working with the newer version compojure-api
               (c/PUT "/v0.1/upload" []
                      :return schema/Filepath
                      :multipart-params [file :- upload/TempFileUpload
                                         username :- s/Str
                                         apiKey :- s/Str]
                      :middleware [upload/wrap-multipart-params]
                      (let [auth {:username username
                                  :apiKey apiKey}]
                        (write/upload auth file)))


;;; ==============================download======================================


               (c/POST "/v0.1/download" []
                       ;; :return schema/File
                       :body [file-manifest schema/FileManifest]
                       (read/download file-manifest))


;;; ==========================dashboard/db-update===============================


               (c/POST "/v0.1/dashboard/db-update" []
                       :return schema/Result
                       :body [db schema/DB]
                       :summary "Updates postgres db for dashboard."
                       (dashboard/update-db db)))))
