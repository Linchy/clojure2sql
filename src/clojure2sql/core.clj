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
(defn)
(defn query-> [& forms]
  (clojure.string/join "\n" forms))

(defn select [& fields]
  (str "SELECT " (clojure.string/join (map #(output-str %) fields))))

(defn from [table]
  (str "FROM " (output-str table)))

(defn where [condition]
  (str "WHERE " condition))

(defn update [table]
  (str "UPDATE " (output-str table)))

(defn set [& options]
  (str "SET " (clojure.string/join ", " options)))

(defn delete-from [table]
  (str "DELETE FROM " (output-str table)))

(defn = [lhs rhs]
  (str (output-str lhs) " = " (output-str rhs)))