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

(def Id s/Str)

(s/defschema Manifest
  {:ids       [Id]
   :auth      Auth})

(s/defschema Result
  {:result           s/Bool
   (s/optional-key
     :error)         s/Str})


;;; ================================login=======================================


(s/defschema Credentials
  {:username  s/Str
   :password  s/Str})

(s/defschema User
  {:id             Id
   (s/optional-key
     :firstName)   s/Str
   (s/optional-key
     :lastName)    s/Str
   (s/optional-key
     :title)       s/Str
   :phone          s/Str
   (s/optional-key
     :email)       s/Str
   (s/optional-key
     :orgUnit)     s/Str
   (s/optional-key
     :state)       s/Str})

(s/defschema UserAuth
  {:user      User
   :apiKey    s/Str})


;;; ================================PUT user====================================


(s/defschema OptionalUser
  {(s/optional-key
     :firstName)    s/Str
   (s/optional-key
     :lastName)     s/Str
   (s/optional-key
     :title)        s/Str
   :phone           s/Str
   (s/optional-key
     :email)        s/Str
   (s/optional-key
     :orgUnit)      s/Str
   (s/optional-key
     :state)        s/Str
   (s/optional-key
     :password)     s/Str})

(s/defschema AddUser
  {:user  OptionalUser
   :auth  Auth})


;;; ================================org-units===================================

(s/defschema OrgUnitUser
  {:name     s/Str
   :username s/Str})

(s/defschema OrgUnitVertical
  {:id    Id
   :name  s/Str
   :users [OrgUnitUser]})

(s/defschema OrgUnitState
  {:id                   Id
   :name                 s/Str
   :vertical/departments [OrgUnitVertical]})

(s/defschema OrgUnitProject
  {:id        Id
   :name      s/Str
   :states    [OrgUnitState]})

(s/defschema OrgUnitsDiff
  {:version    s/Int
   (s/optional-key
     :projects) [OrgUnitProject]})

(s/defschema VersionManifest
  {:version   s/Int
   :auth      Auth})


;;; ================================tasks/pending===============================


(s/defschema Validation
  {})

(s/defschema MeasurementTemplate
  {:id              Id
   :question        s/Str
   (s/optional-key
     :hint)         s/Str
   :required        s/Bool
   :valueType       s/Str        ; int, long, string, assignment etc.
   (s/optional-key
     :validations)  [Validation]
   (s/optional-key
     :defaultValue) s/Str})

(s/defschema PendingTask
  {:id                   Id
   :name                 s/Str
   :projectName          s/Str
   :type                 s/Str       ; assignment or measurement
   :assignerName         s/Str
   :assignerPhone        s/Str
   :assignerOrgUnit      s/Str
   :dueDate              s/Str
   :measurementTemplates [MeasurementTemplate]})

(s/defschema PendingTasksDiff
  {:insert [PendingTask]
   :delete [Id]})


;;; ================================tasks/completed=============================


(s/defschema CompletedTask
  {:id            Id
   :name          s/Str
   :projectName   s/Str
   :completedAt   s/Str
   :assignerPhone s/Str})

(s/defschema CompletedTasksDiff
  {:insert [CompletedTask]
   :delete [Id]})


;;; ============================tasks/assigned/pending==========================


(s/defschema AssignedPendingTask
  {:id              Id
   :name            s/Str
   :assigneeName    s/Str
   :assigneePhone   s/Str
   :assigneeOrgUnit s/Str
   :dueDate         s/Str
   :status          s/Str})

(s/defschema AssignmentPendingTask
  {:id             Id
   :name           s/Str
   :projectName    s/Str
   :assignedTasks [AssignedPendingTask]})

(s/defschema AssignmentPendingTasksDiff
  {:insert [AssignmentPendingTask]
   :delete [Id]})


;;; ============================tasks/assigned/completed========================


(s/defschema AssignedCompletedTask
  {:id              Id
   :name            s/Str
   :assigneeName    s/Str
   :assigneePhone   s/Str
   :assigneeOrgUnit s/Str
   :completedAt     s/Str})

(s/defschema AssignmentCompletedTask
  {:id             Id
   :name           s/Str
   :projectName    s/Str
   :assignedTasks [AssignedCompletedTask]})

(s/defschema AssignmentCompletedTasksDiff
  {:insert [AssignmentCompletedTask]
   :delete [Id]})


;;; ================================tasks/tags==================================


(s/defschema TagsDiff
  {:version        s/Int
   (s/optional-key
     :tags)        [s/Str]})


;;; ================================PUT tasks===================================


(s/defschema Measurement
  {:id              Id     ; measurement template id
   :valueType       s/Str     ; can be integer, float, string, photo, location, date
   :value           s/Str
   (s/optional-key
     :entityId)     s/Str}    ; id of the entity if you are creating a new entity or want to link it to an existing entity
  )

(s/defschema TaskSubmission
  {:id              Id
   :status          s/Str         ; completed, assigned, suspended or rejected
   (s/optional-key
     :measurements) [Measurement] ; present only in case of completed and assigned tasks
   })

(s/defschema TaskSubmissions
  {:tasks  [TaskSubmission]
   :auth   Auth})


;;; ================================PUT activities==============================


(s/defschema CreateMeasurementTemplate
  {:question        s/Str
   :hint            s/Str
   :required        s/Bool
   :valueType       s/Str        ; int, long, string, assignment etc.
   })

(s/defschema ActivitySubmission
  {:projectId            Id
   :name                 s/Str
   (s/optional-key
     :description)       s/Str
   :dueDate              s/Str
   :measurementTemplates [CreateMeasurementTemplate]
   :assignee             s/Str
   :tags                 [s/Str]})

(s/defschema ActivitySubmissions
  {:activities [ActivitySubmission]
   :auth       Auth})


;;; ===========================PUT activities/dynamic===========================


(s/defschema DynamicActivitySubmission
  {:projectId     Id
   :name          s/Str
   :assignee      s/Str
   :dueDate       s/Str})

(s/defschema DynamicActivitySubmissions
  {:activities [DynamicActivitySubmission]
   :auth       Auth})


;;; ===================PUT templates/activities/instantiate=====================



(s/defschema ActivityTemplate
  {:id               Id
   :title            s/Str
   :description      s/Str
   ;; :taskTemplates    [TaskTemplate]
   })

(s/defschema ActivityTemplatesDiff
  {:insert [ActivityTemplate]
   :delete [Id]})


;;; =============================PUT templates/activities=======================


(s/defschema MeasurementTemplateTemplateSubmission
  {:question              s/Str
   (s/optional-key
     :hint)               s/Str
   (s/optional-key
     :validations)        [Validation]
   (s/optional-key
     :required)           s/Bool
   :valueType             s/Str
   (s/optional-key
     :defaultValue)       s/Str})

(s/defschema TaskTemplateSubmission
  {:name                  s/Str
   :description           s/Str
   :measurementTemplates [MeasurementTemplateTemplateSubmission]
   :tags                  [s/Str]
   :children              [(s/recursive #'TaskTemplateSubmission)]})

(s/defschema ActivityTemplateSubmission
  {:title                 s/Str
   :description           s/Str
   :tasks                 [TaskTemplateSubmission]})

(s/defschema ActivityTemplateSubmissions
  {:activityTemplates    [ActivityTemplateSubmission]
   :auth                  Auth})


;;; ==================================init======================================


(s/defschema Init
  {:username s/Str
   :password s/Str
   (s/optional-key :demo) s/Bool})


;;; ================================upload======================================


(s/defschema Filepath
  {:filepath s/Str})


;;; ==============================download======================================


(s/defschema FileManifest
  {:filename s/Str
   :auth     Auth})


(s/defschema File
  {:file java.io.File})


;;===================================update-db==================================


(s/defschema DB
  {:username s/Str
   :password s/Str
   (s/optional-key :init) s/Bool})
