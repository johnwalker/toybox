(ns toybox.handler
  (:require [toybox.controllers.get :as g]
            [toybox.controllers.post :as p]
            [toybox.middleware :as m]
            [compojure.core :refer [defroutes GET POST]]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defroutes app-routes
  (GET "/"          [] g/home)
  (GET "/login"     [] g/login)
  (GET "/admin"     [] g/admin)
  (GET "/inventory" [] (-> g/inventory
                           m/authenticated?))
  ;; check authorized
  (GET "/admin/inventory" [] g/admin-inventory)
  (GET "/cart"      [] g/home)
  (GET "/orders"    [] (-> g/orders
                           m/require-authenticated))
  (GET "/register"  [] g/register)

  (POST "/admin"    [] p/admin)
  (POST "/admin/add-item" [] (-> p/add-item
                                 m/require-authenticated))
  (POST "/login"    [] p/login)
  (POST "/logout"   [] p/logout)
  (POST "/register" [] p/register)

  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))

