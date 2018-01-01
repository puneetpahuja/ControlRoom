(ns dtm.read
  (:require [dtm.convert :as convert]
            [dtm.util :as util]))


;;; ================================org-units===================================


(defn org-unit [id]
  (convert/org-unit (util/get-details :org-unit/id id (util/get-db))))

(defn org-units [_ ids]
  (let [org-unit-ids  (util/get-ids :org-unit/id (util/get-db))
        diff          (util/diff ids org-unit-ids)]
    (mapv org-unit diff)))
