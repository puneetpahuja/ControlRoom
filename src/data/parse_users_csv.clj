(ns data.parse-users-csv
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [clojure.string :as s]
            [dtm.write :as write]
            [dtm.util :as util]
            [postal.core :as email]))

(defn read-csv [path]
  (with-open [reader (io/reader (io/resource path))]
    (doall
     (csv/read-csv reader))))

(defn trim [s]
  (let [len (.length s)]
    (loop [rindex len]
      (if (zero? rindex)
        ""
        (if (Character/isSpaceChar (.charAt s (dec rindex)))
          (recur (dec rindex))
          ;; there is at least one non-whitespace char in the string,
          ;; so no need to check for lindex reaching len.
          (loop [lindex 0]
            (if (Character/isSpaceChar (.charAt s lindex))
              (recur (inc lindex))
              (.. s (subSequence lindex rindex) toString))))))))

(defn send-email
  ([receiver-email subject body]
   (email/send-message {:host    "smtp.gmail.com"
                        :user    "dummyEmail"
                        :pass    "dummy"
                        :ssl     true}
                       {:from    "dummyEmail"
                        :to      receiver-email
                        :cc      "dummy"
                        :subject subject
                        :body    body}))

  ([name receiver-email username password state]
   (send-email (cond
                 (s/includes? receiver-email "@") receiver-email
                 (= state "Madhya Pradesh") "dummy"
                 (= state "Maharashtra")    "dummy")
               "Dummy Subject"
               (str "Dear "
                    name
                    "\n\nYour credentials are : \n\tUsername : "
                    username
                    "\n\tPassword : "
                    password))))

(defn send-emails [users]
  (map #(apply send-email %) users))

(defn mmapv [f list-of-lists]
  (mapv #(map f %) list-of-lists))

(defn get-cols [details & ns]
  (mapv #(mapv % ns) details))

(defn get-col [details n]
  (vec (flatten (get-cols details n))))

(defn users [path]
  (let [parsed-csv       (read-csv path)
        raw-user-data    (drop 2 parsed-csv)
        wanted-columns   (->> raw-user-data
                              (mapv (partial drop 1))
                              (mapv drop-last))
        spaces-trimmed   (mmapv trim wanted-columns)
        replaced         (mapv (comp vec
                                     (partial replace {"Tamilnadu" "Tamil Nadu"}))
                               spaces-trimmed)]
    replaced))

(defn get-states []
  (vec (distinct (get-col (users "hih_users.csv") 5))))

(defn get-states-departments []
  (vec (distinct (get-cols (users "hih_users.csv") 4 5))))

(defn get-departments [state]
  (as-> (get-states-departments) x
    (group-by second x)
    (get x state)
    (mapv first x)))

(defn vertical-tx [name]
  {:vertical/id   (util/uuid)
   :vertical/name name})

(defn state-tx [name]
  (let [tx {:id        (util/uuid)
            :name      name
            :verticals (mapv vertical-tx (get-departments name))}]
    (write/add-namespace "state" tx)))

(defn states-tx []
  (let [states (get-states)]
    (mapv state-tx states)))

(defn make-vertical-and-put-in-state [vertical state]
  (let [vertical-id (util/uuid)]
    (util/transact [{:vertical/id vertical-id
                     :vertical/name vertical}])
    (util/transact [{:state/id (util/get-state-id state (util/get-db))
                     :state/verticals [[:vertical/id vertical-id]]}])))

(defn put-user-in-vertical [state department phone]
  (util/transact [{:vertical/id    (util/get-vertical-id state department (util/get-db))
                   :vertical/users [[:user/username phone]]}]))

(defn user-tx [user]
  (let [user-details    (zipmap [:name :title :phone :email :department :state]
                                user)
        same-properties (select-keys user-details [:title :phone :email])
        {:keys [name
                phone
                department
                state
                email]}  user-details
        first-last-name  (concat (s/split name #" " 2) [""])
        tx               (->> (assoc same-properties
                                     :id         (util/uuid)
                                     :first-name (first first-last-name)
                                     :last-name  (second first-last-name)
                                     :username   phone
                                     :password   (write/simple-name 3 "-")
                                     :api-key    (write/gen-password))
                              (write/add-namespace "user"))]
    (if (= (count phone) 10)
      (do
        (util/transact [tx])
        (put-user-in-vertical state department phone)
        [name email phone (:user/password tx) state]
        ;; (send-email name email phone (:user/password tx) state)
        ))))

(defn users-tx [users]
  (mapv user-tx users))

(defn project-tx [[project state]]
  (write/add-namespace "project" {:id     (util/uuid)
                                  :name   project
                                  :states [(state-tx state)]}))

(defn projects-tx []
  (mapv project-tx {"VUP-TN"            "Tamil Nadu"
                    "VUP-MH"            "Maharashtra"
                    "EGF-MP"            "Madhya Pradesh"
                    "PROJECT-HO"        "HO"
                    "PROJECT-NEW DELHI" "New Delhi"}))

(defn client-tx []
  (write/add-namespace "client" {:id       (util/uuid)
                                 :name     "Dummy"
                                 :projects (projects-tx)}))
