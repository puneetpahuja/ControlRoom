(ns dtm.auth
  (:require [dtm.convert :as convert]
            [dtm.util :as util]))

(defn user-exists? [username]
  (not (nil? (util/get-eid :user/username username (util/get-db)))))


;;; ================================login=======================================


(defn get-password [username]
  (when (user-exists? username)
    (:user/password (util/get-details :user/username username (util/get-db)))))

(defn get-user-auth [username]
  (when (user-exists? username)
    (convert/user-auth (util/get-details :user/username username (util/get-db)))))


;;; ================================logout======================================


(defn get-api-key [username]
  (when (user-exists? username)
    (:user/api-key (util/get-details :user/username username (util/get-db)))))
