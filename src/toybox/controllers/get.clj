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

(defn orders [r]
  (let [cart-items (vals (get-in r [:session :cart]))
        orders (partition-by :orderid (q/select-customer-orders q/db-spec (get-in r [:db :useraccountid])))]
    (-> (response (t/order-page (get-in r [:db :userrole]) orders))
        (content-type "text/html"))))

(defn register [r]
  (-> (response (t/registration-page))
      (content-type "text/html")))

(defn cart [r]
  (let [cart-items (get-in r [:session :cart])]
    (-> (response (t/cart-page (get-in r [:db :userrole]) cart-items))
        (content-type "text/html"))))

(defn pending-orders [r]
  (let [pending-orders (q/select-order-with-status q/db-spec "pending")
        orderitems (doall (for [order pending-orders]
                            (q/select-orderitem-order q/db-spec (:orderid order))))]

    (-> (response "SHIT SHIT SHIT")
        (content-type "text/html"))))

(defn staff-inventory [r]
  (let [role (get-in r [:db :userrole])]
    (if (#{"staff" "manager"} role)
      (let [items (q/select-item q/db-spec)]
        (-> (response (t/staff-inventory-page role items))
            (content-type "text/html")))
      (-> (response "unauthorized")
          (status 400)))))
