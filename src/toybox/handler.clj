(ns toybox.handler
  (:require [toybox.controllers.get :as g]
            [toybox.controllers.post :as p]
            [toybox.middleware :as m]
            [compojure.core :refer [defroutes GET POST]]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults api-defaults]]))

(defroutes app-routes
  (GET "/"          [] (-> g/home
                           m/authenticated?))
  (GET "/inventory" [] (-> g/inventory
                           m/authenticated?))
  
  ;; check authorized
  (POST "/add-to-cart" [] (-> p/add-to-cart))
  (POST "/clear-cart"  [] p/clear-cart)
  (POST "/submit-cart" [] p/submit-cart)
  
  (GET "/cart"      [] g/cart)
  (GET "/orders"    [] g/orders)
  
  (GET "/manager/sales/statistics" [] identity)
  (GET "/manager/sales/promotion" [] identity)
  
  (POST "/staff"    [] identity)
  (POST "/staff/ship" [] identity)
  (POST "/staff/update-quantity" [] identity)
  
  (GET "/login"     [] g/login)
  (POST "/login"    [] p/login)
  (GET "/logout"    [] p/logout)
  (POST "/logout"   [] p/logout)

  (GET "/register" []  g/register)
  (POST "/register" [] p/register)

  (route/not-found "Not Found"))

(def api (wrap-defaults app-routes api-defaults))

(def app (wrap-defaults app-routes site-defaults))
