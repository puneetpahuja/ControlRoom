(ns dtm.write
  (:require [dtm.util :as util]
            [dtm.convert :as convert]
            [clojure.string :as s]))


;;; ====================================tasks===================================


(defn get-namespace [key]
  (-> key
      str
      (s/split #"/")
      first
      (subs 1)))

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
                        get-namespace)
        m-id        (-> m-details
                        convert/keys-emap
                        :id)]
    (add-namespace m-namespace
                   {:id    m-id
                    :value (case m-namespace
                             "assignment-measurement"
                             [:user/username value]

                             ("float-measurement" "integer-measurement")
                             (read-string value)

                             value)})))

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
          :task/status :task.status/completed}
         {:task/id sibling-id
          :task/status :task.status/pending}]

        parent-id
        (into [{:task/id id
                :task/status :task.status/completed}]
              (task {:id parent-id
                     :status "completed"}))

        :else
        [{:task/id id
          :task/status :task.status/completed}])

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
