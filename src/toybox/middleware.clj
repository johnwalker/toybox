(ns toybox.middleware
  (:require [toybox.query :as q]
            [ring.util.response :refer [redirect response content-type status]]))

(defn require-authenticated [app]
  (fn [r]
    (let [ps (:session r)
          s  (q/find-user+pass @q/db
                               (:username ps)
                               (:password ps))]
      (if (seq s)
        (app (assoc r :db (first s)))
        (-> (redirect "/login")
            (status 400)
            (content-type "text/html"))))))

(defn authenticated? [app]
  (fn [r]
    (let [ps (:session r)
          s  (when ps (q/find-user+pass @q/db
                                        (:username ps)
                                        (:password ps)))]
      (if (seq s)
        (app (assoc r :db (first s)))
        (app (dissoc r :db))))))
