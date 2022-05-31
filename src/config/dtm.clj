(ns config.dtm)

(def db       "dummy")
(def ip       "dummy")
(def port     "dummy")
(def user     "dummy")
(def password "dummy")

(def uri (str "datomic:sql://"
              db
              "?jdbc:postgresql://"
              ip
              ":"
              port
              "/datomic?user="
              user
              "&password="
              password))
