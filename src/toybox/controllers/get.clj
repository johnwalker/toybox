(ns toybox.controllers.get
  (:require [ring.util.response :refer [response content-type status]]
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
  (t/home-page (get-in r [:db :userrole])))

(defn inventory [r]
  (let [items (q/select-item q/db-spec)
        cart  (get-in r [:session :cart])]
    (-> (response (t/inventory-page (get-in r [:db :userrole]) cart items))
        (content-type "text/html"))))

(defn admin-inventory [r]
  (-> (response (t/admin-inventory-page []))))

(defn sales [r]
  (let [role (get-in r [:db :userrole])]
    (if (#{"manager"} role)
      (let [orders (partition-by :orderid (q/select-all-customer-orders q/db-spec))]
        ;; close ...
        (-> (response (t/order-page role orders))
            (content-type "text/html"))))))

(defn my-orders [r]
  (let [orders (partition-by :orderid (q/select-customer-orders q/db-spec (get-in r [:db :useraccountid])))]
    (-> (response (t/my-order-page (get-in r [:db :userrole]) orders))
        (assoc :session (:session r))
        (content-type "text/html"))))

(defn register [r]
  (-> (response (t/registration-page))
      (content-type "text/html")))

(defn cart [r]
  (let [cart-items (get-in r [:session :cart])]
    (-> (response (t/cart-page (get-in r [:db :userrole]) cart-items))
        (content-type "text/html"))))

(defn pending-orders [r]
  (let [pending-orders (partition-by :orderid (q/select-order-with-status q/db-spec "pending"))
        role (get-in r [:db :userrole])]
    (if (#{"staff" "manager"} role)
      (-> (response (t/pending-order-page role pending-orders))
          (content-type "text/html"))
      (-> (response "unauthorized. you must first login.")
          (status 400)
          (content-type "text/html")))))

(defn staff-inventory [r]
  (let [role (get-in r [:db :userrole])]
    (if (#{"staff" "manager"} role)
      (let [items (q/select-item q/db-spec)]
        (-> (response (t/staff-inventory-page role items))
            (content-type "text/html")))
      (-> (response "unauthorized")
          (status 400)))))

(defn manager-promorates [r]
  (let [role (get-in r [:db :userrole])]
    (if (#{"manager"} role)
      (let [items (q/select-item q/db-spec)]
        (-> (response (t/manager-promorate-page role items))
            (content-type "text/html")))
      (-> (response "unauthorized")
          (status 400)))))
