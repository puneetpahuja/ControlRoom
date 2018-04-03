(ns fileserver.s3
  (:require [amazonica.aws.s3 :as s3]
            [config.digital-ocean-spaces :as config]))

(defn list-buckets []
  (s3/list-buckets config/credentials
                   {:client-config {:path-style-access-enabled false
                                    :chunked-encoding-disabled false
                                    :accelerate-mode-enabled false
                                    :payload-signing-enabled true
                                    :dualstack-enabled true
                                    :force-global-bucket-access-enabled true}}))

(defn create-bucket [name]
  (s3/create-bucket config/credentials name))

(defn delete-bucket [name]
  (s3/delete-bucket config/credentials name))

(defn put-object [bucket key file]
  (s3/put-object config/credentials
                 bucket
                 key
                 (java.io.File. file)))

(defn put-string [bucket key string]
  (s3/put-object config/credentials
                 bucket
                 key
                 string))

(defn get-object-metadata [bucket key]
  (s3/get-object-metadata config/credentials
                          bucket
                          key))

(defn get-object [bucket key]
  (-> (s3/get-object config/credentials
                     bucket
                     key)
      :input-stream
      slurp))

(defn delete-object [bucket key]
  (s3/delete-object config/credentials
                    bucket
                    key))

(defn list-objects
  ([bucket prefix]
   (s3/list-objects-v2 config/credentials bucket prefix))
  ([bucket]
   (list-objects bucket "")))
