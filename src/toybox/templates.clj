(ns toybox.templates
  (:require [hiccup.page :refer [html5]]
            [ring.util.anti-forgery :refer [anti-forgery-field]]))

(defn login-form []
  [:form {:action "/login"
          :method :post}
   (anti-forgery-field)
   [:input {:type "text"
            :name "username"
            :inputmode "verbatim"
            :placeholder "username"
            :required true}]
   [:input {:type "password"
            :name "password"
            :placeholder "password"
            :inputmode "verbatim"
            :required true}]
   [:input {:type "submit"
            :value "Login"}]])

(defn admin-form []
  [:form {:action "/admin"
          :method :post}
   (anti-forgery-field)
   [:input {:type "text"
            :name "username"
            :inputmode "verbatim"
            :placeholder "username"
            :required true}]
   [:input {:type "password"
            :name "password"
            :placeholder "password"
            :inputmode "verbatim"
            :required true}]
   [:input {:type "submit"
            :value "Login"}]])

(defn item-div [item]
  [:div {:name :item}
   [:p {:name :name}     (:name item)]
   [:p {:name :price}    (:price item)]
   [:p {:name :quantity} (:quantity item)]
   [:p {:name :status}   (:status item)]])

(defn listing-div [listing]
  [:div {:id :listing}
   (map item-div listing)])

(defn cart [items]
  [:div {:id "cart"}
   (for [item items]
     [:p (:name item)])])

(defn orders [items]
  [:div {:id "orders"}
   (for [item items]
     [:p (:name item)])])

(defn unsigned-login-page []
  (html5
   {:lang "en"}
   [:head]
   [:body
    (login-form)
    [:p "think you're cool enough to own a login?"]
    [:a {:href "/register"} "give us your credit card here."]]))

(defn signed-login-page []
  (html5
   {:lang "en"}
   [:head]
   [:body
    [:p "Currently signed in."]
    [:form {:action "/logout"
            :method :post}
     (anti-forgery-field)
     [:input {:type :submit :value "Sign out?"}]]]))

(defn unsigned-admin-page []
  (html5
   {:lang "en"}
   [:head]
   [:body
    (admin-form)]))

(def signed-admin-page signed-login-page)

(defn home-page []
  (html5
   {:lang "en"}
   [:head [:title "Home"]]
   [:body
    [:p "welcome to the toy store."]
    [:p "no soliciting."]
    [:p [:a {:href "/register"} "register here."]]
    [:p [:a {:href "/login"} "login here."]]]))

(defn registration-form []
  [:form {:action "/register"
          :method :post}
   (anti-forgery-field)
   [:input {:type "text"
            :name "username"
            :inputmode "verbatim"
            :placeholder "username"
            :required true}]
   [:input {:type "password"
            :name "password"
            :placeholder "password"
            :inputmode "verbatim"
            :required true}]
   [:input {:type :submit :value "Register"}]])


(defn registration-page []
  (html5
   {:lang "en"}
   [:head [:title "Register"]]
   [:body
    [:p "register here."]
    (registration-form)]))
