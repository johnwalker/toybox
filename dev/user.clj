(ns user
  (:require
   [org.httpkit.server :refer [run-server]]
   [clojure.java.io :as io]
   [clojure.java.javadoc :refer [javadoc]]
   [clojure.pprint :refer [pprint]]
   [clojure.repl :refer [apropos dir doc find-doc pst source]]
   [clojure.test :as test]
   [clojure.tools.namespace.repl :refer [refresh-all]]
   [toybox.handler :refer [app]]
   [toybox.query :as q]))
(defonce used (atom nil))
(defonce server (atom nil))

(defn init
  []
  nil)

(defn start
  []
  (reset! server (run-server #'app {:port 8080}))
  (reset! used true))

(defn stop
  []
  (when @used
    (@server :timeout 100)
    (reset! server nil)
    (reset! used nil)))

(defn go
  []
  (init)
  (start)
  :ready)

(defn reset
  []
  (stop)
  (q/reset-tables!)
  (start))

(defn t []
  (test/run-tests 'toybox.handler-test))

(defn view-db []
  [(q/select-user q/db-spec)
   (q/select-item q/db-spec)])

(defn view2 []
  (q/select-order q/db-spec))

(defn view3 []
  (q/select-orderitem q/db-spec))

(defn launch []
  (q/reset-tables!)
  (q/nonnegative-quantity! @q/db))

