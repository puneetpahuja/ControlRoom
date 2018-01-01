(ns api.schema
  (:require [schema.core :as s]))

(comment DOC
         * javascript naming conventions used
         TODO
         * add templates endpoints templates.task-processes
         TODO LATER
         * add tags and properties[key-value pairs] to all entities)

(s/defschema Auth
  {:username s/Str
   :apiKey s/Str})

(def Id s/Str)

(s/defschema Manifest
  {:ids [Id]
   :auth Auth})


;;; ================================login=======================================


(s/defschema Credentials
  {:username s/Str
   :password s/Str})

(s/defschema User
  {:id s/Str
   :firstName s/Str
   :lastName s/Str
   :title s/Str
   :username s/Str  ;; phone number
   :phone s/Str
   :email s/Str
   :role s/Str   ;; supervisor or worker
   :channels [s/Str] ;; sms, web or app
   :orgUnit s/Str
   :orgUnitId s/Str})

(s/defschema UserAuth
  {:user User
   :apiKey s/Str})


;;; ================================logout======================================


(s/defschema Result
  {:result s/Bool})


;;; ================================org-units===================================


(s/defschema OrgUnit
  {:id s/Str
   :name s/Str
   :users [User]})
