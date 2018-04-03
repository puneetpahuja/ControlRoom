(ns dtm.write
  (:require [dtm.util :as util]
            [dtm.convert :as convert]
            [clojure.string :as s]
            [data.util :as data-util]))


;;; ====================================tasks===================================


(defn add-namespace [namespace cmap]
  (let [transformer (comp keyword
                          (partial str
                                   namespace
                                   "/")
                          name)]
    (into {}
          (for [[k v] cmap]
            [(transformer k) v]))))

(defn measurement [m]
  (let [{:keys
         [id
          valueType
          value]}   m
        db          (util/get-db)
        m-e         (util/get-attr
                      :measurement-template/measurement
                      :measurement-template/id
                      (util/str->uuid id)
                      db)
        m-details   (util/get-details m-e)
        m-namespace (-> m-details
                        convert/entity-map
                        keys
                        first
                        namespace)
        m-id        (-> m-details
                        convert/keys-emap
                        :id)
        tx          {:id    m-id
                     :value (case m-namespace
                              "assignment-measurement"
                              [:user/username value]

                              ("float-measurement" "integer-measurement")
                              (read-string value)

                              value)}]
    (add-namespace m-namespace
                   (if (= m-namespace "any-measurement")
                     (assoc tx :value-type valueType)
                     tx))))

(defn task [t]
  (let [{:keys
         [id
          status]}      t
        db              (util/get-db)
        details         (-> (util/get-details :task/id id db)
                            convert/keys-emap)
        {:keys
         [parent
          firstChild
          sibling]}     details

        parent-id       (:task/id (util/get-details parent))
        first-child-id  (:task/id (util/get-details firstChild))
        sibling-id      (:task/id (util/get-details sibling))]

    (case status
      "completed"
      (cond
        sibling-id
        [{:task/id id
          :task/status :task.status/completed
          :task/completed-at (data-util/now)}
         {:task/id sibling-id
          :task/status :task.status/pending}]

        parent-id
        (into [{:task/id id
                :task/status :task.status/completed
                :task/completed-at (data-util/now)}]
              (task {:id parent-id
                     :status "completed"}))

        :else
        [{:task/id id
          :task/status :task.status/completed
          :task/completed-at (data-util/now)}])

      "assigned"
      (cond
        first-child-id
        [{:task/id id
          :task/status :task.status/assigned}
         {:task/id first-child-id
          :task/status :task.status/pending}]

        :else
        []))))

(defn split-task-and-measurement-data [tasks]
  (let [ts (mapv #(select-keys % [:id :status])
                 tasks)
        ts (mapv #(update % :id util/str->uuid)
                 ts)
        ms (->> tasks
                (mapv :measurements)
                util/concat-lists)]
    {:tasks ts
     :measurements ms}))

;; TODO : Do we want to check if the task is assigned to this user or not and also
;;        whether the measurement templates belong to the task or not?
(defn tasks [_username tasks]
  (let [splitted-data   (split-task-and-measurement-data tasks)
        ms              (:measurements splitted-data)
        ms-tx           (mapv measurement ms)
        ts              (:tasks splitted-data)
        ts-tx           (util/concat-lists (mapv task ts))
        tx              (into ms-tx ts-tx)]
    (util/transact tx)
    {:result true}))


;;; ================================activities==================================


(defn measurement-tx [mt]
  (let [{:keys
         [valueType]} mt
        tx            {:id (util/uuid)}
        namespace     (str valueType "-measurement")]
    (add-namespace namespace tx)))

(defn measurement-template-tx [mt]
  (let [{:keys
         [question
          hint
          required
          valueType]} mt
        m-namespace   (str valueType "-measurement")
        tx            {:id (util/uuid)
                       :question question
                       :hint hint
                       :required required
                       :value-type (->> valueType
                                        (str "measurement.value-type/")
                                        keyword)
                       :measurement (measurement-tx mt)}]
    (add-namespace "measurement-template" tx)))

(defn a-measurement-tx [name assignee-username]
  (let  [tx {:id    (util/uuid)
             :name  name
             :value [:user/username assignee-username]}]
    (add-namespace "assignment-measurement" tx)))

(defn a-measurement-template-tx [activity]
  (let [{:keys
         [name
          assignee]}   activity
        id    (util/uuid)
        tx    {:id          id
               :question    (str "Assign " name)
               :required    true
               :value-type  :measurement.value-type/assignment
               :measurement (a-measurement-tx (str id)
                                              assignee)}]
    (add-namespace "measurement-template" tx)))

(defn m-task-tx [activity]
  (let [{:keys
         [name
          measurementTemplates
          dueDate
          assignee]}            activity
        id     (util/uuid)
        tx
        {:id                    id
         :name                  name
         :measurement-templates (-> (mapv measurement-template-tx measurementTemplates)
                                    (util/add-position :measurement-template/position))
         :type                  :task.type/measurement
         :status                :task.status/pending
         :due-date              dueDate
         :created-at            (data-util/now)
         :updated-at            (data-util/now)}]
    (add-namespace "task" tx)))

(defn a-task-tx [assigner activity]
  (let [{:keys
         [name
          dueDate
          tags]}      activity
        id               (util/uuid)
        mt               (a-measurement-template-tx activity)
        tx
        {:id                    id
         :name                  (str "Assign " name)
         :measurement-templates (util/add-position [mt] :measurement-template/position)
         :type                  :task.type/assignment
         :status                :task.status/assigned
         :assigned-to           (a-measurement-tx (str id)
                                                  assigner)
         :due-date              dueDate
         :first-child           (m-task-tx activity)
         :created-at            (data-util/now)
         :updated-at            (data-util/now)
         :tags                  tags}]
    (add-namespace "task" tx)))

(defn activity [owner activity]
  (let [{:keys
         [projectId
          name
          dueDate
          tags]}  activity
        tx            {:project/id
                       (util/str->uuid projectId)

                       :project/activities
                       (add-namespace "activity"
                                      {:id          (util/uuid)
                                       :name        name
                                       :owner       [:user/username owner]
                                       :created-at  (data-util/now)
                                       :updated-at  (data-util/now)
                                       :due-date    dueDate
                                       :root        (a-task-tx owner activity)})}]
    ;; (clojure.pprint/pprint tx)
    (util/transact [tx])
    (let [m-task-id
          (-> tx
              :project/activities
              :activity/root
              :task/first-child
              :task/id)
          m-task-a-measument-id
          (-> tx
              :project/activities
              :activity/root
              :task/measurement-templates
              first
              :measurement-template/measurement
              :assignment-measurement/id)
          a-task-id
          (-> tx
              :project/activities
              :activity/root
              :task/id)
          tx
          {:task/id m-task-id
           :task/assigned-to
           [:assignment-measurement/id
            m-task-a-measument-id]
           :task/parent
           [:task/id a-task-id]
           :task/tags tags}]
      ;; (clojure.pprint/pprint tx)
      (util/transact [tx]))))

(defn activities [username activities]
  (mapv (partial activity username) activities)
  {:result true})


;;; ================================user========================================

(defn assoc-non-nil
  ([cmap key val]
   (if val
     (assoc cmap key val)
     cmap))

  ([cmap key val & kvs]
   (let [ret (assoc-non-nil cmap key val)]
     (if kvs
       (if (next kvs)
         (recur ret (first kvs) (second kvs) (nnext kvs))
         (throw (IllegalArgumentException.
                  "assoc-non-nil expects even number of arguments after map/vector, found odd number")))
       ret))))

(defn gen-password
  ([]
   (gen-password 10))
  ([n]
   (let [chars (->> (range 33 127)
                    (remove #{34 92})
                    (map char))]
     (apply str
            (take n (repeatedly #(rand-nth chars)))))))

(defn user [_ user-details]
  (let [{:keys [firstName lastName title
                phone email orgUnit state
                password]} user-details
        pass (if password
               password
               (gen-password))]
    (-> {}
        (assoc :id (util/uuid))
        (assoc-non-nil :first-name firstName
                       :last-name lastName
                       :title title
                       :username phone
                       :phone phone
                       :email email
                       ;; TODO :channel
                       :password pass))))
