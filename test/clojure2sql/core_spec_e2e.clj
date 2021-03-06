(ns clojure2sql.core-spec-e2e
  (:require [clojure.test :refer [deftest is]]
            [clojure2sql.core :refer :all]
            [clojure.core :as core]))

(deftest select-test
  (let [sql (query->
              (from :users
                (select :firstname :surname)))
        expected
"SELECT firstname, surname
FROM users"]
    (is (core/= sql expected))))

(deftest where-test
  (let [sql (query->
                (from :users
                    (select :name)
                    (where (= :name "Smith"))))
        expected
"SELECT name
FROM users
WHERE name = 'Smith'"]
    (is (core/= sql expected))))


(deftest update-test
  (let [sql (query->
              (update :users
                (set (= :firstname "John") (= :surname "Smith"))
                (where (= :id 5))))
        expected
"UPDATE users
SET firstname = 'John', surname = 'Smith'
WHERE id = 5"]
    (is (core/= sql expected))))

(deftest delete-test
    (let [sql (query->
                (delete-from :users
                 (where (= :id 5))))
        expected
"DELETE FROM users
WHERE id = 5"]
    (is (core/= sql expected))))


(deftest inner-join-test
    (let [sql (query->
                (from :users
                 (select :name)
                 (where (= :name "Smith"))
                 (inner-join :roles (on= :userId :userId)
                  (select :role))))
            expected
"SELECT name, role
FROM users
INNER JOIN roles on users.userId = roles.userId
WHERE name = 'Smith'"]
        (is (core/= sql expected))))

(deftest left-outer-join-test
         (let [sql (query->
                     (from :users
                           (select :name)
                           (where (= :name "Smith"))
                           (left-join :roles (on= :userId :userId)
                                       (select :role))))
               expected
"SELECT name, role
FROM users
LEFT OUTER JOIN roles on users.userId = roles.userId
WHERE name = 'Smith'"]
           (is (core/= sql expected))))

(deftest right-outer-join-test
         (let [sql (query->
                     (from :users
                           (select :name)
                           (where (= :name "Smith"))
                           (right-join :roles (on= :userId :userId)
                                       (select :role))))
               expected
"SELECT name, role
FROM users
RIGHT OUTER JOIN roles on users.userId = roles.userId
WHERE name = 'Smith'"]
           (is (core/= sql expected))))

(deftest group-by-test
         (let [sql (query->
                     (from :users
                       (group-by :country)
                       (select (count :name) :country)))
               expected
"SELECT COUNT(name), country
FROM users
GROUP BY country"]
           (is (core/= sql expected))))

(deftest group-by-having-test
         (let [sql (query->
                     (from :users
                           (group-by :country
                             (where (> (count :name) 5)))
                           (select (count :name) :country)))
               expected
"SELECT COUNT(name), country
FROM users
GROUP BY country
HAVING surname = 'Smith'"]
           (is (core/= sql expected))))

(deftest nested-query-test
         (let [get-russia-population (query->
                       (from :countries
                             (select :population)
                             (where (= :name "Russia"))))
               sql (query->
                     (from :countries
                      (select :name)
                      (where (> :population (nested get-russia-population)))))
               expected
"SELECT name
FROM countries
WHERE population > (
  SELECT population
  FROM countries
  WHERE name = 'Russia')"]
           (is (core/= sql expected))))

(deftest parameterised-query-test
         (let [sql (query->
                     (from :countries
                           (select :name)
                           (where (> :population (param :minPopulation)))))
               expected
 "SELECT name
 FROM countries
 WHERE population > @0"]
           (is (core/= sql expected))))