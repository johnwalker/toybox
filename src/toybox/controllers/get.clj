(ns toybox.controllers.get
  (:require [ring.util.response :refer [response content-type]]
            [toybox.templates :as t]
            [toybox.query :as q]))

(defn login [r]
  (let [s (:session r)
        u (:username s)]
    (if u
      (t/signed-login-page)
      (t/unsigned-login-page))))

(defn admin [r]
  (let [s (:session r)
        u (:username s)
        a (:admin u)]
    ;; TODO make it so not everyone is admin
    (if (and true u)
      (t/signed-login-page)
      (t/unsigned-admin-page))))

(defn home [r]
  (t/home-page (:db r)))

(defn inventory [r]
  (let [items (q/select-item q/db-spec)
        cart  (get-in r [:session :cart])]
    (-> (response (t/inventory-page r cart items))
        (content-type "text/html"))))

(defn admin-inventory [r]
  (-> (response (t/admin-inventory-page []))))

(defn orders [r]
  ;; (q/select-order q/db-spec)
  (let [cart-items (get-in r [:session :cart])
        p    (:session r)
        s    (q/find-user+pass q/db-spec (:username p) (:password p))
        orders (partition-by :orderid (q/select-customer-orders q/db-spec (:useraccountid (first s))))]
    (-> (response (t/order-page orders))
        (content-type "text/html"))))

(defn register [r]
  (-> (response (t/registration-page))
      (content-type "text/html")))

(defn cart [r]
  (let [cart-items (get-in r [:session :cart])]
    (println r)
    (-> (response (t/cart-page r cart-items))
        (content-type "text/html"))))
