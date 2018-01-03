(ns data.generator)

(defn gen-uuid []
  (java.util.UUID/randomUUID))

(def user-attrs         [:user :id :first-name :last-name
                         :title :username :phone
                         :email :role :org-unit
                         :channels :password :api-key])

(def org-unit-attrs     [:org-unit :id :name :users])

(def task-attrs         [:task :id :name :description
                         :project :measurements :type
                         :status :assigned-to :assigned-by
                         :parent :first-child :sibling
                         :created-at :updated-at :due-date
                         :completed-at])

(def project-attrs      [:project :id :name :description
                         :root :status :client
                         :owner :created-at :updated-at
                         :due-date :completed-at])

(def integer-m-attrs    [:integer-measurement :id :name :value])
(def string-m-attrs     [:string-measurement :id :name :value])
(def assignment-m-attrs [:assignment-measurement :id :name :value
                         :datasource])

(def datasource-attrs   [:datasource :id :name :measurements
                         :tags :entity])


(defn make-tx [attrs vals]
  (let [namespace (first attrs)
        namespaced-attrs (map #(keyword (str (name namespace)
                                             "/"
                                             (name %)))
                              (rest attrs))
        all-attrs-map (zipmap namespaced-attrs vals)
        non-nil-attrs-map (into {} (filter second all-attrs-map))]
    non-nil-attrs-map))

(def user         (partial make-tx user-attrs))
(def org-unit     (partial make-tx org-unit-attrs))
(def task         (partial make-tx task-attrs))
(def project      (partial make-tx project-attrs))
(def integer-m    (partial make-tx integer-m-attrs))
(def string-m     (partial make-tx string-m-attrs))
(def assignment-m (partial make-tx assignment-m-attrs))
(def datasource   (partial make-tx datasource-attrs))
