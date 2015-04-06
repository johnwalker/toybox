(ns toybox.store
  (:require [buddy.hashers :as hs])
  (:import [clojure.lang IDeref]))

(defprotocol IUserstore
  (add-user! [x m]))

(defprotocol ISessionstore
  (add-session! [x m]))

(defrecord Memstore [a]
  IUserstore
  (add-user! [x m]
    (swap! a conj (update-in m :password (hs/encrypt (:password m))))
    x)
  IDeref
  (deref [x]
    @a))

(defrecord Sessionstore [a]
  ISessionstore
  (add-session! [x m]
    (swap! a m conj) x)
  IDeref
  (deref [x]
    @a))

(def ds (->Memstore (atom {})))
