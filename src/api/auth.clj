(ns api.auth
  (:require [dtm.auth :as db]
            [ring.util.http-response :as response]))


;;; ================================login=======================================


(defn login [credentials]
  (let [{check-username :username check-password :password} credentials
        {:keys [user] :as user-auth} (db/get-user-auth check-username)]
    (if-not user
      (response/unauthorized {:error "user does not exist"})
      (if-not (= check-password (db/get-password check-username))
        (response/unauthorized {:error "wrong password"})
        (response/ok user-auth)))))
