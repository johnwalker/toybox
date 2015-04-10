(ns toybox.middleware
  (:require [toybox.query :as q]
            [ring.util.response :refer [response content-type]]))

(defn authenticated? [app]
  (fn [r]
    (let [ps (:session r)
          s  (q/find-user-pass q/db-spec
                               (:username ps)
                               (:password ps))]
      (if (seq s) 
        (app r)
        (response "invalid auth")))))
