(ns data.init
  (:require [data.test :as t]
            [dtm.schema :as s]
            [datomic.api :as d]
            [dtm.config :as config]
            [ring.util.http-response :as response]))

(defn init [{:keys [username password]}]
  (let [uri  config/uri]
    (if (and (= username "admin")
             (= password "fancyLikeThat"))
      (do
        (d/delete-database uri)
        (d/create-database uri)
        (let [ conn (d/connect uri)]
          @(d/transact conn s/schema)
          @(d/transact conn t/ro1)
          @(d/transact conn t/ro1-linking)
          @(d/transact conn t/ro2)
          @(d/transact conn t/ro2-linking)
          @(d/transact conn t/templates)
          (response/ok {:result true})))
      (response/unauthorized {:error "wrong credentials"}))))
