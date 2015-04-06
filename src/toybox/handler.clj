(ns toybox.handler
  (:require [toybox.controllers.get :as g]
            [toybox.controllers.post :as p]
            [compojure.core :refer [defroutes GET POST]]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defroutes app-routes
  (GET "/"          [] g/home)
  (GET "/login"     [] g/login)
  (GET "/admin"     [] g/admin)
  (GET "/inventory" [] g/inventory)
  (GET "/orders"    [] g/orders)
  (GET "/register"  [] g/register)

  (POST "/admin"    [] p/admin)
  (POST "/login"    [] p/login)
  (POST "/logout"   [] p/logout)  
  (POST "/register" [] p/register)

  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
