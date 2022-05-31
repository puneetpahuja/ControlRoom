(ns data.test
  (:require [data.ids :as ids]
            [data.generator :as g]
            [data.util :as util]))

(defn get-supervisor [id username fname lname]
  [id fname lname
   "Supervisor" username username
   "dummy@dummy.com" :user.role/supervisor [:user.channel/app]
   "ramesh-pwd" "ramesh-key"])

(defn get-worker [id username fname lname]
  [id fname lname
   "Field Executive" username username
   "dummy@dummy.com" :user.role/worker [:user.channel/app]
   "jai-pwd" "jai-key"])

(def users
  (let [workers    (mapv (comp g/user (partial apply get-worker))
                         [[ids/mp-edu-w-id        "9898989898" "Jai" "Prakash"]
                          [ids/mp-liv-w-id        "8888888802" "Suresh" "Kumar"]
                          [ids/mp-admin-w-id      "8888888803" "Rahul" "Singh"]
                          [ids/mp-ops-w-id        "8888888804" "Sham" "Singh"]

                          [ids/odisha-health-w-id "8888888805" "Ravi" "Sharma"]
                          [ids/odisha-admin-w-id  "8888888806" "Ravi" "Gupta"]
                          [ids/odisha-ops-w-id    "8888888807" "Alok" "Rathi"]

                          [ids/tn-edu-w-id        "8888888808" "Ankit" "Sharma"]
                          [ids/tn-health-w-id     "8888888809" "Ankit" "Gupta"]
                          [ids/tn-admin-w-id      "8888888810" "Pulkit" "Kumar"]
                          [ids/tn-ops-w-id        "8888888811" "Gaurav" "Kumar"]

                          [ids/mhr-edu-w-id       "8888888812" "Himanshu" "Singh"]
                          [ids/mhr-health-w-id    "8888888813" "Himanshu" "Pandey"]
                          [ids/mhr-admin-w-id     "8888888814" "Prakash" "Kumar"]
                          [ids/mhr-ops-w-id       "8888888815" "Hemant" "Tripathi"]])

        supervisors (mapv (comp g/user (partial apply get-supervisor))
                          [[ids/mp-edu-s-id        "9999999999" "Ramesh" "Kumar"]
                           [ids/mp-liv-s-id        "9999999902" "Vaibhav" "Trivedi"]
                           [ids/mp-admin-s-id      "9999999903" "Rahul" "Gupta"]
                           [ids/mp-ops-s-id        "9999999904" "Rishabh" "Singla"]

                           [ids/odisha-health-s-id "9999999905" "Anurag" "Tomer"]
                           [ids/odisha-admin-s-id  "9999999906" "Abhishek" "Tiwari"]
                           [ids/odisha-ops-s-id    "9999999907" "Purshotam" "Chauhan"]

                           [ids/tn-edu-s-id        "9999999908" "Toms" "J"]
                           [ids/tn-health-s-id     "9999999909" "Arth" "Patel"]
                           [ids/tn-admin-s-id      "9999999910" "Stanley" "Samuel"]
                           [ids/tn-ops-s-id        "9999999911" "Ravindra" "Kumar"]

                           [ids/mhr-edu-s-id       "9999999912" "Arvind" "Chawla"]
                           [ids/mhr-health-s-id    "9999999913" "Deepak" "Shukla"]
                           [ids/mhr-admin-s-id     "9999999914" "Raghavan" "K"]
                           [ids/mhr-ops-s-id       "9999999915" "Pawan" "Kumar"]])]
    (into workers supervisors)))

(def verticals
  (let [mp-admin  (g/vertical [ids/mp-admin-id "Admin"
                               [[:user/id ids/mp-admin-s-id]
                                [:user/id ids/mp-admin-w-id]]])
        mp-ops    (g/vertical [ids/mp-ops-id "Operations"
                               [[:user/id ids/mp-ops-s-id]
                                [:user/id ids/mp-ops-w-id]]])
        mp-edu    (g/vertical [ids/mp-edu-id "Education"
                               [[:user/id ids/mp-edu-s-id]
                                [:user/id ids/mp-edu-w-id]]])
        mp-liv    (g/vertical [ids/mp-liv-id "Livelihoods"
                               [[:user/id ids/mp-liv-s-id]
                                [:user/id ids/mp-liv-w-id]]])

        odisha-admin  (g/vertical [ids/odisha-admin-id "Admin"
                                   [[:user/id ids/odisha-admin-s-id]
                                    [:user/id ids/odisha-admin-w-id]]])
        odisha-ops    (g/vertical [ids/odisha-ops-id "Operations"
                                   [[:user/id ids/odisha-ops-s-id]
                                    [:user/id ids/odisha-ops-w-id]]])
        odisha-health (g/vertical [ids/odisha-health-id "Health"
                                   [[:user/id ids/odisha-health-s-id]
                                    [:user/id ids/odisha-health-w-id]]])

        tn-admin  (g/vertical [ids/tn-admin-id "Admin"
                               [[:user/id ids/tn-admin-s-id]
                                [:user/id ids/tn-admin-w-id]]])
        tn-ops    (g/vertical [ids/tn-ops-id "Operations"
                               [[:user/id ids/tn-ops-s-id]
                                [:user/id ids/tn-ops-w-id]]])
        tn-edu    (g/vertical [ids/tn-edu-id "Education"
                               [[:user/id ids/tn-edu-s-id]
                                [:user/id ids/tn-edu-w-id]]])
        tn-health (g/vertical [ids/tn-health-id "Health"
                               [[:user/id ids/tn-health-s-id]
                                [:user/id ids/tn-health-w-id]]])

        mhr-admin  (g/vertical [ids/mhr-admin-id "Admin"
                                [[:user/id ids/mhr-admin-s-id]
                                 [:user/id ids/mhr-admin-w-id]]])
        mhr-ops    (g/vertical [ids/mhr-ops-id "Operations"
                                [[:user/id ids/mhr-ops-s-id]
                                 [:user/id ids/mhr-ops-w-id]]])
        mhr-edu    (g/vertical [ids/mhr-edu-id "Education"
                                [[:user/id ids/mhr-edu-s-id]
                                 [:user/id ids/mhr-edu-w-id]]])
        mhr-health (g/vertical [ids/mhr-health-id "Health"
                                [[:user/id ids/mhr-health-s-id]
                                 [:user/id ids/mhr-health-w-id]]])]
    [mp-admin mp-ops mp-edu mp-liv
     odisha-admin odisha-ops odisha-health
     tn-admin tn-ops tn-edu tn-health
     mhr-admin mhr-ops mhr-edu mhr-health]))

(def states
  (let [mp     (g/state [ids/mp-id "Madhya Pradesh"
                         [[:vertical/id ids/mp-admin-id]
                          [:vertical/id ids/mp-ops-id]
                          [:vertical/id ids/mp-edu-id]
                          [:vertical/id ids/mp-liv-id]]])
        odisha (g/state [ids/odisha-id "Odisha"
                         [[:vertical/id ids/odisha-admin-id]
                          [:vertical/id ids/odisha-ops-id]
                          [:vertical/id ids/odisha-health-id]]])

        tn  (g/state [ids/tn-id "Tamil Nadu"
                      [[:vertical/id ids/tn-admin-id]
                       [:vertical/id ids/tn-ops-id]
                       [:vertical/id ids/tn-edu-id]
                       [:vertical/id ids/tn-health-id]]])
        mhr (g/state [ids/mhr-id "Maharashtra"
                      [[:vertical/id ids/mhr-admin-id]
                       [:vertical/id ids/mhr-ops-id]
                       [:vertical/id ids/mhr-edu-id]
                       [:vertical/id ids/mhr-health-id]]])]
    [mp odisha tn mhr]))

(def projects
  (let [tvs (g/project [ids/tvs-id "TVS"
                        [[:state/id ids/mp-id]]
                        nil])
        mrg (g/project [ids/mrg-id "Muruguppa Group"
                        [[:state/id ids/tn-id]]
                        nil])]
    [tvs mrg]))

(def org-units [(g/org-units [1])])

(def clients
  (let [hih (g/client [ids/hih-id "Hand in Hand"
                       [[:project/id ids/tvs-id]
                        [:project/id ids/mrg-id]]])]
    [hih]))

(def task-tags
  [(g/task-tags [2 ["SHG"
                    "SDTC"
                    "Health"
                    "Environment"
                    "SWM"
                    "NRM"
                    "CLEP"
                    "VUP"
                    "EGF-MP"]])])

(def activity-templates
  (let [at1 (g/activity-template [ids/at1-id "RO plant installation" "Install a new RO plant"
                                  nil])
        at2 (g/activity-template [ids/at2-id "Self help group" "Make a new self help group"
                                  nil])
        at3 (g/activity-template [ids/at3-id "RO plant water level check" "Checks the water level of a plant"
                                  nil])]
    [at1 at2 at3]))
