(ns clojure2sql.core
  (:gen-class :main true))

(defn -main [& args]
  "I don't do a whole lot."
  (println "Hello, World!"))

(defn output-str [obj]
  (cond
    (keyword? obj)
      (name obj)
    (string? obj)
      (str "\"" obj "\"")
    :else
    (str obj)))

(defn query-> [query]
  (clojure.pprint/pprint query)
  (let [sql-object { :header [] :mid [] :footer [] }
        ])
  query)

;

(defn select [& fields]
  { :type "select" :fields fields})

(defn from [table & clauses]
  { :type "from" :table table :clauses clauses})

(defn update [table & clauses]
  { :type "update" :clauses clauses})

(defn delete-from [table & clauses]
  { :type "delete" :clauses clauses})

;

(defn where [condition]
  { :type "where" :cond condition})

(defn group-by [col & clauses]
  { :type "group-by" :col col :clauses clauses})

;

(defn set [& fields]
  { :type "set" :fields fields})

(defn count [field]
  { :type "count" :field field})

(defn = [lhs rhs]
  { :type "=" :lhs lhs :rhs rhs})

(defn > [lhs rhs]
  { :type ">" :lhs lhs :rhs rhs})

; joins

(defn inner-join [table on & clauses]
  { :type "inner-join" :table table :on on :clauses clauses})

(defn left-join [table on & clauses]
  { :type "left-join" :table table :on on :clauses clauses})

(defn right-join [table on & clauses]
  { :type "right-join" :table table :on on :clauses clauses})

;

(defn on= [lhs rhs]
  { :type "on=" :lhs lhs :rhs rhs})

;

(defn nested [query]
  { :type "nested" :query query})
