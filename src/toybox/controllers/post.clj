(ns toybox.controllers.post
  (:require [ring.util.response :refer [response content-type]]
            [toybox.templates :as v]
            [toybox.query :as q]))

(defn admin [r]
  (pr-str r))

(defn login [r]
  (let [p (:params r)
        s (q/find-user-pass q/db-spec (:username p) (:password p))]
    (if (seq s)
      (-> (response "user/pass found")
          (assoc-in [:session :username] (:username p))
          (assoc-in [:session :password] (:password p))
          (content-type "text/html"))
      (response "invalid user/pass"))))

(defn logout [r]
  (-> (response "login session cleared.")
      (assoc :session nil)
      (content-type "text/html")))

(defn register [r]
  (let [p (:params r)]
    (try
      (q/add-user! q/db-spec
                   (:username p)
                   (:password p))
      ;; TODO: redirect to acc
      (-> (response "user created.")
          (content-type "text/html"))
      (catch Exception e
        (-> (response (str "failed to create user " (:username p)))
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
