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

(defn home [r]
  (t/home-page (get-in r [:db :userrole])))

(defn inventory [r]
  (let [items (q/select-item @q/db)
        cart  (get-in r [:session :cart])]
    (-> (response (t/inventory-page (get-in r [:db :userrole]) cart items))
        (content-type "text/html"))))

(defn sub-statistics [r]
  (let [role (get-in r [:db :userrole])]
    (if (#{"manager"} role)
      (let [orders (partition-by :orderid (q/select-all-customer-orders @q/db))]
        ;; close ...
        (-> (response (t/order-page role orders))
            (content-type "text/html"))))))

(defn statistics [r]
  (let [role (get-in r [:db :userrole])]
    (if (#{"manager"} role)
      (-> (response (t/manager-statistics-page role))
          (content-type "text/html"))
      (-> (response "Not authorized.")
          (status 400)))))

(defn my-orders [r]
  (let [orders (partition-by :orderid (q/select-customer-orders @q/db (get-in r [:db :useraccountid])))]
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
  (let [pending-orders (partition-by :orderid (q/select-order-with-status @q/db "pending"))
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
      (let [items (q/select-item @q/db)]
        (-> (response (t/staff-inventory-page role items))
            (content-type "text/html")))
      (-> (response "unauthorized")
          (status 400)))))

(defn manager-promorates [r]
  (let [role (get-in r [:db :userrole])]
    (if (#{"manager"} role)
      (let [items (q/select-item @q/db)]
        (-> (response (t/manager-promorate-page role items))
            (content-type "text/html")))
      (-> (response "unauthorized")
          (status 400)))))
