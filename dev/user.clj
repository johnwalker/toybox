(ns user
  (:require
   [org.httpkit.server :refer [run-server]]
   [clojure.java.io :as io]
   [clojure.java.javadoc :refer [javadoc]]
   [clojure.pprint :refer [pprint]]
   [clojure.repl :refer [apropos dir doc find-doc pst source]]
   [clojure.test :as test]
   [clojure.tools.namespace.repl :refer [refresh refresh-all]]
   [toybox.handler :refer [app]]))

(defonce server (atom nil))

(defn init
  []
  nil)

(defn start
  []
  (reset! server (run-server #'app {:port 8080})))

(defn stop
  []
  (@server :timeout 100)
  (reset! server nil))

(defn go
  []
  (init)
  (start)
  :ready)

(defn reset
  []
  (stop)
  (refresh :after 'user/go))

(defn t []
  (test/run-tests 'toybox.handler-test))
