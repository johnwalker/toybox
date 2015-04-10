(ns toybox.schema)

(defmulti valid identity)

(defmethod valid :username [s])

(defmethod valid :password [s])
