(ns api.handler
  (:require [api.auth :as auth]
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
                       (auth/login credentials)))))
