(ns data.demo.test
  (:require [data.demo.ids :as ids]
            [data.generator :as g]
            [data.util :as util]
            [dtm.util :as dtm-util]))

(defn get-supervisor [id username fname lname]
  [id fname lname
   "Supervisor" username username
   (str fname "." lname "@hihseed.org") :user.role/supervisor [:user.channel/app]
   "cr-pwd" "cr-key"])

(defn get-worker [id username fname lname]
  [id fname lname
   "Field Executive" username username
   (str fname "." lname "@hihseed.org") :user.role/worker [:user.channel/app]
   "cr-pwd" "cr-key"])

(def users
  (let [workers    (mapv (comp g/user (partial apply get-worker))
                         [[ids/tn-edu-w-id        "8888888808" "Ankit" "Sharma"]
                          [ids/tn-health-w-id     "8888888809" "Ankit" "Gupta"]
                          [ids/tn-admin-w-id      "8888888810" "Pulkit" "Kumar"]
                          [ids/tn-ops-w-id        "8888888811" "Gaurav" "Kumar"]

                          [ids/mp-edu-w-id        "9898989898" "Jai" "Prakash"]
                          [ids/mp-liv-w-id        "8888888802" "Suresh" "Kumar"]
                          [ids/mp-admin-w-id      "8888888803" "Rahul" "Singh"]
                          [ids/mp-ops-w-id        "8888888804" "Sham" "Singh"]

                          [ids/bhr-edu-w-id       "8888888812" "Himanshu" "Singh"]
                          [ids/bhr-health-w-id    "8888888813" "Himanshu" "Pandey"]
                          [ids/bhr-admin-w-id     "8888888814" "Prakash" "Kumar"]
                          [ids/bhr-ops-w-id       "8888888815" "Hemant" "Tripathi"]])

        supervisors (mapv (comp g/user (partial apply get-supervisor))
                          [[ids/tn-edu-s-id        "9999999908" "Toms" "J"]
                           [ids/tn-health-s-id     "9999999909" "Arth" "Patel"]
                           [ids/tn-admin-s-id      "9999999910" "Stanley" "Samuel"]
                           [ids/tn-ops-s-id        "9999999911" "Ravindra" "Kumar"]

                           [ids/mp-edu-s-id        "9999999999" "Ramesh" "Kumar"]
                           [ids/mp-liv-s-id        "9999999902" "Vaibhav" "Trivedi"]
                           [ids/mp-admin-s-id      "9999999903" "Rahul" "Gupta"]
                           [ids/mp-ops-s-id        "9999999904" "Rishabh" "Singla"]

                           [ids/bhr-edu-s-id       "9999999912" "Arvind" "Chawla"]
                           [ids/bhr-health-s-id    "9999999913" "Deepak" "Shukla"]
                           [ids/bhr-admin-s-id     "9999999914" "Raghavan" "K"]
                           [ids/bhr-ops-s-id       "9999999915" "Pawan" "Kumar"]])]
    (into workers supervisors)))

(def verticals
  (let [tn-admin  (g/vertical [ids/tn-admin-id "Admin"
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

        mp-admin  (g/vertical [ids/mp-admin-id "Admin"
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

        bhr-admin  (g/vertical [ids/bhr-admin-id "Admin"
                                [[:user/id ids/bhr-admin-s-id]
                                 [:user/id ids/bhr-admin-w-id]]])
        bhr-ops    (g/vertical [ids/bhr-ops-id "Operations"
                                [[:user/id ids/bhr-ops-s-id]
                                 [:user/id ids/bhr-ops-w-id]]])
        bhr-edu    (g/vertical [ids/bhr-edu-id "Education"
                                [[:user/id ids/bhr-edu-s-id]
                                 [:user/id ids/bhr-edu-w-id]]])
        bhr-health (g/vertical [ids/bhr-health-id "Health"
                                [[:user/id ids/bhr-health-s-id]
                                 [:user/id ids/bhr-health-w-id]]])]
    [tn-admin tn-ops tn-edu tn-health
     mp-admin mp-ops mp-edu mp-liv
     bhr-admin bhr-ops bhr-edu bhr-health]))

(def states
  (let [tn  (g/state [ids/tn-id "Tamil Nadu"
                      [[:vertical/id ids/tn-admin-id]
                       [:vertical/id ids/tn-ops-id]
                       [:vertical/id ids/tn-edu-id]
                       [:vertical/id ids/tn-health-id]]])

        mp  (g/state [ids/mp-id "Madhya Pradesh"
                      [[:vertical/id ids/mp-admin-id]
                       [:vertical/id ids/mp-ops-id]
                       [:vertical/id ids/mp-edu-id]
                       [:vertical/id ids/mp-liv-id]]])

        bhr (g/state [ids/bhr-id "Bihar"
                      [[:vertical/id ids/bhr-admin-id]
                       [:vertical/id ids/bhr-ops-id]
                       [:vertical/id ids/bhr-edu-id]
                       [:vertical/id ids/bhr-health-id]]])]
    [tn mp bhr]))

(def projects
  (let [tn-water (g/project [ids/tn-water-id "Tamil Nadu Water"
                             [[:state/id ids/tn-id]]
                             nil])
        mp-env   (g/project [ids/mp-env-id "Madhya Pradesh Environment"
                             [[:state/id ids/mp-id]]
                             nil])

        bhr-sch  (g/project [ids/bhr-sch-id "Bihar Schools"
                             [[:state/id ids/bhr-id]]
                             nil])]
    [tn-water mp-env bhr-sch]))

(def org-units [(g/org-units [1])])

(def clients
  (let [hih (g/client [ids/hih-id "Hand in Hand"
                       [[:project/id ids/tn-water-id]
                        [:project/id ids/mp-env-id]
                        [:project/id ids/bhr-sch-id]]])]
    [hih]))

(def task-tags
  [(g/task-tags [1 ["Admin" "Air" "Education" "Health" "HR" "Finance"
                    "Pond Deepening" "RO Plant" "Schools" "Trees" "Water"]])])

(def activity-templates
  (let [at1 (g/activity-template [ids/at1-id "RO plant installation" "Install a new RO plant"
                                  nil])
        at2 (g/activity-template [ids/at2-id "Self help group" "Make a new self help group"
                                  nil])
        at3 (g/activity-template [ids/at3-id "RO plant water level check" "Checks the water level of a plant"
                                  nil])]
    [at1 at2 at3]))


(def ro-activity
  (let [m-a-site  (g/assignment-m [ids/m-a-site-id "m-a-site" [:user/id ids/tn-ops-s-id]])
        mt-a-site (g/m-template   [ids/mt-a-site-id "Assign Identify Site" nil
                                   nil true :measurement.value-type/assignment
                                   nil m-a-site 1])
        m-a-wo    (g/assignment-m [ids/m-a-wo-id "m-a-wo" [:user/id ids/tn-admin-s-id]])
        mt-a-wo   (g/m-template   [ids/mt-a-wo-id "Assign Work Order" nil
                                   nil true :measurement.value-type/assignment
                                   nil m-a-wo 2])
        m-a-df    (g/assignment-m [ids/m-a-df-id "m-a-df" [:user/id ids/tn-ops-w-id]])
        mt-a-df   (g/m-template   [ids/mt-a-df-id "Assign Dig Foundation" nil
                                   nil true :measurement.value-type/assignment
                                   nil m-a-df 3])
        m-a-ll    (g/assignment-m [ids/m-a-ll-id "m-a-ll" [:user/id ids/tn-ops-w-id]])
        mt-a-ll   (g/m-template   [ids/mt-a-ll-id "Assign Lintel Level Reached" nil
                                   nil true :measurement.value-type/assignment
                                   nil m-a-ll 4])
        m-a-h    (g/assignment-m [ids/m-a-h-id "m-a-h" [:user/id ids/tn-ops-s-id]])
        mt-a-h   (g/m-template   [ids/mt-a-h-id "Assign Handover" nil
                                  nil true :measurement.value-type/assignment
                                  nil m-a-h 5])


        m-location  (g/location-m   [ids/m-location-id "m-location" "11.6643,78.1460"])
        mt-location (g/m-template   [ids/mt-location-id "Geolocation" nil
                                     nil true :measurement.value-type/location
                                     nil m-location 1])
        m-address   (g/string-m     [ids/m-address-id "m-address" "Salem"])
        mt-address  (g/m-template   [ids/mt-address-id "Address" nil
                                     nil true :measurement.value-type/string
                                     nil m-address 2])
        m-name      (g/string-m     [ids/m-name-id "m-name" "Mr. Bagirathi T"])
        mt-name     (g/m-template   [ids/mt-name-id "Contact person name" nil
                                     nil true :measurement.value-type/string
                                     nil m-name 3])
        m-number    (g/string-m     [ids/m-number-id "m-number" "9856417885"])
        mt-number   (g/m-template   [ids/mt-number-id "Contact person number" nil
                                     nil true :measurement.value-type/string
                                     nil m-number 4])


        m-wo-number  (g/string-m     [ids/m-wo-number-id "m-wo-number" "5541225WMG"])
        mt-wo-number (g/m-template   [ids/mt-wo-number-id "Work order number" nil
                                      nil true :measurement.value-type/string
                                      nil m-wo-number 1])
        m-amount     (g/float-m     [ids/m-amount-id "m-amount" 890000.0])
        mt-amount    (g/m-template   [ids/mt-amount-id "Amount" nil
                                      nil true :measurement.value-type/float
                                      nil m-amount 2])


        m-photo      (g/photo-m      [ids/m-photo-id "m-photo" "a.com/8.jpg"])
        mt-photo     (g/m-template   [ids/mt-photo-id "Photo" nil
                                      nil true :measurement.value-type/photo
                                      nil m-photo 1])


        m-photo2     (g/photo-m      [ids/m-photo2-id "m-photo2" "a.com/9.jpg"])
        mt-photo2    (g/m-template   [ids/mt-photo2-id "mt-photo2" nil
                                      nil true :measurement.value-type/photo
                                      nil m-photo2 1])


        m-name2     (g/string-m     [ids/m-name2-id "m-name2" nil])
        mt-name2    (g/m-template   [ids/mt-name2-id "Contact person name" nil
                                     nil true :measurement.value-type/string
                                     nil m-name2 1])
        m-number2   (g/string-m     [ids/m-number2-id "m-number2" nil])
        mt-number2  (g/m-template   [ids/mt-number2-id "Contact person number" nil
                                     nil true :measurement.value-type/string
                                     nil m-number2 2])
        m-photo3     (g/photo-m      [ids/m-photo3-id "m-photo3" nil])
        mt-photo3    (g/m-template   [ids/mt-photo3-id "mt-photo3" nil
                                      nil true :measurement.value-type/photo
                                      nil m-photo3 3])

        t-h        (g/task [ids/t-h-id "Handover" "Handover the RO plant"
                            [mt-name2 mt-number2 mt-photo3]
                            :task.type/measurement :task.status/pending
                            nil (util/milliseconds 14 6 2018) nil
                            nil nil nil
                            (util/milliseconds 9 6 2018) (util/milliseconds 9 6 2018)
                            ["Water" "RO Plant" "Admin"]])

        t-ll      (g/task [ids/t-ll-id "Lintel Level reached" "Check the lintel level of the RO plant"
                           [mt-photo2]
                           :task.type/measurement :task.status/completed
                           nil (util/milliseconds 7 6 2018) (util/milliseconds 9 6 2018)
                           nil nil t-h
                           (util/milliseconds 30 5 2018) (util/milliseconds 9 6 2018)
                           ["Water" "RO Plant" "Admin"]])

        t-df      (g/task [ids/t-df-id "Dig Foundation" nil
                           [mt-photo]
                           :task.type/measurement :task.status/completed
                           nil (util/milliseconds 30 5 2018) (util/milliseconds 30 5 2018)
                           nil nil t-ll
                           (util/milliseconds 26 5 2018) (util/milliseconds 30 5 2018)
                           ["Water" "RO Plant"]])

        t-wo      (g/task [ids/t-wo-id "Work Order" nil
                           [mt-wo-number mt-amount]
                           :task.type/measurement :task.status/completed
                           nil (util/milliseconds 24 5 2018) (util/milliseconds 26 5 2018)
                           nil nil t-df
                           (util/milliseconds 19 5 2018) (util/milliseconds 26 5 2018)
                           ["Water" "RO Plant" "Finance"]])

        t-is      (g/task [ids/t-is-id "Identify Site" "Identify the site for the RO plant"
                           [mt-location mt-address mt-name mt-number]
                           :task.type/measurement :task.status/completed
                           nil (util/milliseconds 21 5 2018) (util/milliseconds 19 5 2018)
                           nil nil t-wo
                           (util/milliseconds 17 5 2018) (util/milliseconds 19 5 2018)
                           ["Water" "RO Plant"]])

        t-ro      (g/task [ids/t-ro-id "Assign Salem RO plant installation" nil
                           [mt-a-site mt-a-wo mt-a-df mt-a-ll mt-a-h]
                           :task.type/assignment :task.status/assigned
                           nil (util/milliseconds 16 5 2018) (util/milliseconds 17 5 2018)
                           nil t-is nil
                           (util/milliseconds 14 5 2018) (util/milliseconds 17 5 2018)
                           ["Water" "RO Plant" "Admin"]])

        m-a-ro   (g/assignment-m [ids/m-a-ro-id "m-a-ro" [:user/id ids/tn-admin-s-id]])

        a-ro     (g/activity [ids/a-ro-id "Salem RO plant installation" "Install RO plant in Salem"
                              t-ro nil [:user/id ids/tn-admin-s-id]
                              (util/milliseconds 14 5 2018) (util/milliseconds 9 6 2018) (util/milliseconds 14 6 2018)])]
    [m-a-ro a-ro]))

(def ro-linking
  [{:task/id          ids/t-h-id
    :task/parent      [:task/id ids/t-ro-id]
    :task/assigned-to [:assignment-measurement/id ids/m-a-h-id]}

   {:task/id          ids/t-ll-id
    :task/assigned-to [:assignment-measurement/id ids/m-a-ll-id]}

   {:task/id          ids/t-df-id
    :task/assigned-to [:assignment-measurement/id ids/m-a-df-id]}

   {:task/id          ids/t-wo-id
    :task/assigned-to [:assignment-measurement/id ids/m-a-wo-id]}

   {:task/id          ids/t-is-id
    :task/assigned-to [:assignment-measurement/id ids/m-a-site-id]}

   {:task/id          ids/t-ro-id
    :task/assigned-to [:assignment-measurement/id ids/m-a-ro-id]}

   {:project/id ids/tn-water-id
    :project/activities [[:activity/id ids/a-ro-id]]}])

(def tree-activity
  (let [m-a-sidewalk    (g/assignment-m [ids/m-a-sidewalk-id "m-a-sidewalk" [:user/id ids/mp-ops-w-id]])
        mt-a-sidewalk   (g/m-template   [ids/mt-a-sidewalk-id "Assign Clear Sidewalk" nil
                                         nil true :measurement.value-type/assignment
                                         nil m-a-sidewalk 1])

        m-a-sapling     (g/assignment-m [ids/m-a-sapling-id "m-a-sapling" [:user/id ids/mp-admin-s-id]])
        mt-a-sapling    (g/m-template   [ids/mt-a-sapling-id "Assign Procure Saplings" nil
                                         nil true :measurement.value-type/assignment
                                         nil m-a-sapling 2])

        m-a-digging     (g/assignment-m [ids/m-a-digging-id "m-a-sapling" [:user/id ids/mp-ops-w-id]])
        mt-a-digging    (g/m-template   [ids/mt-a-digging-id "Assign Start Digging Works" nil
                                         nil true :measurement.value-type/assignment
                                         nil m-a-digging 3])

        m-a-completion  (g/assignment-m [ids/m-a-completion-id "m-a-completion" nil])
        mt-a-completion (g/m-template   [ids/mt-a-completion-id "Assign Completion" nil
                                         nil true :measurement.value-type/assignment
                                         nil m-a-completion 4])


        m-s-address     (g/string-m     [ids/m-s-address-id "m-s-address" "MG Road"])
        mt-s-address    (g/m-template   [ids/mt-s-address-id "Address" nil
                                         nil true :measurement.value-type/string
                                         nil m-s-address 1])

        m-s-photo       (g/photo-m      [ids/m-s-photo-id "m-s-photo" "a.com/52.jpg"])
        mt-s-photo      (g/m-template   [ids/mt-s-photo-id "Photo" nil
                                         nil true :measurement.value-type/photo
                                         nil m-s-photo 2])


        m-sp-sname       (g/string-m    [ids/m-sp-sname-id "m-sp-sname" "Mr. Robin Chugh"])
        mt-sp-sname      (g/m-template  [ids/mt-sp-sname-id "Supplier Name" nil
                                         nil true :measurement.value-type/string
                                         nil m-sp-sname 1])

        m-sp-snum        (g/string-m    [ids/m-sp-snum-id "m-sp-snum" "8955425669"])
        mt-sp-snum       (g/m-template  [ids/mt-sp-snum-id "Supplier Number" nil
                                         nil true :measurement.value-type/string
                                         nil m-sp-snum 2])

        m-sp-n           (g/integer-m   [ids/m-sp-n-id "m-sp-n" 500])
        mt-sp-n          (g/m-template  [ids/mt-sp-n-id "Number of saplings" nil
                                         nil true :measurement.value-type/integer
                                         nil m-sp-n 3])

        m-sp-cost        (g/float-m     [ids/m-sp-cost-id "m-sp-cost" 50000.0])
        mt-sp-cost       (g/m-template  [ids/mt-sp-cost-id "Cost" nil
                                         nil true :measurement.value-type/float
                                         nil m-sp-cost 4])


        m-digging-photo  (g/photo-m     [ids/m-digging-photo-id "m-digging-photo" nil])
        mt-digging-photo (g/m-template  [ids/mt-digging-photo-id "Photo" nil
                                         nil true :measurement.value-type/photo
                                         nil m-digging-photo 1])


        m-com-photo      (g/photo-m     [ids/m-com-photo-id "m-com-photo" nil])
        mt-com-photo     (g/m-template  [ids/mt-com-photo-id "Photo" nil
                                         nil true :measurement.value-type/photo
                                         nil m-com-photo 1])

        t-com            (g/task [ids/t-com-id "Completion" nil
                                  [mt-com-photo]
                                  :task.type/measurement :task.status/pre-pending
                                  nil (util/milliseconds 18 6 2018) nil
                                  nil nil nil
                                  nil nil
                                  ["Trees" "Admin" "Air" "Health"]])

        t-digging        (g/task [ids/t-digging-id "Start Digging Works" nil
                                  [mt-digging-photo]
                                  :task.type/measurement :task.status/pending
                                  nil (util/milliseconds 11 6 2018) nil
                                  nil nil t-com
                                  (util/milliseconds 25 5 2018) (util/milliseconds 25 5 2018)
                                  ["Trees" "Air"]])

        t-sapling        (g/task [ids/t-sapling-id "Procure Saplings" nil
                                  [mt-sp-sname mt-sp-snum mt-sp-n mt-sp-cost]
                                  :task.type/measurement :task.status/completed
                                  nil (util/milliseconds 20 5 2018) (util/milliseconds 25 5 2018)
                                  nil nil t-digging
                                  (util/milliseconds 5 5 2018) (util/milliseconds 25 5 2018)
                                  ["Trees" "Air" "Finance"]])

        t-sidewalk        (g/task [ids/t-sidewalk-id "Clear Sidewalk" nil
                                   [mt-s-address mt-s-photo]
                                   :task.type/measurement :task.status/completed
                                   nil (util/milliseconds 10 5 2018) (util/milliseconds 5 5 2018)
                                   nil nil t-sapling
                                   (util/milliseconds 20 4 2018) (util/milliseconds 5 5 2018)
                                   ["Trees" "Air" "Finance"]])

        t-tree            (g/task [ids/t-tree-id "Assign Jabalpur Avenue Tree Planting" nil
                                   [mt-a-sidewalk mt-a-sapling mt-a-digging mt-a-completion]
                                   :task.type/assignment :task.status/assigned
                                   nil (util/milliseconds 22 4 2018) (util/milliseconds 20 4 2018)
                                   nil t-sidewalk nil
                                   (util/milliseconds 18 4 2018) (util/milliseconds 20 4 2018)
                                   ["Trees" "Air"]])

        m-a-tree          (g/assignment-m [ids/m-a-tree-id "m-a-tree" [:user/id ids/mp-admin-s-id]])

        a-tree            (g/activity [ids/a-tree-id "Jabalpur Avenue Tree Planting" nil
                                       t-tree nil [:user/id ids/mp-admin-s-id]
                                       (util/milliseconds 18 4 2018) (util/milliseconds 25 5 2018) (util/milliseconds 18 6 2018)])]
    [m-a-tree a-tree]))

(def tree-linking
  [{:task/id          ids/t-com-id
    :task/parent      [:task/id ids/t-tree-id]
    :task/assigned-to [:assignment-measurement/id ids/m-a-completion-id]}

   {:task/id          ids/t-digging-id
    :task/assigned-to [:assignment-measurement/id ids/m-a-digging-id]}

   {:task/id          ids/t-sapling-id
    :task/assigned-to [:assignment-measurement/id ids/m-a-sapling-id]}

   {:task/id          ids/t-sidewalk-id
    :task/assigned-to [:assignment-measurement/id ids/m-a-sidewalk-id]}


   {:task/id          ids/t-tree-id
    :task/assigned-to [:assignment-measurement/id ids/m-a-tree-id]}

   {:project/id ids/mp-env-id
    :project/activities [[:activity/id ids/a-tree-id]]}])

(def school-activity
  (let [m-a-identify  (g/assignment-m [ids/m-a-identify-id "m-a-identify" [:user/id ids/bhr-admin-w-id]])
        mt-a-identify (g/m-template   [ids/mt-a-identify-id "Assign Identify School" nil
                                       nil true :measurement.value-type/assignment
                                       nil m-a-identify 1])
        m-a-cost    (g/assignment-m [ids/m-a-cost-id "m-a-cost" [:user/id ids/bhr-edu-s-id]])
        mt-a-cost   (g/m-template   [ids/mt-a-cost-id "Assign Estimate Cost" nil
                                     nil true :measurement.value-type/assignment
                                     nil m-a-cost 2])
        m-a-vendor    (g/assignment-m [ids/m-a-vendor-id "m-a-vendor" [:user/id ids/bhr-admin-s-id]])
        mt-a-vendor   (g/m-template   [ids/mt-a-vendor-id "Assign Identify Vendor" nil
                                       nil true :measurement.value-type/assignment
                                       nil m-a-vendor 3])
        m-a-complete    (g/assignment-m [ids/m-a-complete-id "m-a-complete" [:user/id ids/bhr-admin-s-id]])
        mt-a-complete   (g/m-template   [ids/mt-a-complete-id "Assign Completion" nil
                                         nil true :measurement.value-type/assignment
                                         nil m-a-complete 4])

        m-i-location  (g/location-m   [ids/m-i-location-id "m-i-location" "26.1209,85.3647"])
        mt-i-location (g/m-template   [ids/mt-i-location-id "Geolocation" nil
                                       nil true :measurement.value-type/location
                                       nil m-i-location 1])
        m-i-address   (g/string-m     [ids/m-i-address-id "m-i-addess" "Pritam Vihar, Muzzafarpur"])
        mt-i-address  (g/m-template   [ids/mt-i-address-id "Address" nil
                                       nil true :measurement.value-type/string
                                       nil m-i-address 2])
        m-i-name      (g/string-m     [ids/m-i-name-id "m-i-name" "Mr. Salman Ayyub"])
        mt-i-name     (g/m-template   [ids/mt-i-name-id "Contact person name" nil
                                       nil true :measurement.value-type/string
                                       nil m-i-name 3])
        m-i-number    (g/string-m     [ids/m-i-number-id "m-i-number" "9885586945"])
        mt-i-number   (g/m-template   [ids/mt-i-number-id "Contact person number" nil
                                       nil true :measurement.value-type/string
                                       nil m-i-number 4])


        m-e-estimate  (g/float-m     [ids/m-e-estimate-id "m-e-estimate" 500000.0])
        mt-e-estimate (g/m-template   [ids/mt-e-estimate-id "Estimate" nil
                                       nil true :measurement.value-type/float
                                       nil m-e-estimate 1])

        m-v-name      (g/string-m     [ids/m-v-name-id "m-v-name" "Mr. Rajkumar Saini"])
        mt-v-name     (g/m-template   [ids/mt-v-name-id "Vendor name" nil
                                       nil true :measurement.value-type/string
                                       nil m-v-name 1])
        m-v-number    (g/string-m     [ids/m-v-number-id "m-v-number" "9454875526"])
        mt-v-number   (g/m-template   [ids/mt-v-number-id "Vendor number" nil
                                       nil true :measurement.value-type/string
                                       nil m-v-number 2])
        m-v-quote  (g/float-m      [ids/m-v-quote-id "m-v-quote" 550000.0])
        mt-v-quote (g/m-template   [ids/mt-v-quote-id "Vendor quote" nil
                                    nil true :measurement.value-type/float
                                    nil m-v-quote 3])


        m-c-photo    (g/photo-m      [ids/m-c-photo-id "m-c-photo" "a.com/5589.jpg"])
        mt-c-photo   (g/m-template   [ids/mt-c-photo-id "mt-c-photo" nil
                                      nil true :measurement.value-type/photo
                                      nil m-c-photo 1])

        t-complete     (g/task [ids/t-complete-id "Completion" nil
                                [mt-c-photo]
                                :task.type/measurement :task.status/completed
                                nil (util/milliseconds 7 6 2018) (util/milliseconds 5 6 2018)
                                nil nil nil
                                (util/milliseconds 9 5 2018) (util/milliseconds 5 6 2018)
                                ["Education" "Schools" "Admin"]])

        t-vendor      (g/task [ids/t-vendor-id "Identify Vendor" nil
                               [mt-v-name mt-v-number mt-v-quote]
                               :task.type/measurement :task.status/completed
                               nil (util/milliseconds 7 5 2018) (util/milliseconds 9 5 2018)
                               nil nil t-complete
                               (util/milliseconds 26 4 2018) (util/milliseconds 9 5 2018)
                               ["Schools" "Education" "Admin"]])

        t-cost      (g/task [ids/t-cost-id "Estimate Cost" nil
                             [mt-e-estimate]
                             :task.type/measurement :task.status/completed
                             nil (util/milliseconds 28 4 2018) (util/milliseconds 26 4 2018)
                             nil nil t-vendor
                             (util/milliseconds 22 4 2018) (util/milliseconds 26 4 2018)
                             ["Schools" "Education" "Finance"]])

        t-identify      (g/task [ids/t-identify-id "Identify School" nil
                                 [mt-i-location mt-i-address mt-i-name mt-i-number]
                                 :task.type/measurement :task.status/completed
                                 nil (util/milliseconds 21 4 2018) (util/milliseconds 22 4 2018)
                                 nil nil t-cost
                                 (util/milliseconds 19 4 2018) (util/milliseconds 22 4 2018)
                                 ["Schools" "Education" "Admin"]])

        t-school      (g/task [ids/t-school-id "Assign Muzzafarpur School Rebuilding" nil
                               [mt-a-identify mt-a-cost mt-a-vendor mt-a-complete]
                               :task.type/assignment :task.status/completed
                               nil (util/milliseconds 18 4 2018) (util/milliseconds 19 4 2018)
                               nil t-identify nil
                               (util/milliseconds 16 4 2018) (util/milliseconds 19 4 2018)
                               ["Schools" "Education"]])

        m-a-school   (g/assignment-m [ids/m-a-school-id "m-a-school" [:user/id ids/bhr-admin-s-id]])

        a-school     (g/activity [ids/a-school-id "Muzzafarpur School Rebuilding" nil
                                  t-school (util/milliseconds 5 6 2018) [:user/id ids/bhr-admin-s-id]
                                  (util/milliseconds 16 4 2018) (util/milliseconds 5 6 2018) (util/milliseconds 7 6 2018)])]
    [m-a-school a-school]))

(def school-linking
  [{:task/id          ids/t-complete-id
    :task/parent      [:task/id ids/t-school-id]
    :task/assigned-to [:assignment-measurement/id ids/m-a-complete-id]}

   {:task/id          ids/t-vendor-id
    :task/assigned-to [:assignment-measurement/id ids/m-a-vendor-id]}

   {:task/id          ids/t-cost-id
    :task/assigned-to [:assignment-measurement/id ids/m-a-cost-id]}

   {:task/id          ids/t-identify-id
    :task/assigned-to [:assignment-measurement/id ids/m-a-identify-id]}

   {:task/id          ids/t-school-id
    :task/assigned-to [:assignment-measurement/id ids/m-a-school-id]}

   {:project/id ids/bhr-sch-id
    :project/activities [[:activity/id ids/a-school-id]]}])

(def multimedia-assignment-measurement
  (g/assignment-m [(dtm-util/uuid) "m-a" [:user/username "9898989898"]]))

;; debug
(def multimedia-sample-task
  (g/task [ids/demo1 "demo multimedia" ""
           [(g/m-template [(dtm-util/uuid) "" ""
                           nil true :measurement.value-type/playVideo
                           "a.mp4" nil 1])
            (g/m-template [(dtm-util/uuid) "" ""
                           nil true :measurement.value-type/playAudio
                           "b.mp3" nil 2])
            (g/m-template [(dtm-util/uuid) "" ""
                           nil true :measurement.value-type/showImage
                           "c.jpg" nil 3])]
           :task.type/measurement :task.status/pending
           multimedia-assignment-measurement (util/milliseconds-from 5) nil
           nil nil nil
           (util/now) nil nil]))

(def multimedia-sample-activity
  [(g/activity [ids/demo2 "Multimedia" nil
                multimedia-sample-task (util/milliseconds 5 6 2018) [:user/username "9999999999"]
                (util/milliseconds 16 4 2018) (util/milliseconds 5 6 2018) (util/milliseconds 7 6 2018)])])

(def multimedia-sample-activity-linking
  [{:project/id ids/bhr-sch-id
    :project/activities [[:activity/id ids/demo2]]}])



;; m-  (g/   [ids/m--id "" ""])
;; mt- (g/m-template   [ids/mt--id "" nil
;;                      nil true :measurement.value-type/
;;                      nil m- ])


;; t-        (g/task [ids/t--id "" ""
;;                           []
;;                           :task.type/measurement :task.status/
;;                           nil (util/milliseconds) (util/milliseconds)
;;                           nil nil nil
;;                           (util/milliseconds) (util/milliseconds)])
