(ns data.util
  (:require [clj-time.core :as t]
            [clj-time.coerce :as c]))

(defn date-time [day month year]
  (t/date-time year day month))

(defn milliseconds
  ([day month year]
   (milliseconds (t/date-time year day month)))

  ([datetime]
   (-> datetime
       c/to-long
       str)))

(defn milliseconds-from
  ([days-offset]
   (milliseconds-from (t/now) days-offset))
  ([date-time days-offset]
   (let [dt (if (pos? days-offset)
              (t/plus date-time (t/days days-offset))
              (t/minus date-time (t/days (- days-offset))))]
     (milliseconds dt))))
