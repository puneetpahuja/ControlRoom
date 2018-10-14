(ns dashboard.convert
  (:refer-clojure :exclude [keys])
  (:require [dtm.convert :as c]
            [config.postgres :as config]
            [clojure.java.jdbc :as j]
            [dtm.util :as util]
            [clojure.string :as s]
            [clojure.set :as set])
  (:import [java.sql.Connection]))

(defn keys [emap]
  (let [cmap (c/entity-map emap)]
    (into {}
          (for [[k v] cmap]
            [(keyword (name k)) v]))))

(defn vec->array [vec vec-type arr-type]
  (let [conn (j/get-connection config/db-spec)]
    (.createArrayOf conn arr-type (into-array vec-type vec))))

(defn transform-map [m fs]
  (if (empty? fs)
    m
    (let [[k f]      (first fs)
          val        (k m)
          updated-m  (if (nil? val)
                       m
                       (assoc m k (f val)))]
      (transform-map updated-m (rest fs)))))

(defn transform-map-plus [m fs]
  (if (empty? fs)
    m
    (let [[k f]      (first fs)
          val        (k m)
          updated-m  (assoc m k (f val))]
      (transform-map-plus updated-m (rest fs)))))

(defn user [emap]
  (let [keys-converted (keys emap)
        keys-needed    (dissoc keys-converted :password :api-key :channels)
        vals-converted (transform-map keys-needed
                                      {:role name})]
    vals-converted))

;; (unfold-array {:a 1 :b 2 :cs [3 4 5]} :cs :c) or (unfold-array {:a 1 :b 2 :cs [3 4 5]} :cs) ->
;;   [{:a 1, :b 2, :c 3} {:a 1, :b 2, :c 4} {:a 1, :b 2, :c 5}]
(defn unfold-array
  ([cmap array-key new-key]
   (let [rest-map (dissoc cmap array-key)
         array    (array-key cmap)]
     (mapv (partial assoc rest-map new-key) array)))

  ;; new-key is array-key's last character removed
  ([cmap array-key]
   (let [new-key (->> array-key
                      name
                      drop-last
                      (s/join "")
                      keyword)]
     (unfold-array cmap array-key new-key))))

(defn user-channel [emap]
  (let [keys-converted (keys emap)
        keys-needed    (select-keys keys-converted [:id :channels])
        vals-converted (transform-map keys-needed {:channels (partial mapv name)})]
    (unfold-array vals-converted :channels)))

(defn task [emap]
  (let [keys-converted (keys emap)
        keys-needed    (dissoc keys-converted
                               :measurement-templates
                               :tags)
        get-task-id    (partial util/get-attr :task/id)
        val-fs         {:type name
                        :status name
                        :assigned-to (partial util/get-attr :assignment-measurement/id)
                        :parent get-task-id
                        :first-child get-task-id
                        :sibling get-task-id}]
    (transform-map keys-needed val-fs)))

(defn task-measurement-template [emap]
  (let [keys-converted                  (keys emap)
        keys-needed                     (select-keys keys-converted [:id :measurement-templates])
        get-measurement-templates-ids   (fn [mt-entities]
                                          (as-> (map util/get-details mt-entities) x
                                            (util/sort-by-position x :measurement-template/position)
                                            (map :measurement-template/id x)))
        vals-converted                  (transform-map
                                          keys-needed
                                          {:measurement-templates
                                           get-measurement-templates-ids})]
    (unfold-array vals-converted :measurement-templates)))

(defn task-tag [emap]
  (let [keys-converted (keys emap)
        keys-needed    (select-keys keys-converted [:id :tags])]
    (unfold-array keys-needed :tags)))

(defn get-attr-list
  ([attr convert-entities-f entities]
   (->> entities
        (map  (partial util/get-details))
        (map  convert-entities-f)
        (mapv attr)))

  ([attr entities]
   (get-attr-list attr identity entities)))

(defn get-attr-array
  ([attr convert-entities-f entities]
   (-> (get-attr-list attr convert-entities-f entities)
       (vec->array java.util.UUID "uuid")))

  ([attr entities]
   (get-attr-array attr identity entities)))

(defn client [emap]
  (let [keys-converted     (keys emap)
        get-projects-ids   (partial get-attr-list :project/id)
        val-fs             {:projects get-projects-ids}
        vals-converted     (transform-map keys-converted val-fs)]
    (unfold-array vals-converted :projects)))

(defn project [emap]
  (let [keys-converted (keys emap)
        keys-needed    (dissoc keys-converted :activities)
        get-state-id   (fn [entities]
                         (->> entities
                              (get-attr-list :state/id)
                              first))
        val-fs         {:states get-state-id}]
    (-> keys-needed
        (transform-map val-fs)
        (set/rename-keys {:states :state}))))

(defn project-activity [emap]
  (let [keys-converted     (keys emap)
        keys-needed        (select-keys keys-converted [:id :activities])
        get-activities-ids (partial get-attr-list :activity/id)]
    (-> keys-needed
        (transform-map {:activities get-activities-ids})
        (unfold-array :activities :activity))))

(defn state [emap]
  (let [keys-converted    (keys emap)
        get-verticals-ids (partial get-attr-list :vertical/id)
        val-fs            {:verticals get-verticals-ids}]
    (-> keys-converted
        (transform-map val-fs)
        (unfold-array :verticals))))

(defn vertical [emap]
  (let [keys-converted  (keys emap)
        get-users-ids   (partial get-attr-list :user/id)
        val-fs          {:users get-users-ids}]
    (-> keys-converted
        (transform-map val-fs)
        (set/rename-keys {:users :usrs})
        (unfold-array :usrs))))

(defn measurement [emap]
  (let [keys-converted (keys emap)]
    keys-converted))

(def integer-measurement measurement)

(def string-measurement measurement)

(defn assignment-measurement [emap]
  (transform-map (measurement emap)
                 {:value (partial util/get-attr :user/id)}))

(def photo-measurement measurement)

(def date-measurement measurement)

(def location-measurement measurement)

(def float-measurement measurement)

(defn measurement-template [emap]
  (let [keys-converted      (keys emap)
        keys-needed         (dissoc keys-converted
                                    :position
                                    :validations)
        get-measurement-id  (fn [measurement-entity]
                              (-> measurement-entity
                                  util/get-details
                                  keys
                                  :id))
        val-fs              {:value-type name
                             :measurement get-measurement-id}]
    (transform-map keys-needed val-fs)))

(defn datasource [emap]
  (let [keys-converted       (keys emap)
        get-measurements-ids (partial get-attr-array :id keys)
        get-tags             #(vec->array % String "text")
        val-fs               {:measurements get-measurements-ids
                              :tags get-tags}]
    (transform-map keys-converted val-fs)))

(defn tag [emap]
  (let [keys-converted  (keys emap)
        keys-needed     (dissoc keys-converted :version)]
    (unfold-array keys-needed :values)))

(defn activity [emap]
  (let [keys-converted  (keys emap)
        keys-needed     (dissoc keys-converted :tasks)
        get-root        (partial util/get-attr :task/id)
        get-owner       (partial util/get-attr :user/id)
        val-fs          {:root get-root
                         :owner get-owner}]
    (transform-map keys-needed val-fs)))

(defn activity-task [emap]
  (if (:activity/root emap)
    (let [keys-converted (keys emap)
          keys-needed    (select-keys keys-converted [:id :tasks])
          get-root       (partial util/get-attr :task/id)
          root-task-id   (get-root (:root keys-converted))
          val-fs         {:tasks (fn [_]
                                   (util/connected-tasks root-task-id (util/get-db)))}]
      (-> keys-needed
          (transform-map-plus val-fs)
          (unfold-array :tasks)))))
