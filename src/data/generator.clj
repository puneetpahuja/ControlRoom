(ns data.generator
  (:require [dtm.util :as util]))

(def user-attrs         [:user
                         :id :first-name :last-name
                         :title :username :phone
                         :email :role :channels
                         :password :api-key])

(def org-units-attrs    [:org-units
                         :version])

(def client-attrs      [:client
                        :id :name :projects])

(def project-attrs [:project
                    :id :name :states
                    :activities])

(def state-attrs [:state
                  :id :name :verticals])

(def vertical-attrs [:vertical
                     :id :name :users])

(def integer-m-attrs    [:integer-measurement
                         :id :name :value])
(def string-m-attrs     [:string-measurement
                         :id :name :value])
(def assignment-m-attrs [:assignment-measurement
                         :id :name :value])
(def photo-m-attrs      [:photo-measurement
                         :id :name :value])
(def date-m-attrs       [:date-measurement
                         :id :name :value])
(def location-m-attrs   [:location-measurement
                         :id :name :value])
(def float-m-attrs      [:float-measurement
                         :id :name :value])
(def any-m-attrs        [:any-measurement
                         :id :name :value
                         :value-type])

(def datasource-attrs   [:datasource
                         :id :measurements :tags
                         :entity])

(def m-template-attrs   [:measurement-template
                         :id :question :hint
                         :validations :required :value-type
                         :default-value :measurement :position])

(def task-attrs         [:task
                         :id :name :description
                         :measurement-templates :type :status
                         :assigned-to :due-date :completed-at
                         :parent :first-child :sibling
                         :created-at :updated-at])

(def activity-attrs      [:activity
                          :id :name :description
                          :root :completed-at :owner
                          :created-at :updated-at :due-date])

(def project-template-attrs [:project-template
                             :id :title :description
                             :project-schema-id])



(defn make-tx [attrs vals]
  (let [namespace (first attrs)
        namespaced-attrs (map #(keyword (str (name namespace)
                                             "/"
                                             (name %)))
                              (rest attrs))
        all-attrs-map (zipmap namespaced-attrs vals)
        non-nil-attrs-map (util/filter-nil all-attrs-map)]
    non-nil-attrs-map))

(def user         (partial make-tx user-attrs))
(def org-units    (partial make-tx org-units-attrs))
(def client       (partial make-tx client-attrs))
(def project      (partial make-tx project-attrs))
(def state        (partial make-tx state-attrs))
(def vertical     (partial make-tx vertical-attrs))
(def integer-m    (partial make-tx integer-m-attrs))
(def string-m     (partial make-tx string-m-attrs))
(def assignment-m (partial make-tx assignment-m-attrs))
(def photo-m      (partial make-tx photo-m-attrs))
(def date-m       (partial make-tx date-m-attrs))
(def location-m   (partial make-tx location-m-attrs))
(def float-m      (partial make-tx float-m-attrs))
(def datasource   (partial make-tx datasource-attrs))
(def m-template   (partial make-tx m-template-attrs))
(def task         (partial make-tx task-attrs))
(def activity     (partial make-tx activity-attrs))
(def project-template (partial make-tx project-template-attrs))
