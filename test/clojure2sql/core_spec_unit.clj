(ns clojure2sql.core-spec-unit
  (:require [clojure.test :refer [run-tests deftest is]]
            [clojure2sql.core :refer :all]
            [clojure.core :as core]))

(defn print-info [expected actual]
  (print "The Expected: ")
  (clojure.pprint/pprint expected)
  (print "  The Actual: ")
  (clojure.pprint/pprint actual))

(deftest test-from-returns-list
         (let [from-result (from :users
                            (select :firstname :surname))
               expected '({:type "from" :table :users :order 1} {:type "select" :fields (:firstname :surname) :order 0})]
           (print-info expected from-result)
           (is (core/= from-result expected))))

(deftest test-group-selects
  (let [selects [{:type "select", :fields [:name], :order 0}
                 {:type "select", :fields [:role], :order 0}]
        select (group-selects selects)
        expected [{:type "select", :fields [:name :role], :order 0}]]
    (print-info expected select)
    (is (core/= select expected))))

(deftest test-map-select-to-string
  (let [query [{:type "select", :fields [:name], :order 0}]
        str (map-query-to-string query)
        expected "SELECT name"]
    (is (core/= str expected))))

(deftest test-map-from-to-string
  (let [query [{:type "from", :table :users, :order 0}]
        str (map-query-to-string query)
        expected "FROM users"]
    (print-info expected str)
    (is (core/= str expected))))

(deftest test-map-where-to-string
  (let [query [{:type "where", :cond (= :name "Smith"), :order 0}]
        str (map-query-to-string query)
        expected "WHERE name = 'Smith'"]
    (print-info expected str)
    (is (core/= str expected))))

(deftest test-map-update-to-string
  (let [query [{:type "update", :table :users, :order 0}]
        str (map-query-to-string query)
        expected "UPDATE users"]
    (print-info expected str)
    (is (core/= str expected))))

(deftest test-2-sets-to-string
  (let [query [{:type "set", :fields [{ :type "=", :lhs :firstname, :rhs "John"} { :type "=", :lhs :surname, :rhs "Smith"}] :order 0}]
        str (map-query-to-string query)
        expected "SET firstname = 'John', surname = 'Smith'"]
    (print-info expected str)
    (is (core/= str expected))))

(deftest test-delete-to-string
  (let [query [{:type "delete", :table :users :clauses [] :order 0}]
        str (map-query-to-string query)
        expected "DELETE FROM users"]
    (print-info expected str)
    (is (core/= str expected))))

(deftest test-inner-join-to-string
  (let [query [{ :type "inner-join", :table :users, :on { :type "on=", :lhs :userId, :rhs :userId}, :order 2 }]
        str (map-query-to-string query)
        expected "INNER JOIN users ON userId = userId"]
    (print-info expected str)
    (is (core/= str expected))))

(deftest test-left-join-to-string
  (let [query [{ :type "left-join", :table :users, :on { :type "on=", :lhs :userId, :rhs :userId}, :order 2 }]
        str (map-query-to-string query)
        expected "LEFT OUTER JOIN users ON userId = userId"]
    (print-info expected str)
    (is (core/= str expected))))

(deftest test-right-join-to-string
  (let [query [{ :type "right-join", :table :users, :on { :type "on=", :lhs :userId, :rhs :userId}, :order 2 }]
        str (map-query-to-string query)
        expected "RIGHT OUTER JOIN users ON userId = userId"]
    (print-info expected str)
    (is (core/= str expected))))