(ns toybox.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [toybox.handler :refer :all]
            [toybox.query :as q])
  (:import org.postgresql.ds.PGPoolingDataSource))

(def config (read-string (slurp "config.edn")))

(defonce test-database
  {:datasource
   (doto (new PGPoolingDataSource)
     (.setServerName     "localhost")
     (.setDatabaseName   "toyboxtest")
     (.setUser           (:username config))
     (.setPassword       (:password config))
     (.setMaxConnections 10))})



(deftest test-app
  (q/reset-tables! test-database)
  (testing ":get /"
    (let [response (app-routes (mock/request :get "/"))]
      (is (= (:status response) 200))))

  (testing ":get /invalid"
    (let [response (app-routes (mock/request :get "/invalid"))]
      (is (= (:status response) 404))))

  (testing ":get /register"
    (let [response (app-routes (mock/request :get "/register"))]
      (is (= (:status response) 200))))

  ;; how to generate a test database and wipe it?
  (testing ":post /register"
    (let [unique-login-request (mock/request :post "/register" {:username "ausername"
                                                                :password "apassword"})
          unique-login-response (app-routes unique-login-request)

          duplicate-login-request unique-login-request
          duplicate-login-response (app-routes duplicate-login-request)

          empty-username-request (mock/request :post "/register" {:username ""
                                                                  :password "apassword"})
          empty-username-response (app-routes empty-username-request)]
      (is (=    (:status unique-login-response) 201))
      ;; TODO: RESTful response for failure?
      (is (= (:status duplicate-login-response) 303))
      (is (= (:status empty-username-response) 303)))))
