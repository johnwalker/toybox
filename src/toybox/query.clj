(ns toybox.query
  (:require [yesql.core :refer [defquery]])
  (:import org.postgresql.ds.PGPoolingDataSource))

(def config (clojure.edn/read-string (slurp "config.edn")))

(def db-spec
  {:datasource
   (doto (new PGPoolingDataSource)
     (.setServerName     "localhost")
     (.setDatabaseName   "toybox")
     (.setUser           (:username config))
     (.setPassword       (:password config))
     (.setMaxConnections 10))})

(defquery get-username "sql/getusername.sql")


