(ns toybox.query
  (:require [yesql.core :refer [defqueries]])
  (:import org.postgresql.ds.PGPoolingDataSource))

(def config (read-string (slurp "config.edn")))

(defonce db-spec
  {:datasource
   (doto (new PGPoolingDataSource)
     (.setServerName     "localhost")
     (.setDatabaseName   "toybox")
     (.setUser           (:username config))
     (.setPassword       (:password config))
     (.setMaxConnections 10))})

(defqueries "sql/query.sql")

