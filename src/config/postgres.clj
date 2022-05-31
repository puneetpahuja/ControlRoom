(ns config.postgres)

(def db-spec {:classname   "org.postgresql.Driver"
              :subprotocol "postgresql"
              :subname     "dummy"
              :user        "dummy"
              :password    "dummy"})
