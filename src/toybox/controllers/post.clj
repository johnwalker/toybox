(ns toybox.controllers.post
  (:require [ring.util.response :refer [redirect response content-type status]]
            [toybox.templates :as v]
            [toybox.query :as q]))

(defn admin [r]
  (pr-str r))

(defn login [r]
  (let [p (:params r)
        s (first (q/find-user+pass q/db-spec (:username p) (:password p)))]
    (if s
      (-> (redirect "/")
          (assoc :session (:session r))
          (assoc-in [:session] (select-keys s [:username :password :userrole]))
          (content-type "text/html"))

      (redirect "/login"))))

(defn logout [r]
  (-> (redirect "/")
      (assoc :session nil)
      (content-type "text/html")))

(defn register [r]
  (let [p (:params r)]
    (try
      (q/insert-user! q/db-spec
                      (:username p)
                      (:password p))
      ;; TODO: redirect to acc
      (-> (response "user created.")
          (content-type "text/html"))
      (catch Exception e
        (-> (response (str "failed to create user " (:username p)))
            (status 400)
            (content-type "text/html"))))))

(defn add-item [r]
  (let [{:keys [name price quantity]} (:params r)]
    (try
      (q/insert-item! q/db-spec
                      name
                      price
                      quantity)
      (-> (response "Added item")
          (content-type "text/html"))
      (catch Exception e
        (-> (response "Failed to add item...")
            (content-type "text/html"))))))

(defn add-to-cart [r]
  (if (:db r)
    (let [q (select-keys (:params r) [:itemname :price :itemid :quantity])]
      (try
        (-> (redirect "/inventory")
            (assoc :session (:session r))
            (update-in [:session :cart (:itemid q)]
                       (fn [o q]
                         (if o
                           (update-in o [:quantity] (fn [l r] (+ (if (string? l)
                                                                   (Integer/parseInt l)
                                                                   l)
                                                                 (if (string? r)
                                                                   (Integer/parseInt r)
                                                                   r))) (:quantity q))
                           q)) q)
            (content-type "text/html"))
        (catch Exception e
          (-> (response "WTF")
              (content-type "text/html")))))
    (redirect "/login")))

(defn clear-cart [r]
  (-> (redirect "/inventory")
      (assoc :session (:session r))
      (assoc-in [:session :cart] nil)
      (content-type "text/html")))

(defn submit-cart [r]
  (let [cart (vals (get-in r [:session :cart]))
        p    (:session r)
        s    (q/find-user+pass q/db-spec (:username p) (:password p))]
    (if (seq s)
      (try
        (let [qr (q/insert-order<! q/db-spec (:useraccountid (first s)))]
          (doseq [item cart]
            ;; FIXME: security vulnerability, i don't care right now.
            (q/insert-orderitem! q/db-spec
                                 (:generated_key qr)
                                 (:itemid item)
                                 (:quantity item)
                                 (:price item))))
        (-> (clear-cart r)
            (assoc-in [:headers "Location"] "/orders")))
      (redirect "/login"))))


(defn update-quantity [r]
  (let [role (get-in r [:db :userrole])
        {:keys [new-quantity itemid]} (:params r)]
    (if (#{"staff" "manager"} role)
      (try
        (let [new-quantity (Integer/parseInt new-quantity)
              itemid (Integer/parseInt itemid)]
          (q/update-item-quantity! q/db-spec new-quantity itemid)
          (redirect "/staff/inventory"))
        (catch Exception e
          (-> (response (str "Failed to update quantity for itemid " itemid))
              (content-type "text/html"))))

      (-> (response "unauthorized")
          (status 400)))))

(defn ship-order [r]
  (let [role (get-in r [:db :userrole])
        orderid (get-in r [:params :orderid])]
    (if (#{"staff" "manager"} role)
      (try
        (let [orderid (Integer/parseInt orderid)]
          (q/approve-order! q/db-spec orderid)
          (redirect "/inventory"))
        (catch Exception e
          (-> (response (str "Insufficient quantity of item in inventory. Order not shipped."))
              (content-type "text/html"))))
      (-> (response "unauthorized")
          (status 400)))))
