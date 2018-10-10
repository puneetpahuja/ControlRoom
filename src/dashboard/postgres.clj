(ns dashboard.postgres
  (:require [camel-snake-kebab.core :as convert-case]
            [config.postgres :as config]
            [dtm.util :as util]
            [api.util :as api-util]
            [clojure.java.jdbc :as j]
            [dashboard.convert :as c]
            [ring.util.http-response :as response]
            [clojure.tools.trace :as trace]))

(def convert {:entities convert-case/->snake_case})

(def all-tables
  [:users :user_channels :tasks :task_measurement_templates :task_tags :clients
   :projects :project_activities :states :verticals :integer_measurements
   :string_measurements :assignment_measurements :photo_measurements
   :date_measurements :location_measurements :float_measurements
   :measurement_templates :datasources :tags :activities :activity_tasks])

(defn get-all-rows [attr]
  (let [db (util/get-db)]
    (as-> (util/get-all-vals attr db) x
      (map #(util/filter-nil (util/get-details attr % db)) x))))

(defn exec [& commands]
  (j/with-db-connection [conn config/db-spec]
    (j/db-do-commands conn commands)))

(defn drop-tables [& tables]
  (apply exec (map #(j/drop-table-ddl % {:conditional? true}) tables)))

(defn drop-all-tables []
  (apply drop-tables all-tables))

(defn create-table [table columns]
  (exec (j/create-table-ddl table
                            columns
                            convert)))

(defn empty-tables [& tables]
  (apply exec (map (comp (partial str "DELETE FROM ")
                         name)
                   tables)))

(defn empty-all-tables []
  (apply empty-tables all-tables))

(defn create-users-table []
  (create-table :users [[:id :uuid :primary :key]
                        [:first-name :text]
                        [:last-name :text]
                        [:title :text]
                        [:username :text]
                        [:phone :text]
                        [:email :text]
                        [:role :text]]))

(defn create-user-channels-table []
  (create-table :user_channels [[:id :uuid]
                                [:channel :text ", UNIQUE (id, channel)"]]))

(defn create-tasks-table []
  (create-table :tasks [[:id :uuid :primary :key]
                        [:name :text]
                        [:description :text]
                        [:type :text]
                        [:status :text]
                        [:assigned-to :uuid]
                        [:due-date :text]
                        [:completed-at :text]
                        [:parent :uuid]
                        [:first-child :uuid]
                        [:sibling :uuid]
                        [:created-at :text]
                        [:updated-at :text]]))

(defn create-task-measurement-templates-table []
  (create-table :task_measurement_templates [[:id :uuid]
                                             [:measurement-template :uuid ", UNIQUE (id, measurement_template)"]]))

(defn create-task-tags-table []
  (create-table :task_tags [[:id :uuid]
                            [:tag :text ", UNIQUE (id, tag)"]]))


(defn create-clients-table []
  (create-table :clients [[:id :uuid]
                          [:name :text]
                          [:project :uuid ", UNIQUE (id, name, project)"]]))

(defn create-projects-table []
  (create-table :projects [[:id :uuid :primary :key]
                           [:name :text]
                           [:state :uuid]]))

(defn create-project-activities-table []
  (create-table :project_activities [[:id :uuid]
                                     [:activity :uuid ", UNIQUE (id, activity)"]]))

(defn create-states-table []
  (create-table :states [[:id :uuid]
                         [:name :text]
                         [:vertical :uuid ", UNIQUE (id, name, vertical)"]]))

(defn create-verticals-table []
  (create-table :verticals [[:id :uuid]
                            [:name :text]
                            [:usr :uuid ", UNIQUE (id, name, usr)"]]))

(defn create-measurements-table [table type]
  (create-table table [[:id :uuid :primary :key]
                       [:name :text]
                       [:value type]]))

(defn create-integer-measurements-table []
  (create-measurements-table :integer_measurements :bigint))

(defn create-string-measurements-table []
  (create-measurements-table :string_measurements :text))

(defn create-assignment-measurements-table []
  (create-measurements-table :assignment_measurements :uuid))

(defn create-photo-measurements-table []
  (create-measurements-table :photo_measurements :text))

(defn create-date-measurements-table []
  (create-measurements-table :date_measurements :text))

(defn create-location-measurements-table []
  (create-measurements-table :location_measurements :text))

(defn create-float-measurements-table []
  (create-measurements-table :float_measurements :real))

(defn create-measurement-templates-table []
  (create-table :measurement_templates [[:id :uuid :primary :key]
                                        [:question :text]
                                        [:hint :text]
                                        [:required :boolean]
                                        [:value-type :text]
                                        [:default-value :text]
                                        [:measurement :uuid]]))

(defn create-datasources-table []
  (create-table :datasources [[:id :uuid :primary :key]
                              [:measurements "uuid[]"]
                              [:tags "text[]"]
                              [:entity :text]]))

(defn create-tags-table []
  (create-table :tags [[:value :text]]))

(defn create-activities-table []
  (create-table :activities [[:id :uuid :primary :key]
                             [:name :text]
                             [:description :text]
                             [:root :uuid]
                             [:completed-at :text]
                             [:owner :uuid]
                             [:created-at :text]
                             [:updated-at :text]
                             [:due-date :text]]))

(defn create-activity-tasks-table []
  (create-table :activity_tasks [[:id :uuid]
                                 [:task :uuid ", UNIQUE (id, task)"]]))

(defn create-all-tables []
  (create-users-table)
  (create-user-channels-table)
  (create-tasks-table)
  (create-task-measurement-templates-table)
  (create-task-tags-table)
  (create-clients-table)
  (create-projects-table)
  (create-project-activities-table)
  (create-states-table)
  (create-verticals-table)
  (create-integer-measurements-table)
  (create-string-measurements-table)
  (create-assignment-measurements-table)
  (create-photo-measurements-table)
  (create-date-measurements-table)
  (create-location-measurements-table)
  (create-float-measurements-table)
  (create-measurement-templates-table)
  (create-datasources-table)
  (create-tags-table)
  (create-activities-table)
  (create-activity-tasks-table))

(defn insert [table rows]
  (j/with-db-connection [conn config/db-spec]
    (if (map? rows)
      (j/insert! conn table rows convert)
      (j/insert-multi! conn table rows convert))))

(defn fill-table [table attr convert-f]
  (let [dtm-rows (get-all-rows attr)
        converted-rows (map convert-f dtm-rows)]
    (map (partial insert table) converted-rows)))

(defn fill-users-table []
  (fill-table :users :user/id c/user))

(defn fill-user-channels-table []
  (fill-table :user_channels :user/id c/user-channel))

(defn fill-tasks-table []
  (fill-table :tasks :task/id c/task))

(defn fill-task-measurement-templates-table []
  (fill-table :task_measurement_templates :task/id c/task-measurement-template))

(defn fill-task-tags-table []
  (fill-table :task_tags :task/id c/task-tag))

(defn fill-clients-table []
  (fill-table :clients :client/id c/client))

(defn fill-projects-table []
  (fill-table :projects :project/id c/project))

(defn fill-project-activities-table []
  (fill-table :project_activities :project/id c/project-activity))

(defn fill-states-table []
  (fill-table :states :state/id c/state))

(defn fill-verticals-table []
  (fill-table :verticals :vertical/id c/vertical))

(defn fill-integer-measurements-table []
  (fill-table :integer_measurements :integer-measurement/id c/integer-measurement))

(defn fill-string-measurements-table []
  (fill-table :string_measurements :string-measurement/id c/string-measurement))

(defn fill-assignment-measurements-table []
  (fill-table :assignment_measurements :assignment-measurement/id c/assignment-measurement))

(defn fill-photo-measurements-table []
  (fill-table :photo_measurements :photo-measurement/id c/photo-measurement))

(defn fill-date-measurements-table []
  (fill-table :date_measurements :date-measurement/id c/date-measurement))

(defn fill-location-measurements-table []
  (fill-table :location_measurements :location-measurement/id c/location-measurement))

(defn fill-float-measurements-table []
  (fill-table :float_measurements :float-measurement/id c/float-measurement))

(defn fill-measurement-templates-table []
  (fill-table :measurement_templates :measurement-template/id c/measurement-template))

(defn fill-datasources-table []
  (fill-table :datasources :datasource/id c/datasource))

(defn fill-tags-table []
  (fill-table :tags :task-tags/version c/tag))

(defn fill-activities-table []
  (fill-table :activities :activity/id c/activity))

(defn fill-activity-tasks-table []
  (fill-table :activity_tasks :activity/id c/activity-task))

(defn fill-all-tables []
  (fill-users-table)
  (fill-user-channels-table)
  (fill-tasks-table)
  (fill-task-measurement-templates-table)
  (fill-task-tags-table)
  (fill-clients-table)
  (fill-projects-table)
  (fill-project-activities-table)
  (fill-states-table)
  (fill-verticals-table)
  (fill-integer-measurements-table)
  (fill-string-measurements-table)
  (fill-assignment-measurements-table)
  (fill-photo-measurements-table)
  (fill-date-measurements-table)
  (fill-location-measurements-table)
  (fill-float-measurements-table)
  (fill-measurement-templates-table)
  (fill-datasources-table)
  (fill-tags-table)
  (fill-activities-table)
  (fill-activity-tasks-table))

(defn cron-once []
  (trace/trace-ns 'dashboard.postgres)
  (j/query config/db-spec ["select pg_terminate_backend(pid) from pg_stat_activity where datname='hihdemo' and application_name='' and pid <> pg_backend_pid();"])
  (empty-tables :tasks
                :task_measurement_templates
                :task_tags
                :integer_measurements
                :string_measurements
                :assignment_measurements
                :photo_measurements
                :date_measurements
                :location_measurements
                :float_measurements
                :measurement_templates
                :activities
                :activity_tasks)
  (fill-tasks-table)
  (fill-task-measurement-templates-table)
  (fill-task-tags-table)
  (fill-integer-measurements-table)
  (fill-string-measurements-table)
  (fill-assignment-measurements-table)
  (fill-photo-measurements-table)
  (fill-date-measurements-table)
  (fill-location-measurements-table)
  (fill-float-measurements-table)
  (fill-measurement-templates-table)
  (fill-activities-table)
  (fill-activity-tasks-table)
  (j/query config/db-spec ["select pg_terminate_backend(pid) from pg_stat_activity where datname='hihdemo' and application_name='' and pid <> pg_backend_pid();"]))

(defn cron []
  (while true
    (println "refreshing")
    (cron-once)
    (Thread/sleep 3000)))

(defn update-db [{:keys [username password init]}]
  (trace/trace-ns 'dashboard.postgres)
  (if (api-util/db-admin? username password)
    (do
      (if init
        (do
          (empty-all-tables)
          (fill-all-tables)))
      (cron-once)
      (response/ok {:result true}))
    (response/unauthorized {:error "wrong credentials"})))
