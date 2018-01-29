(ns data.init
  (:require [data.test :as t]
            [dtm.schema :as s]
            [datomic.api :as d]
            [dtm.config :as config]
            [ring.util.http-response :as response]))

(defn init
  ([]
   (let [uri  config/uri]
     (d/delete-database uri)
     (d/create-database uri)
     (let [conn (d/connect uri)]
       @(d/transact conn s/schema)
       @(d/transact conn t/users)
       @(d/transact conn t/ro1)
       @(d/transact conn t/ro1-init-linking)
       @(d/transact conn t/ro2)
       @(d/transact conn t/ro2-init-linking)
       @(d/transact conn t/templates))))

  ([{:keys [username password]}]
   (if (and (= username "admin")
            (= password "fancyLikeThat"))
     (do
       (init)
       (response/ok {:result true}))
     (response/unauthorized {:error "wrong credentials"}))))

(defn init-plus [{:keys [username password]}]
  (if (and (= username "admin")
           (= password "fancyLikeThat"))
    (do
      (init)
      (let [uri config/uri
            conn (d/connect uri)]
        @(d/transact conn t/ro1-linking)
        @(d/transact conn t/ro2-linking))
      (response/ok {:result true}))
    (response/unauthorized {:error "wrong credentials"})))
