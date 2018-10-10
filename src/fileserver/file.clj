(ns fileserver.file
  (:require [fileserver.s3 :as object-store]
            [clojure.java.io :as io]))

(defn random-alphanum-string
  ([]
   (random-alphanum-string 48))
  ([len]
   (let [chars (->> (concat
                      (range 48 58)
                      (range 65 91)
                      (range 97 123))
                    (map char))]
     (apply str
            (take len (repeatedly #(rand-nth chars)))))))

(defn upload [_username file-obj]
  (let [filename      (:filename file-obj)
        fileparts     (clojure.string/split filename #"\.")
        extension     (if (> (count fileparts) 1)
                        (str "." (last fileparts))
                        "")
        new-filename  (str (random-alphanum-string)
                           extension)
        file          (:tempfile file-obj)]
    (object-store/put-object "hih" new-filename file)
    (io/delete-file file true)
    {:filepath new-filename}))

(defn download [_username filepath]
  (with-open [input-stream (:input-stream (object-store/get-object "hih" filepath))]
    (io/copy input-stream (io/file filepath))
    {:file input-stream}))
