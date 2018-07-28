(ns tmp.test
  (:require  [clojure.test :as t]
             [dtm.util :as util]))

(def t {:name "a"
        :children [{:name "b"}
                   {:name "c"}
                   {:name "d"
                    :children [{:name "e"}
                               {:name "f"
                                :children [{:name "g"}]}]}
                   {:name "h"}]})



(defn make-tree-helper [tree]
  (let [c (:children tree)
        f (first c)
        l (last c)
        r (rest c)]
    (cond
      r
      (do
        (make-tree-helper r)))))

(defn add-random-number [_]
  )

(defn make-tree [tree]
  (let [annotated-tree (add-random-number tree)]
    (make-tree-helper annotated-tree)))

(defn process-tree [tree]
  (print "tree: ")
  (clojure.pprint/pprint tree))

(defn process-node [node]
  (println "node: " node))
