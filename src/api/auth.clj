(ns api.auth
  (:require [dtm.auth :as db]
            [ring.util.http-response :as response]))

(defn authorize [auth]
  (let [{check-username :username check-api-key :apiKey} auth]
    (if-not (db/user-exists? check-username)
      {:result false
       :error (response/unauthorized {:error "user does not exist"})}
      (if-not (= check-api-key (db/get-api-key check-username))
        {:result false
         :error (response/unauthorized {:error "wrong api key"})}
        {:result true}))))


;;; ================================login=======================================


(defn login [credentials]
  (let [{check-username :username check-password :password} credentials]
    (if-not (db/user-exists? check-username)
      (response/unauthorized {:error "user does not exist"})
      (if-not (= check-password (db/get-password check-username))
        (response/unauthorized {:error "wrong password"})
        (response/ok (db/get-user-auth check-username))))))


;;; ================================logout======================================


(defn logout [auth]
  (let [{:keys [result error]} (authorize auth)]
    (if-not result
      error
      (response/ok {:result true}))))
