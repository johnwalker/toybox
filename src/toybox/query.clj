(ns toybox.query
  (:require [yesql.core :refer [defqueries]]
            [toybox.constant :refer [config]])
  (:import com.mchange.v2.c3p0.ComboPooledDataSource))

(defn make-pool
  [spec]
  (let [cpds (doto (ComboPooledDataSource.)
               (.setDriverClass (:classname spec))
               (.setJdbcUrl (str "jdbc:" (:subprotocol spec) ":" (:subname spec)))
               (.setUser (:user spec))
               (.setPassword (:password spec))
               (.setMaxIdleTimeExcessConnections (* 30 30))
               (.setMaxIdleTime (* 3 60 60)))]
    {:datasource cpds}))

(let [db-host "localhost"
      db-port 3306
      db-name "toyboxtest"]
  (defonce db-spec {:classname "com.mysql.jdbc.Driver"
                    :subprotocol "mysql"
                    :subname (str "//" db-host ":" db-port "/" db-name)
                    :user (:username config)
                    :password (:password config)}))

(def db (delay (make-pool db-spec)))

(defqueries "sql/query.sql")

(def creates [create-useraccount!
              create-item!
              create-order!
              create-orderitem!])

(def drops [drop-orderitem!
            drop-order!
            drop-item!
            drop-useraccount!])

(defn create-tables! [s?]
  (doseq [create! creates]
    (try
      (create! @db)
      (catch Exception e
        (when s?
          (println (str "Failed to create table in " create!))
          (throw e))))))

(defn drop-tables! [s?]
  (doseq [drop! drops]
    (try
      (drop! @db)
      (catch Exception e
        (when s?
          (println (str "Failed to drop table in " drop!))
          (throw e))))))

(defn reset-tables! []
  (let [message (try
                  (with-out-str (create-tables! true))
                  (catch Exception e
                    (drop-tables! false)
                    (let [s (with-out-str (create-tables! false))]
                      (when (seq s) s))))]
    (when-not message
      (doto @db
        (init-useraccount!)
        (init-item!)
        (init-order!)
        (init-orderitem!)))))
