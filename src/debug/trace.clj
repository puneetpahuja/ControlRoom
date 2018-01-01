(ns debug.trace
  (:require [clojure.tools.trace :as t]))

(defn trace [& ns-list]
  (map #(t/trace-ns %) ns-list))

(trace 'api.auth
       'api.handler
       'api.read
       'api.schema)

(trace 'dtm.auth
       'dtm.config
       'dtm.convert
       'dtm.util
       'dtm.read)
