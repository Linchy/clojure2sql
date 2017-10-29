(ns clojure2sql.core_spec
  (:require [clojure.test :refer [run-tests deftest is]]
            [clojure2sql.core :refer :all]
            [clojure.core :as core]))

(deftest test-from-returns-list
         (let [from-result (from :users
                            (select :firstname :surname))
               expected '({:type "from" :table :users :order 1} {:type "select" :fields (:firstname :surname) :order 0})]
           (print "The Expected: ")
           (clojure.pprint/pprint expected)
           (print "  The Actual: ")
           (clojure.pprint/pprint from-result)
           (is (core/= from-result expected))))

(deftest test-group-selects
  (let [selects [{:type "select", :fields [:name], :order 0}
                 {:type "select", :fields [:role], :order 0}]
        select (group-selects selects)
        expected [{:type "select", :fields [:name :role], :order 0}]]
    (print "The Expected: ")
    (clojure.pprint/pprint expected)
    (print "  The Actual: ")
    (clojure.pprint/pprint select)
    (is (core/= select expected))))

(deftest test-map-select-to-string
  (let [query [{:type "select", :fields [:name], :order 0}]
        str (map-query-to-string query)
        expected "SELECT name"]
    (print "The Expected: ")
    (clojure.pprint/pprint expected)
    (print "  The Actual: ")
    (clojure.pprint/pprint str)
    (is (core/= str expected))))