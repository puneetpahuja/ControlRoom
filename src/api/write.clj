(ns api.write
  (:require [dtm.write :as db]
            [api.auth :as auth]
            [data.init :as data]
            [fileserver.file :as file]))


;;; ==================================user======================================


(defn user [{:keys [auth user]}]
  (auth/authorize-and-respond auth db/user user))


;;; ================================tasks=======================================


(defn tasks [{:keys [auth tasks]}]
  (auth/authorize-and-respond auth db/tasks tasks))


;;; ================================activities==================================


(defn activities [{:keys [auth activities]}]
  (auth/authorize-and-respond auth db/activities activities))


;;; ===============================activities/dynamic===========================


(defn activities-dynamic [{:keys [auth activities]}]
  (auth/authorize-and-respond auth db/activities-dynamic activities))


;;; ===============================templates/activities=========================


(defn templates-activities [{:keys [auth activity-templates]}]
  (auth/authorize-and-respond auth db/activity-templates activity-templates))


;;; ================================reset=======================================


(defn init [credentials]
  (data/init credentials))


;;; ================================upload======================================


(defn upload [auth file]
  (auth/authorize-and-respond auth file/upload file))
