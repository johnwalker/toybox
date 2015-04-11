(ns toybox.middleware
  (:require [toybox.query :as q]
            [ring.util.response :refer [response content-type]]))

(defn require-authenticated [app]
  (fn [r]
    (let [ps (:session r)
          s  (q/find-user+pass q/db-spec
                               (:username ps)
                               (:password ps))]
      (if (seq s)
        (app (assoc r :db (first s)))
        (-> (response "invalid auth")
            (content-type "text/html"))))))

(defn authenticated? [app]
  (fn [r]
    (let [ps (:session r)
          s  (q/find-user+pass q/db-spec
                               (:username ps)
                               (:password ps))]
      (if (seq s)
        (app (assoc r :db (first s)))
        (app r)))))
