(ns debug.trace
  (:require [clojure.tools.trace :as t]))

(defn trace [& ns-list]
  (map #(t/trace-ns %) ns-list))

(defn trace-all []
  (trace 'api.auth
         'api.handler
         'api.read
         'api.schema
         'api.write)

  (trace 'dashboard.convert
         'dashboard.postgres)

  (trace 'data.demo.ids
         'data.demo.test
         'data.generator
         'data.ids
         'data.init
         'data.test
         'data.util)

  (trace 'dtm.auth
         'dtm.convert
         'dtm.read
         'dtm.schema
         'dtm.util
         'dtm.write)

  (trace 'fileserver.file
         'fileserver.s3))

(trace-all)
