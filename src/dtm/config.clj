(ns dtm.config)

(def db       "realtest")
(def ip       "localhost")
(def port     "5432")
(def user     "datomic")
(def password "datomic")

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
