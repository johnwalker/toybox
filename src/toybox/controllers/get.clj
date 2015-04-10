(ns toybox.controllers.get
  (:require [ring.util.response :refer [response content-type]]
            [toybox.templates :as t]))

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
  (t/home-page))

(defn inventory [r]
  (-> (response "smoke weed")
      (content-type "text/html")))

(defn admin-inventory [r]
  (-> (response (t/admin-inventory-page []))))

(defn orders [r]
  (pr-str r))

(defn register [r]
  (-> (response (t/registration-page))
      (content-type "text/html")))
