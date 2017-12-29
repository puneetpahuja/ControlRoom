(ns dtm.auth
  (:require [dtm.convert :as convert]
            [dtm.util :as util]))

;;; ================================login=======================================


(defn get-user-auth [username]
  (convert/user-auth (util/get-details :user/username username (util/get-db))))

(defn get-password [username]
 (:user/password (util/get-details :user/username username (util/get-db))))