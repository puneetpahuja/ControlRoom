(ns dtm.schema
  (:require [datomic-schema.schema :as s]
            [dtm.util :as util]))

;; :keyword :string :boolean :long :bigint :float :double :bigdec :ref :instant
;; :uuid :uri :bytes :enum

;; :unique-value :unique-identity :indexed :many :fulltext :component :nohistory
;; "Some doc string" [:arbitrary "Enum" :values] :alter!

(def schemas
  [;; these comments are for one to one correspondence between api.schema and this dtm.schema

   ;; `Auth` is in `user`

   ;; `Manifest` is not needed here


;;; ================================login=======================================

   ;; `Credentials` is in `user`

   (s/schema user
             (s/fields
               [id :uuid :unique-identity]
               [first-name :string]
               [last-name :string]

               [title :string]
               [username :string :unique-identity]
               [phone :string]

               [email :string]
               [role :ref]
               [channels :ref :many]

               [password :string]
               [api-key :string]))

   ;; `UserAuth` is made from `user`


;;; ================================logout======================================


   (s/schema result
             (s/fields
               [result :boolean]))


;;; ================================org-units===================================


   (s/schema client
             (s/fields
               [id :uuid :unique-identity]
               [name :string]
               [projects :ref :many :component]))

   (s/schema org-units
             (s/fields
               [version :long]))

   (s/schema project
             (s/fields
               [id :uuid :unique-identity]
               [name :string]
               [states :ref :many]
               [activities :ref :many :component]))

   (s/schema state
             (s/fields
               [id :uuid :unique-identity]
               [name :string]
               [verticals :ref :many]))

   (s/schema vertical
             (s/fields
               [id :uuid :unique-identity]
               [name :string]
               [users :ref :many]))


;;; ================================tasks/*=====================================


   (s/schema validation
             (s/fields
               [id :uuid :unique-identity]))

   (s/schema integer-measurement
             (s/fields
               [id :uuid :unique-identity]
               [name :string]
               [value :long]))

   (s/schema string-measurement
             (s/fields
               [id :uuid :unique-identity]
               [name :string]
               [value :string]))

   (s/schema assignment-measurement
             (s/fields
               [id :uuid :unique-identity]
               [name :string]
               [value :ref]))

   (s/schema photo-measurement
             (s/fields
               [id :uuid :unique-identity]
               [name :string]
               [value :string]))

   (s/schema date-measurement
             (s/fields
               [id :uuid :unique-identity]
               [name :string]
               [value :string]))

   (s/schema location-measurement
             (s/fields
               [id :uuid :unique-identity]
               [name :string]
               [value :string]))

   (s/schema float-measurement
             (s/fields
               [id :uuid :unique-identity]
               [name :string]
               [value :float]))

   (s/schema any-measurement
             (s/fields
               [id :uuid :unique-identity]
               [name :string]
               [value :string]
               [value-type :string]))

   (s/schema measurement-template
             (s/fields
               [id :uuid :unique-identity]
               [question :string]
               [hint :string]

               [validations :ref :many]
               [required :boolean]
               [value-type :ref]

               [default-value :string]
               [measurement :ref]
               [position :long]))

   (s/schema datasource
             (s/fields
               [id :uuid :unique-identity]
               [measurements :ref :many :component]
               [tags :string :many :fulltext]

               [entity :string]))

   (s/schema task
             (s/fields
               [id :uuid :unique-identity]
               [name :string]
               [description :string]

               [measurement-templates :ref :many]
               [type :ref]
               [status :ref]

               [assigned-to :ref]   ; assignment-measurement
               [due-date :string]
               [completed-at :string]

               [parent :ref]
               [first-child :ref]
               [sibling :ref]

               [created-at :string]
               [updated-at :string]
               [tags       :string :many]))

   (s/schema task-tags
             (s/fields
               [version :long]
               [values :string :many]))

   (s/schema activity
             (s/fields
               [id :uuid :unique-identity]
               [name :string]
               [description :string]

               [root :ref]    ; root task
               [completed-at :string]
               [owner :ref]

               [created-at :string]
               [updated-at :string]
               [due-date :string]))


;;; ================================templates/activities========================


   (s/schema activity-template
             (s/fields
               [id :uuid :unique-identity]
               [title :string]
               [description :string]
               [project-schema-id :uuid :unique-identity]))])    ; TODO - make project-schema-id as ref which refs to project-schema

(def enums
  {:task-status    [{:db/ident :task.status/pre-pending}
                    {:db/ident :task.status/pending}
                    {:db/ident :task.status/completed}
                    {:db/ident :task.status/suspended}
                    {:db/ident :task.status/rejected}
                    {:db/ident :task.status/assigned}]

   :task-type      [{:db/ident :task.type/assignment}
                    {:db/ident :task.type/measurement}]

   :user-role      [{:db/ident :user.role/worker}
                    {:db/ident :user.role/supervisor}]

   :user-channels  [{:db/ident :user.channel/app}
                    {:db/ident :user.channel/sms}
                    {:db/ident :user.channel/web}]

   :m-value-types  [{:db/ident :measurement.value-type/integer}
                    {:db/ident :measurement.value-type/float}
                    {:db/ident :measurement.value-type/string}
                    {:db/ident :measurement.value-type/photo}
                    {:db/ident :measurement.value-type/location}
                    {:db/ident :measurement.value-type/date}
                    {:db/ident :measurement.value-type/any}
                    {:db/ident :measurement.value-type/assignment}]})

(def generated-schema (mapv #(dissoc % :db.install/_attribute :db/id)
                            (s/generate-schema schemas {:gen-all? false})))

(def schema (vec (concat
                   (util/concat-lists (vals enums))
                   generated-schema)))
