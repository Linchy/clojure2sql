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

(defn group-selects [clauses]
  (reduce (fn [clauseAccum clause]
            (if (= (:type clause) "select")
              ; assume list is sorted, with all selects at the top
              (let [have-prev-selects (> (count clauseAccum) 0)
                    prev-fields (if have-prev-selects
                                  (:fields (nth clauseAccum 0))
                                  [])
                    new-fields (vec (flatten (conj prev-fields (:fields clause))))]
              [{ :type "select" :fields new-fields :order 0 }])
              ; else just append clause
              (conj clauseAccum clause)))
          (concat [[]] clauses)))

(defn select-to-string [select]
  (let [field-strings (map #(output-str %) (:fields select))
        fields (clojure.string/join ", " field-strings)
        full (str "SELECT " fields)]
    full))

(def clause-string-lookup {
         :select #(select-to-string %)
         })

(defn map-query-to-string [query]
  (let [strings (map #((get clause-string-lookup (keyword (:type %))) %)
                     query)
        full (clojure.string/join "\n" strings)]
    full))

(defn query-> [query]
  (println "-------------------------------------")
  ;clojure.pprint/pprint (list query))
  (let [flattened (flatten query)
        sorted-list (sort #(compare (:order %1) (:order %2)) flattened)
        grouped (group-selects sorted-list)]
    (clojure.pprint/pprint grouped)
    grouped))

(defn convert-where-to-having [where]
  { :type "having" :cond (:cond where) :order 4 })

; clauses

(defn select [& fields]
  { :type "select" :fields fields :order 0 })

(defn update [table & clauses]
  (concat [{ :type "update" :table table :order 0 }]
          clauses))

(defn delete-from [table & clauses]
  (concat [{ :type "delete" :table table :order 0 }]
          clauses))

(defn from [table & clauses]
  (concat [{ :type "from" :table table :order 1 }]
          clauses))

(defn inner-join [table on & clauses]
  (concat [{ :type "inner-join" :table table :on on :order 2 }]
          clauses))

(defn left-join [table on & clauses]
  (concat [{ :type "left-join" :table table :on on :order 2 }]
          clauses))

(defn right-join [table on & clauses]
  (concat [{ :type "right-join" :table table :on on :order 2 }]
          clauses))

(defn where [condition]
  { :type "where" :cond condition :order 3 })

(defn set [& fields]
  { :type "set" :fields fields :order 3 })

(defn group-by [col & clauses]
  (concat [{ :type "group-by" :col col :order 4 }]
          (map #(if (= (:type %) "where")
                  (convert-where-to-having %)
                  %) clauses)))


; functions

(defn count [field]
  { :type "count" :field field})

(defn = [lhs rhs]
  { :type "=" :lhs lhs :rhs rhs})

(defn > [lhs rhs]
  { :type ">" :lhs lhs :rhs rhs})


;

(defn on= [lhs rhs]
  { :type "on=" :lhs lhs :rhs rhs})

;

(defn nested [query]
  { :type "nested" :query query})

(defn param [name]
  { :type "param" :name name})