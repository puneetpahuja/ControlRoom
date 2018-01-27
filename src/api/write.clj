(ns api.write
  (:require [dtm.write :as db]
            [api.auth :as auth]))


;;; ================================tasks=======================================


(defn tasks [{:keys [auth tasks]}]
  (auth/authorize-and-respond auth db/tasks tasks))
