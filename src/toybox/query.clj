(ns toybox.query
  (:require [yesql.core :refer [defquery]])
  (:import org.postgresql.ds.PGPoolingDataSource))

(def config (read-string (slurp "config.edn")))

(def db-spec
  {:datasource
   (doto (new PGPoolingDataSource)
     (.setServerName     "localhost")
     (.setDatabaseName   "toybox")
     (.setUser           (:username config))
     (.setPassword       (:password config))
     (.setMaxConnections 10))})

(defquery get-username "sql/getusername.sql")
(defquery create-user! "sql/createuser.sql")
(defquery add-user!    "sql/adduser.sql")
(defquery list-users   "sql/listusers.sql")
(defquery create-item! "sql/createitem.sql")
(defquery find-user-pass "sql/finduserpass.sql")

;; (get-username db-spec "hi")
;; (create-user! db-spec)
;; (add-user! db-spec "stephen" "steveellis")
;; (list-users db-spec)
;; (create-item! db-spec)
;; (find-user-pass db-spec "stephen" "steveellis")
