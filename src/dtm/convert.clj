(ns dtm.convert
  (:refer-clojure :exclude [keys])
  (:require [camel-snake-kebab.core :as convert-case]
            [camel-snake-kebab.extras :as convert-case-helper]
            [clojure.string :as s]
            [dtm.util :as util]))

(defn entity-map [emap]
  (into {} emap))

(defn remove-namespace [namespaced-keyword]
  (-> namespaced-keyword
      name
      (s/split #"/")
      last
      keyword))

(def remove-namespace-str (comp name remove-namespace))

(defn keys [cmap]
  (let [transformer (comp convert-case/->camelCase
                          remove-namespace)]
    (into {}
          (for [[k v] cmap]
            [(transformer k) v]))))

;;; ================================login=======================================


(defn user-auth [emap]
  (when emap
    (let [cmap                         (entity-map emap)
          keys-converted               (keys cmap)
          keys-same                    (select-keys keys-converted
                                                    [:firstName :lastName
                                                     :title :username :phone :email])

          {:keys [id role orgUnit
                  channels apiKey]}    keys-converted

          org-unit-details             (keys (util/get-details orgUnit))
          org-unit-id                  (str (:id org-unit-details))
          org-unit-name                (:name org-unit-details)

          user          (assoc keys-same
                               :id        (str id)
                               :role      (remove-namespace-str role)
                               :channels  (mapv remove-namespace-str channels)
                               :orgUnit   org-unit-name
                               :orgUnitId org-unit-id)

          user-auth                    {:user user
                                        :apiKey apiKey}]
      user-auth)))
