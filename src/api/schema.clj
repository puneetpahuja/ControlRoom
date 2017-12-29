(ns api.schema
  (:require [schema.core :as s]))

(comment DOC
         * javascript naming conventions used
         TODO
         * add templates endpoints templates.task-processes
         TODO LATER
         * add tags and properties[key-value pairs] to all entities)


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
   :orgUnit s/Str
   :orgUnitId s/Str
   :channels [s/Str] ;; sms, web or app
   })

(s/defschema UserAuth
  {:user User
   :apiKey s/Str})
