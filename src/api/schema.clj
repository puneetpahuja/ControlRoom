(ns api.schema
  (:require [schema.core :as s]))

(comment DOC
         * javascript naming conventions used
         TODO

         TODO LATER
         * add tags and properties[key-value pairs] to all entities)

(s/defschema Auth
  {:username  s/Str
   :apiKey    s/Str})

(def Id
  s/Str)

(s/defschema Manifest
  {:ids       [Id]
   :auth      Auth})

(s/defschema Result
  {:result                 s/Bool
   (s/optional-key :error) s/Str})


;;; ================================login=======================================


(s/defschema Credentials
  {:username  s/Str
   :password  s/Str})

(s/defschema User
  {:id        s/Str
   :firstName s/Str
   :lastName  s/Str
   :title     s/Str
   :username  s/Str   ; phone number
   :phone     s/Str
   :email     s/Str
   :role      s/Str   ; supervisor or worker
   :channels  [s/Str] ; sms, web or app
   :orgUnit   s/Str
   :orgUnitId s/Str})

(s/defschema UserAuth
  {:user User
   :apiKey    s/Str})


;;; ================================org-units===================================


(s/defschema OrgUnit
  {:id        s/Str
   :name      s/Str
   :users     [User]})

(s/defschema OrgUnitsDiff
  {:insert    [OrgUnit]
   :delete    [Id]})


;;; ================================tasks/pending===============================


(s/defschema Validation
  {})

(s/defschema MeasurementTemplate
  {:id        s/Str
   :question  s/Str
   :hint      s/Str
   :required  s/Bool
   :valueType s/Str        ; int, long, string, assignment etc.

   (s/optional-key :validations)  [Validation]
   (s/optional-key :defaultValue) s/Str})

(s/defschema PendingTask
  {:id                   s/Str
   :name                 s/Str
   :description          s/Str
   :projectId            s/Str
   :projectName          s/Str
   :type                 s/Str       ; assignment or measurement
   :assignedTo           s/Str       ; id of the user this is assigned to
   :assignerName         s/Str
   :assignerPhone        s/Str
   :assignerOrgUnit      s/Str
   :createdAt            s/Str       ; milliseconds since 1970
   :updatedAt            s/Str
   :dueDate              s/Str
   :measurementTemplates [MeasurementTemplate]})

(s/defschema PendingTasksDiff
  {:insert [PendingTask]
   :delete [Id]})


;;; ================================tasks/assigned==============================


(s/defschema AssignedTask
  {:id              s/Str
   :name            s/Str
   :assigneeName    s/Str
   :assigneePhone   s/Str
   :assigneeOrgUnit s/Str
   :dueDate         s/Str})

(s/defschema AssignmentTask
  {:id             s/Str
   :name           s/Str
   :projectName    s/Str
   :assignedTasks [AssignedTask]})

(s/defschema AssignmentTasksDiff
  {:insert [AssignmentTask]
   :delete [Id]})


;;; ================================tasks/completed=============================


(s/defschema CompletedTask
  {:id            s/Str
   :name          s/Str
   :projectName   s/Str
   :completedAt   s/Str
   :assignerPhone s/Str})

(s/defschema CompletedTasksDiff
  {:insert [CompletedTask]
   :delete [Id]})


;;; ================================put /tasks==================================


(s/defschema Measurement
  {:id    s/Str
   :value s/Str
   (s/optional-key :dataSource) s/Str
   (s/optional-key :name)       s/Str
   (s/optional-key :valueType)  s/Str})
