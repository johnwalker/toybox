(ns toybox.controllers.post
  (:require [ring.util.response :refer [response content-type]]
            [toybox.templates :as v]))

(defn admin [r]
  (pr-str r))

(defn login [r]
  (let [p (:params r)]
    (-> (response (str "we'll assume " (:username p) (:password p) " isn't a big phony"))
        (assoc-in [:session :username] (:username p))
        (assoc-in [:session :password] (:password p))
        (content-type "text/html"))))

(defn logout [r]
  (response "clearing login session.")
  (assoc :session nil)
  (content-type "text/html"))

(defn register [r]
  (pr-str r))
