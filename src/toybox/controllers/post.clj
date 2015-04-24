(ns toybox.controllers.post
  (:require [ring.util.response :refer [redirect response content-type status]]
            [toybox.templates :as t]
            [toybox.query :as q]))

(defn admin [r]
  (pr-str r))

(defn login [r]
  (let [p (:params r)
        s (first (q/find-user+pass @q/db (:username p) (:password p)))]
    (if s
      (-> (redirect "/inventory")
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
      (q/insert-user! @q/db
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
      (q/insert-item! @q/db
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
        s    (q/find-user+pass @q/db (:username p) (:password p))]
    (if (seq s)
      (try
        (let [qr (q/insert-order<! @q/db (:useraccountid (first s)))]
          (doseq [item cart]
            (q/insert-orderitem! @q/db
                                 (:generated_key qr)
                                 (:itemid item)
                                 (:quantity item))))
        (-> (clear-cart r)
            (assoc-in [:headers "Location"] "/my-orders")))
      (redirect "/login"))))


(defn update-quantity [r]
  (let [role (get-in r [:db :userrole])
        {:keys [new-quantity itemid]} (:params r)]
    (if (#{"staff" "manager"} role)
      (try
        (let [new-quantity (Integer/parseInt new-quantity)
              itemid (Integer/parseInt itemid)]
          (q/update-item-quantity! @q/db new-quantity itemid)
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
          (q/approve-order! @q/db orderid)
          (redirect "/staff/pending-orders"))
        (catch Exception e
          (-> (response (t/insufficient-item-page role (q/select-insufficient-items @q/db orderid)))
              (content-type "text/html"))))
      (-> (response "unauthorized")
          (status 400)))))

(defn update-promorate [r]
  (let [role (get-in r [:db :userrole])
        {:keys [itemid new-promorate]} (:params r)]
    (if (#{"manager"} role)
      (try
        (println new-promorate)
        (let [itemid (Integer/parseInt itemid)
              new-promorate (Float/parseFloat new-promorate)]
          (q/update-item-promorate! @q/db new-promorate itemid)
          (redirect "/manager/promorate"))
        (catch Exception e
          (-> (response (str "Invalid promorate. Must be between 1 and 0"))
              (content-type "text/html"))))
      (-> (response "unauthorized")
          (status 400)))))
