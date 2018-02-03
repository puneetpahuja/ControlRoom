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
                    :value (if (= m-namespace "assignment-measurement")
                             [:user/username value]
                             value)})))

(defn task [t]
  )

(defn split-task-and-measurement-data [tasks]
  {:tasks (mapv #(select-keys % [:id :status])
                tasks)
   :measurements (->> tasks
                      (mapv :measurements)
                      util/concat-lists)})

;; TODO : Do we want to check if the task is assigned to this user or not and also
;;        whether the measurement templates belong to the task or not?
(defn tasks [_username tasks]
  (let [splitted-data   (split-task-and-measurement-data tasks)
        ms              (:measurements splitted-data)
        m-tx            (mapv measurement ms)]
    ;;{:result true}
    m-tx))
