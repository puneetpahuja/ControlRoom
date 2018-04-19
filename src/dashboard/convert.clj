(ns dashboard.convert
  (:refer-clojure :exclude [keys])
  (:require [dtm.convert :as c]
            [config.postgres :as config]
            [clojure.java.jdbc :as j]
            [dtm.util :as util])
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
    (let [[k f]    (first fs)
          val      (k m)
          updated-m (if (nil? val)
                      m
                      (assoc m k (f val)))]
      (transform-map updated-m (rest fs)))))

(defn transform-map-plus [m fs]
  (if (empty? fs)
    m
    (let [[k f]    (first fs)
          val      (k m)
          updated-m (assoc m k (f val))]
      (transform-map-plus updated-m (rest fs)))))

(defn user [emap]
  (let [keys-converted (keys emap)
        keys-needed    (dissoc keys-converted :password :api-key)
        vals-converted (transform-map keys-needed
                                      {:role name
                                       :channels #(vec->array (map name %)
                                                              String
                                                              "text")})]
    vals-converted))

(defn task [emap]
  (let [keys-converted                  (keys emap)
        get-measurement-templates-ids   (fn [mt-entities]
                                          (as-> (map util/get-details mt-entities) x
                                            (util/sort-by-position x :measurement-template/position)
                                            (map :measurement-template/id x)
                                            (vec->array x java.util.UUID "uuid")))
        get-task-id                      (partial util/get-attr :task/id)
        val-fs         {:measurement-templates get-measurement-templates-ids
                        :type name
                        :status name
                        :assigned-to (partial util/get-attr :assignment-measurement/id)
                        :parent get-task-id
                        :first-child get-task-id
                        :sibling get-task-id
                        :tags #(vec->array % String "text")}]
    (transform-map keys-converted val-fs)))

(defn get-id-array
  ([attr convert-entities-f entities]
   (as-> entities x
     (map (partial util/get-details) x)
     (map convert-entities-f x)
     (map attr x)
     (vec->array x java.util.UUID "uuid")))

  ([attr entities]
   (get-id-array attr identity entities)))

(defn client [emap]
  (let [keys-converted (keys emap)
        get-projects-ids   (partial get-id-array :project/id)
        val-fs    {:projects get-projects-ids}]
    (transform-map keys-converted val-fs)))

(defn project [emap]
  (let [keys-converted (keys emap)
        get-states-ids (partial get-id-array :state/id)
        get-activities-ids (partial get-id-array :activity/id)
        val-fs {:states get-states-ids
                :activities get-activities-ids}]
    (transform-map keys-converted val-fs)))

(defn state [emap]
  (let [keys-converted (keys emap)
        get-verticals-ids (partial get-id-array :vertical/id)
        val-fs {:verticals get-verticals-ids}]
    (transform-map keys-converted val-fs)))

(defn vertical [emap]
  (let [keys-converted (keys emap)
        get-users-ids (partial get-id-array :user/id)
        val-fs {:users get-users-ids}]
    (transform-map keys-converted val-fs)))

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
  (let [keys-converted (keys emap)
        keys-needed  (dissoc keys-converted
                             :position
                             :validations)
        get-measurement-id  (fn [measurement-entity]
                              (-> (util/get-details measurement-entity)
                                  keys
                                  :id))
        val-fs {:value-type name
                :measurement get-measurement-id}]
    (transform-map keys-needed val-fs)))

(defn datasource [emap]
  (let [keys-converted (keys emap)
        get-measurements-ids (partial get-id-array :id keys)
        get-tags #(vec->array % String "text")
        val-fs {:measurements get-measurements-ids
                :tags get-tags}]
    (transform-map keys-converted val-fs)))

(defn task-tags [emap]
  (let [keys-converted (keys emap)
        keys-needed (dissoc keys-converted :version)
        get-values #(vec->array % String "text")
        val-fs {:values get-values}]
    (transform-map keys-needed val-fs)))

(defn activity [emap]
  (let [keys-converted (keys emap)
        get-root (partial util/get-attr :task/id)
        root-task-id (get-root (:root keys-converted))
        get-owner (partial util/get-attr :user/id)
        val-fs {:root get-root
                :owner get-owner
                :tasks (fn [_]
                         (-> (util/connected-tasks root-task-id (util/get-db))
                             (vec->array java.util.UUID "uuid")))}]
    (transform-map-plus keys-converted val-fs)))
