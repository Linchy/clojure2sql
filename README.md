# clojure2sql

Write modular queries in Clojure, then transpile them to monolithic SQL.

## Usage

```Clojure
(deftest select-test
  (let [sql (query->
              (from :users
                (select :name)))
        expected
"SELECT name
FROM users"]
    (is (= sql expected))))

(deftest where-test
  (let [sql (query->
                (from :users
                    (select :name)
                    (where (= :name "Smith"))))
        expected
"SELECT name
FROM users
WHERE name = 'Smith'"]
    (is (= sql expected))))


(deftest update-test
  (let [sql (query->
              (update :users
                (set (= :firstname "John") (= :surname "Smith"))
                (where (= :id 5))))
        expected
"UPDATE users
SET firstname = 'John', surname = 'Smith'
WHERE id = 5"]
    (is (= sql expected))))

(deftest delete-test
    (let [sql (query->
                (delete-from :users)
                (where (= :id 5)))
        expected
"DELETE FROM users
WHERE id = 5"]
    (is (= sql expected))))


(deftest join-test
    (let [sql (query->
                (from :users
                 (select :name)
                 (where (= :name "Smith"))
                 (middle-join :roles (on= :userId :userId)
                  (select :role))))
            expected
"SELECT name, role
FROM users
INNER JOIN roles on users.userId = roles.userId
WHERE name = 'Smith'"]
        (is (= sql expected))))
```

## License

Copyright © 2017 Alex Lynch

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
