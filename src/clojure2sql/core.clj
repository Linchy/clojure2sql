(ns clojure2sql.core
  (:gen-class :main true))

(defn -main [& args]
  "I don't do a whole lot."
  (println "Hello, World!"))

; ---------------------------------------------
; ---------------------------------------------

(defn output-str [obj]
  (cond
    (keyword? obj)
      (name obj)
    (string? obj)
      (str "'" obj "'")
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

; ---------------------------------------------
; may query maps to strings
; ---------------------------------------------

(declare map-clause-to-string)

(defn select-to-string [clause]
  (let [field-strings (map #(output-str %) (:fields clause))
        fields (clojure.string/join ", " field-strings)
        full (str "SELECT " fields)]
    full))

(defn from-to-string [clause]
  (let [full (str "FROM " (output-str (:table clause)))]
    full))

(defn where-to-string [clause]
  (let [full (str "WHERE " (map-clause-to-string (:cond clause)))]
    full))

(defn =-to-string [clause]
  (let [full (str (output-str (:lhs clause)) " = " (output-str (:rhs clause)))]
    full))

(defn update-to-string [clause]
  (let [full (str "UPDATE " (output-str (:table clause)))]
    full))

(defn set-to-string [clause]
  (let [fields (map #(map-clause-to-string %) (:fields clause))
        full (str "SET " (clojure.string/join ", " fields))]
    full))

(defn delete-to-string [clause]
  (let [full (str "DELETE FROM " (output-str (:table clause)))]
    full))

(defn inner-join-to-string [clause]
  (let [full (str "INNER JOIN " (output-str (:table clause)) " " (map-clause-to-string (:on clause)))]
    full))

(defn left-join-to-string [clause]
  (let [full (str "LEFT OUTER JOIN " (output-str (:table clause)) " " (map-clause-to-string (:on clause)))]
    full))

(defn right-join-to-string [clause]
  (let [full (str "RIGHT OUTER JOIN " (output-str (:table clause)) " " (map-clause-to-string (:on clause)))]
    full))

(defn on=-to-string [clause]
  (let [full (str "ON " (output-str (:lhs clause)) " = " (output-str (:rhs clause)))]
    full))

(def clause-string-lookup { :select #(select-to-string %)
                            :from #(from-to-string %)
                            :where #(where-to-string %)
                            := #(=-to-string %)
                            :update #(update-to-string %)
                            :set #(set-to-string %)
                            :delete #(delete-to-string %)
                            :inner-join #(inner-join-to-string %)
                            :left-join #(left-join-to-string %)
                            :right-join #(right-join-to-string %)
                            :on= #(on=-to-string %) })

(defn map-clause-to-string [clause]
  (let [full ((get clause-string-lookup (keyword (:type clause))) clause)]
    full))

(defn map-query-to-string [query]
  (let [strings (map #(map-clause-to-string %)
                     query)
        full (clojure.string/join "\n" strings)]
    full))

; ---------------------------------------------
; ---------------------------------------------

(defn query-> [query]
  (println "-------------------------------------")
  ;clojure.pprint/pprint (list query))
  (let [flattened (flatten query)
        sorted-list (sort #(compare (:order %1) (:order %2)) flattened)
        grouped (group-selects sorted-list)
        output (map-query-to-string grouped)]
    (clojure.pprint/pprint output)
    output))

(defn convert-where-to-having [where]
  { :type "having" :cond (:cond where) :order 4 })

; ---------------------------------------------
; clauses
; ---------------------------------------------

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

; ---------------------------------------------
; functions
; ---------------------------------------------

(defn count [field]
  { :type "count" :field field})

(defn = [lhs rhs]
  { :type "=" :lhs lhs :rhs rhs})

(defn > [lhs rhs]
  { :type ">" :lhs lhs :rhs rhs})

; ---------------------------------------------

(defn on= [lhs rhs]
  { :type "on=" :lhs lhs :rhs rhs})

; ---------------------------------------------

(defn nested [query]
  { :type "nested" :query query})

(defn param [name]
  { :type "param" :name name})