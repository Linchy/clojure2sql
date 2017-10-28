(ns clojure2sql.core-test
  (:require [clojure.test :refer [is]]
            [clojure2sql.core :refer :all]))

(deftest select-test
  (let [sql (query->
              (from :users
                (select :name)))
        expected "SELECT name\nFROM users"]
    (println "print:\n" sql)
    (is (= sql expected))))

(deftest where-test
  (let [sql (query->
                (from :users
                    (select :name)
                    (where (= :name "Smith"))))
        expected "SELECT name\nFROM users\nWHERE name = 'Smith'"]
    (println "print:\n" sql)
    (is (= sql expected))))


(deftest update-test
  (let [sql (query->
              (update :users
                (set (= :firstname "John") (= :surname "Smith"))
                (where (= :id 5))))
        expected "UPDATE users\nSET firstname = 'John', surname = 'Smith'\nWHERE id = 5"]
    (println "print:\n" sql)
    (is (= sql expected))))

(deftest delete-test
    (let [sql (query->
                (delete-from :users)
                (where (= :id 5)))
        expected "DELETE FROM users\nWHERE id = 5"]
    (println "print:\n" sql)
    (is (= sql expected))))


(deftest join-test
    (let [sql (query->
                (from :users
                 (select :name)
                 (where (= :name "Smith"))
                 (middle-join :roles (on= :userId :userId)
                  (select :role))))
            expected "DELETE FROM users\nWHERE id = 5"]
        (println "print:\n" sql)
        (is (= sql expected))))