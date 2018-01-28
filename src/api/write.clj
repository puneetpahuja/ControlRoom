(ns api.write
  (:require [dtm.write :as db]
            [api.auth :as auth]
            [data.init :as data]))


;;; ================================tasks=======================================


(defn tasks [{:keys [auth tasks]}]
  (auth/authorize-and-respond auth db/tasks tasks))


;;; ================================reset=======================================


(defn init [credentials]
  (data/init credentials))


;;; ================================test========================================


(defn init-plus [credentials]
  (data/init-plus credentials))
