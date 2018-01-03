(ns api.read
  (:require [dtm.read :as db]
            [api.auth :as auth]
            [ring.util.http-response :as response]))


;;; ================================org-units===================================


(defn org-units [{:keys [auth ids]}]
  (auth/authorize-and-respond auth db/org-units ids))


;;; ================================tasks/pending===============================


(defn tasks-pending [{:keys [auth ids]}]
  (auth/authorize-and-respond auth db/tasks-pending ids))


;;; ================================tasks/completed=============================


(defn tasks-completed [{:keys [auth ids]}]
  (auth/authorize-and-respond auth db/tasks-completed ids))
