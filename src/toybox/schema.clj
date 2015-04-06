(ns toybox.schema)

(defmulti valid identity)

(defmethod valid :username [])

(defmethod valid :password [])
