(ns toybox.handler
  (:require [org.httpkit.server :refer [run-server]]
            [toybox.controllers.get :as g]
            [toybox.controllers.post :as p]
            [toybox.middleware :as m]
            [compojure.core :refer [defroutes GET POST]]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defonce server (atom nil))

(defroutes app-routes
  (GET "/"          [] g/home)
  (GET "/login"     [] g/login)
  (GET "/admin"     [] g/admin)
  (GET "/inventory" [] (-> g/inventory
                           m/authenticated?))
  (GET "/orders"    [] (-> g/orders
                           m/authenticated?))
  (GET "/register"  [] g/register)

  (POST "/admin"    [] p/admin)
  (POST "/login"    [] p/login)
  (POST "/logout"   [] p/logout)
  (POST "/register" [] p/register)

  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))

(defn restart-server []
  (when-let [x @server]
    (@server :timeout 100)
    (reset! server nil))
  (reset! server (run-server app {:port 8080})))

(defn -main [& args]
  (restart-server))

