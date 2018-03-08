(ns api.read
  (:require [dtm.read :as db]
            [api.auth :as auth]))


;;; ================================org-units===================================


(defn org-units [{:keys [auth version]}]
  (auth/authorize-and-respond auth db/org-units version))


;;; ================================tasks/pending===============================


(defn tasks-pending [{:keys [auth ids]}]
  (auth/authorize-and-respond auth db/tasks-pending ids))


;;; ================================tasks/assigned==============================


(defn tasks-assigned-pending [{:keys [auth ids]}]
  (auth/authorize-and-respond auth db/tasks-assigned-pending ids))


;;; ================================tasks/completed=============================


(defn tasks-completed [{:keys [auth ids]}]
  (auth/authorize-and-respond auth db/tasks-completed ids))


;;; ================================templates/projects==========================


(defn templates-projects [{:keys [auth ids]}]
  (auth/authorize-and-respond auth db/templates-projects ids))
