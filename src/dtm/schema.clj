(ns dtm.schema
  (:require [datomic-schema.schema :as s]
            [dtm.util :as util]))

;; :keyword :string :boolean :long :bigint :float :double :bigdec :ref :instant
;; :uuid :uri :bytes :enum

;; :unique-value :unique-identity :indexed :many :fulltext :component :nohistory
;; "Some doc string" [:arbitrary "Enum" :values] :alter!

(def schemas
  [;; `credentials` and `auth` are in `user`

   (s/schema user
             (s/fields
               [id :uuid :unique-identity]
               [first-name :string] ;; remove this
               [last-name :string]
               [title :string]
               [username :string :unique-identity]
               [phone :string]
               [email :string]
               [role :ref]
               [org-unit :ref]
               [channels :ref :many]
               [password :string]
               [api-key :string]))

   (s/schema result
             (s/fields
               [result :boolean]))

   (s/schema org-unit
             (s/fields
               [id :uuid :unique-identity]
               [name :string]
               [users :ref :many]))

   (s/schema task
             (s/fields
               [id :uuid :unique-identity]
               [name :string]
               [description :string]
               [project :ref]
               [measurements :ref :many]
               [type :ref]
               [status :ref]
               [assigned-to :ref]
               [assigned-by :ref]
               [parent :ref]
               [first-child :ref]
               [sibling :ref]
               [created-at :string]
               [updated-at :string]
               [due-date :string]
               [completed-at :string]))

   (s/schema project
             (s/fields
               [id :uuid :unique-identity]
               [name :string]
               [description :string]
               [root :ref]
               [status :ref]
               [client :ref]
               [owner :ref]
               [created-at :string]
               [updated-at :string]
               [due-date :string]
               [completed-at :string]))

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
               [value :ref]
               [datasource :ref]))

   (s/schema datasource
             (s/fields
               [id :uuid :unique-identity]
               [name :string]
               [measurements :ref :many :component]
               [tags :string :many :fulltext]
               [entity :ref]))])

(def enums
  {:task-status [{:db/ident :task.status/pre-pending}
                 {:db/ident :task.status/pending}
                 {:db/ident :task.status/completed}
                 {:db/ident :task.status/suspended}
                 {:db/ident :task.status/rejected}
                 {:db/ident :task.status/assigned}]

   :project-status [{:db/ident :project.status/pending}
                    {:db/ident :project.status/completed}
                    {:db/ident :project.status/rejected}]

   :task-type [{:db/ident :task.type/assignment}
               {:db/ident :task.type/measurement}]

   :user-role [{:db/ident :user.role/worker}
               {:db/ident :user.role/supervisor}]

   :user-channels [{:db/ident :user.channel/app}
                   {:db/ident :user.channel/sms}
                   {:db/ident :user.channel/web}]})

(def generated-schema (mapv #(dissoc % :db.install/_attribute :db/id)
                            (s/generate-schema schemas {:gen-all? false})))

(def schema (vec (concat
                   (util/concat-lists (vals enums))
                   generated-schema)))
