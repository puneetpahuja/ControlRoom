(ns data.generator)

(def user-attrs         [:user
                         :id :first-name :last-name
                         :title :username :phone
                         :email :role :channels
                         :password :api-key])

(def org-unit-attrs     [:org-unit
                         :id :name :users])

(def integer-m-attrs    [:integer-measurement
                         :id :name :value])
(def string-m-attrs     [:string-measurement
                         :id :name :value])
(def assignment-m-attrs [:assignment-measurement
                         :id :name :value])
(def photo-m-attrs      [:photo-measurement
                         :id :name :value])

(def datasource-attrs   [:datasource
                         :id :measurements :tags
                         :entity])

(def m-template-attrs   [:measurement-template
                         :id :question :hint
                         :validations :required :value-type
                         :default-value :measurement])

(def task-attrs         [:task
                         :id :name :description
                         :measurement-templates :type :status
                         :assigned-to :assigned-by :completed-at
                         :parent :first-child :sibling
                         :created-at :updated-at :due-date])

(def project-attrs      [:project
                         :id :name :description
                         :root :status :owner
                         :created-at :updated-at :due-date
                         :completed-at])


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
(def integer-m    (partial make-tx integer-m-attrs))
(def string-m     (partial make-tx string-m-attrs))
(def assignment-m (partial make-tx assignment-m-attrs))
(def photo-m      (partial make-tx photo-m-attrs))
(def datasource   (partial make-tx datasource-attrs))
(def m-template   (partial make-tx m-template-attrs))
(def task         (partial make-tx task-attrs))
(def project      (partial make-tx project-attrs))
