(ns debug.trace
  (:require [clojure.tools.trace :as t]))

(defn trace [& ns-list]
  (map #(t/trace-ns %) ns-list))

(trace 'api.auth
       'api.handler
       'api.read
       'api.schema)

(trace 'data.generator
       'data.repl-commands
       'data.test)

(trace 'dtm.auth
       'dtm.config
       'dtm.convert
       'dtm.read
       'dtm.schema
       'dtm.util)
