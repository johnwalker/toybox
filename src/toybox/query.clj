(ns toybox.query
  (:require [yesql.core :refer [defqueries]])
  (:import org.postgresql.ds.PGPoolingDataSource))

(defonce config (read-string (slurp "config.edn")))

(defonce db-spec
  {:datasource
   (doto (new PGPoolingDataSource)
     (.setServerName     "localhost")
     (.setDatabaseName   (if (:testing config)
                           "toyboxtest"
                           "toybox"))
     (.setUser           (:username config))
     (.setPassword       (:password config))
     (.setMaxConnections 10))})

(defqueries "sql/query.sql")

(def creates [create-useraccount!
              create-urd!
              create-status!
              create-userrole!
              create-item!
              create-order!
              create-orderitem!])

(def drops [drop-orderitem!
            drop-order!
            drop-item!
            drop-status!
            drop-userrole!
            drop-urd!
            drop-useraccount!])

(defn create-tables! [s?]
  (doseq [create! creates]
    (try
      (create! db-spec)
      (catch Exception e
        (when s?
          (println (str "Failed to create table in " create!))
          (throw e))))))

(defn drop-tables! [s?]
  (doseq [drop! drops]
    (try
      (drop! db-spec)
      (catch Exception e
        (when s?
          (println (str "Failed to drop table in " drop!))
          (throw e))))))

(defn reset-tables! []
  (try
    (with-out-str (create-tables! true))
    (catch Exception e
      (drop-tables! false)
      (let [s (with-out-str (create-tables! false))]
        (when (seq s) s)))))
