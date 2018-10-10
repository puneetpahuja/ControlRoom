(ns api.util
  (:require [config.admin :as config]
            [config.db-admin :as db-config]))

(defn admin?
  ([creds]
   (= creds {:username config/username
             :password config/password}))

  ([username password]
   (and (= username config/username)
        (= password config/password))))

(defn db-admin?
  ([creds]
   (= creds {:username db-config/username
             :password db-config/password}))

  ([username password]
   (and (= username db-config/username)
        (= password db-config/password))))
