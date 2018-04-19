(ns dashboard.postgres
  (:require [camel-snake-kebab.core :as convert-case]
            [config.postgres :as config]
            [dtm.util :as util]
            [clojure.java.jdbc :as j]
            [dashboard.convert :as c]
            [ring.util.http-response :as response]
            [clojure.tools.trace :as trace]))

(def convert {:entities convert-case/->snake_case})

(defn get-all-rows [attr]
  (let [db (util/get-db)]
    (as-> (util/get-all-vals attr db) x
      (map #(util/filter-nil (util/get-details attr % db)) x))))

(defn exec [& commands]
  (j/with-db-connection [conn config/db-spec]
    (j/db-do-commands conn commands)))

(defn drop-tables [& tables]
  (apply exec (map #(j/drop-table-ddl %) tables)))

(defn create-table [table columns]
  (exec (j/create-table-ddl table
                            columns
                            convert)))

(defn empty-tables [& tables]
  (apply exec (map (comp (partial str "DELETE FROM ")
                         name)
                   tables)))

(defn create-users-table []
  (create-table :users [[:id :uuid :primary :key]
                        [:first-name :text]
                        [:last-name :text]
                        [:title :text]
                        [:username :text]
                        [:phone :text]
                        [:email :text]
                        [:role :text]
                        [:channels "text[]"]]))

(defn create-tasks-table []
  (create-table :tasks [[:id :uuid :primary :key]
                        [:name :text]
                        [:description :text]
                        [:measurement-templates "uuid[]"]
                        [:type :text]
                        [:status :text]
                        [:assigned-to :uuid]
                        [:due-date :text]
                        [:completed-at :text]
                        [:parent :uuid]
                        [:first-child :uuid]
                        [:sibling :uuid]
                        [:created-at :text]
                        [:updated-at :text]
                        [:tags "text[]"]]))

(defn create-clients-table []
  (create-table :clients [[:id :uuid :primary :key]
                          [:name :text]
                          [:projects "uuid[]"]]))

(defn create-projects-table []
  (create-table :projects [[:id :uuid :primary :key]
                           [:name :text]
                           [:states "uuid[]"]
                           [:activities "uuid[]"]]))

(defn create-states-table []
  (create-table :states [[:id :uuid :primary :key]
                         [:name :text]
                         [:verticals "uuid[]"]]))

(defn create-verticals-table []
  (create-table :verticals [[:id :uuid :primary :key]
                            [:name :text]
                            [:users "uuid[]"]]))

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

(defn create-task-tags-table []
  (create-table :task_tags [[:values "text[]"]]))

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

(defn create-all-tables []
  (create-users-table)
  (create-tasks-table)
  (create-clients-table)
  (create-projects-table)
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
  (create-task-tags-table)
  (create-activities-table))

(defn insert [table row]
  (j/with-db-connection [conn config/db-spec]
    (j/insert! conn table row convert)))

(defn fill-table [table attr convert-f]
  (let [dtm-rows (get-all-rows attr)
        converted-rows (map convert-f dtm-rows)]
    (map (partial insert table) converted-rows)))

(defn fill-users-table []
  (fill-table :users :user/id c/user))

(defn fill-tasks-table []
  (fill-table :tasks :task/id c/task))

(defn fill-clients-table []
  (fill-table :clients :client/id c/client))

(defn fill-projects-table []
  (fill-table :projects :project/id c/project))

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

(defn fill-task-tags-table []
  (fill-table :task_tags :task-tags/version c/task-tags))

(defn fill-activities-table []
  (fill-table :activities :activity/id c/activity))

(defn fill-all-tables []
  (fill-users-table)
  (fill-tasks-table)
  (fill-clients-table)
  (fill-projects-table)
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
  (fill-task-tags-table)
  (fill-activities-table))


(defn cron-once []
  (trace/trace-ns 'dashboard.postgres)
  (j/query config/db-spec ["select pg_terminate_backend(pid) from pg_stat_activity where datname='hihdemo' and application_name='' and pid <> pg_backend_pid();"])
  (empty-tables :tasks
                :integer_measurements
                :string_measurements
                :assignment_measurements
                :photo_measurements
                :date_measurements
                :location_measurements
                :float_measurements
                :measurement_templates
                :activities)
  (fill-tasks-table)
  (fill-integer-measurements-table)
  (fill-string-measurements-table)
  (fill-assignment-measurements-table)
  (fill-photo-measurements-table)
  (fill-date-measurements-table)
  (fill-location-measurements-table)
  (fill-float-measurements-table)
  (fill-measurement-templates-table)
  (fill-activities-table)
  (j/query config/db-spec ["select pg_terminate_backend(pid) from pg_stat_activity where datname='hihdemo' and application_name='' and pid <> pg_backend_pid();"]))


(defn cron []
  (while true
    (println "refreshing")
    (cron-once)
    (Thread/sleep 3000)))



(defn update-db [{:keys [username password init]}]
  (trace/trace-ns 'dashboard.postgres)
  (if (and (= username "db-admin")
           (= password "update{DB}syvylyze<PostGres>"))
    (do
      (if init
        (do
          (create-all-tables)
          (fill-all-tables)))
      (cron-once)
      (response/ok {:result true}))
    (response/unauthorized {:error "wrong credentials"})))
