(ns toybox.handler
  (:require [org.httpkit.server :refer [run-server]]
            [toybox.controllers.get :as g]
            [toybox.controllers.post :as p]
            [toybox.query :as q]
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
  (POST "/add-to-cart" [] (-> p/add-to-cart
                              m/authenticated?))
  (POST "/clear-cart"  [] p/clear-cart)
  (POST "/submit-cart" [] (-> p/submit-cart
                              m/authenticated?))

  (GET "/cart"      [] (-> g/cart
                           m/authenticated?))
  (GET "/my-orders" [] (-> g/my-orders
                           m/authenticated?))

  (GET "/staff/pending-orders"    [] (-> g/pending-orders
                                         m/authenticated?))

  (GET "/staff/inventory" [] (-> g/staff-inventory
                                 m/authenticated?))
  (POST "/staff/update-quantity" [] (-> p/update-quantity
                                        m/authenticated?))
  (POST "/staff/ship" [] (-> p/ship-order
                             m/authenticated?))

  (GET "/manager/promorate" [] (-> g/manager-promorates
                                   m/authenticated?))

  (POST "/manager/update-promorate" [] (-> p/update-promorate
                                           m/authenticated?))

  (GET "/manager/statistics"    [] (-> g/statistics
                                       m/authenticated?))

  (GET "/login"     [] g/login)
  (POST "/login"    [] p/login)
  (GET "/logout"    [] p/logout)
  (POST "/logout"   [] p/logout)

  (GET  "/register" []  g/register)
  (POST "/register" [] p/register)
  (route/resources "/")
  (route/not-found "Not Found"))

(def api (wrap-defaults app-routes api-defaults))

(def app (wrap-defaults app-routes site-defaults))


(defn -main []
  (q/reset-tables!)
  (run-server app {:port 8000}))
