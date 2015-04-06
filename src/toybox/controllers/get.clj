(ns toybox.controllers.get
  (:require [ring.util.response :refer [response content-type]]
            [toybox.templates :as t]))

(defn login [r]
  (let [s (:session r)
        u (:username s)]
    (if u
      (t/signed-login-page)
      (t/unsigned-login-page))))

;; need to check that user is /rly/ admin,
;; not just that they say they r.
(defn admin [r]
  (let [s (:session r)
        u (:username s)
        a (:admin u)]
    (if (and a u)
      (t/signed-login-page)
      (t/unsigned-admin-page))))

(defn home [r]
  (t/home-page))

(defn inventory [r]
  (pr-str r))

(defn orders [r]
  (pr-str r))

(defn register [r]
  (t/registration-page))
