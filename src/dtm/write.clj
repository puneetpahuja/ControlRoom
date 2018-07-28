(ns dtm.write
  (:require [dtm.util :as util]
            [dtm.convert :as convert]
            [clojure.string :as s]
            [data.util :as data-util]
            ;;debug
            [clojure.tools.trace :as t]))


;;; ====================================tasks===================================


;; str in the realm of keywords. can take strings or keywords. outputs a keyword.
;; (str-keyword :heot "/" :tehua :eh "/" "wutu") -> :heot/tehuaeh/wutu
(defn str-keyword [& keywords-or-strs]
  (->> keywords-or-strs
       (map name)
       (apply str)
       keyword))

;; adds `namespace` to all keys of `cmap`. `namespace` can be a string or keyword
;; (add-namespace :ttt {:a :b})       -> {:ttt/a :b}
;; (add-namespace "ttt" {:a :b})      -> {:ttt/a :b}
;; (add-namespace "ttt.nrn" {:a :b})  -> {:ttt.nrn/a :b}
;; (add-namespace :ttt.tt {:a :b})    -> {:ttt.tt/a :b}
(defn add-namespace [namespace cmap]
  (let [transformer (partial str-keyword namespace "/")]
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

;; todo - add user in the orgUnit of the state
;; check if a user exists before transacting
(defn user [_ user-details]
  (let [{:keys [phone
                password]} user-details]
    (if (auth/user-exists? phone)
      (-> (util/get-details :user/username phone (util/get-db))
          convert/keys-emap
          ;; should we error out or only send username
          (select-keys [:username :password]))
      (let [same-properties    (select-keys user-details [:firstName :lastName :title
                                                          :phone :email :password])
            pass               (if password
                                 password
                                 (gen-password 12))]
        (as-> same-properties x
          (util/filter-nil x)
          (->kebab-case x)
          (assoc-non-nil x
                         :id       (util/uuid)
                         :password pass
                         :username phone
                         :api-key  (gen-password 20))
          (add-namespace "user" x)
          (util/transact [x]))
        {:username phone
         :password pass}))))


;;; ===============================activities/dynamic===========================

;; keep a track of used names and make sure to not use them
(defn simple-name [length separator]
  (let [words words/words3]
    (as-> (repeatedly length #(rand-nth words)) x
      (interleave x (repeat separator))
      (apply str (butlast x)))))

(defn activity-dynamic [owner activity]
  (let [{:keys
         [projectId
          name
          dueDate
          assignee]}  activity
        simple-name   (simple-name)
        tx            {:project/id
                       (util/str->uuid projectId)
                       :project/activities
                       (add-namespace "activity"
                                      {:id          (util/uuid)
                                       :name        name
                                       :description simple-name
                                       :owner       [:user/username owner]
                                       :created-at  (data-util/now)
                                       :updated-at  (data-util/now)
                                       :due-date    dueDate
                                       :root        (a-task-tx owner activity)})}]
    (user nil {:phone assignee})
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
           [:task/id a-task-id]}]
      (util/transact [tx])
      simple-name)))

(defn activities-dynamic [username activities]
  (mapv (partial activity-dynamic username) activities))


;;; ==============================PUT templates/activities======================


;; remove this later, debug purposes
(def p clojure.pprint/pprint)

(defn tree-add-uuid [tree]
  (cond
    (map? tree)
    (let [{:keys [children]} tree
          uuid (util/uuid)]
      (if children
        (assoc tree
               :id uuid
               :children (tree-add-uuid children))
        (assoc tree
               :id uuid)))

    (or (vector? tree) (seq? tree))
    (mapv tree-add-uuid tree)))

(defn parent-tx-helper [tree]
  (let [parent-id (:id tree)
        last-child-id (-> tree
                          :children
                          last
                          :id)]
    (if (and parent-id last-child-id)
      {:task/id last-child-id
       :task/parent [:task/id parent-id]})))

(defn listify-non-nil [elem]
  (if elem
    [elem]))

(defn parent-tx
  ([tree accumulated-tx]
   (cond
     (map? tree)
     (let [p-tx (parent-tx-helper tree)
           {:keys [children]} tree]
       (util/concatv accumulated-tx
                     (listify-non-nil p-tx)
                     (parent-tx children)))

     (or (vector? tree) (seq? tree))
     (util/concatv accumulated-tx
                   (flatten (mapv parent-tx tree)))))

  ([tree]
   (parent-tx tree [])))

(defn mt-tx [mt]
  (let [same-properties (select-keys mt [:question :hint :validations
                                         :required :defaultValue :position])
        {:keys
         [valueType]}   mt
        id              (util/uuid)
        tx              (as-> same-properties x
                          (assoc x
                                 :id        id
                                 :valueType (str-keyword "measurement.value-type/" valueType))
                          (add-namespace "measurement-template-template" x))]
    tx))

(defn mts-tx [mts]
  (let [mts-with-position (util/add-position mts)]
    (mapv mt-tx mts-with-position)))

(defn task-tx [details]
  (let [{:keys
         [measurementTemplates
          children]}             details
        id                       (util/uuid)
        same-properties          (select-keys details [:name :description :tags])
        type                     (if (empty? children) :task.type/measurement :task.type/assignment)
        mts                      (mts-tx measurementTemplates)
        tx                       (as-> same-properties x
                                   (assoc x
                                          :id                    id
                                          :type                  type
                                          :measurement-templates mts)
                                   (add-namespace "task-template" x))]
    tx))

(defn tasks-tx [tree]
  (cond
    (map? tree)
    (let [{:keys [children]} tree
          task-details       (task-tx tree)]
      (if (empty? children)
        task-details
        (assoc task-details :first-child (tasks-tx children))))

    (or (vector? tree) (seq? tree))
    (let [frst         (first tree)
          rst          (rest tree)
          task-details (if frst
                         (tasks-tx frst))]
      (cond
        (not (empty? rst))
        (assoc task-details :sibling (tasks-tx rst))

        frst
        task-details))))

(defn activity-template [owner template]
  (let [{:keys
         [tasks]}         template
        same-properties   (select-keys template [:title :description])
        id                (util/uuid)
        tasks-with-ids    (tree-add-uuid tasks)
        prnt-tx           (parent-tx tasks-with-ids)
        root              (tasks-tx tasks-with-ids)
        tx                (as-> same-properties x
                            (assoc x
                                   :id    id
                                   :root  root
                                   :owner [:user/username owner])
                            (add-namespace "activity-template" x))]
    (p tx)
    (p prnt-tx)))

(defn activity-templates [owner templates]
  (let [template (partial activity-template owner)]
    (map template templates)
    {:result true}))
