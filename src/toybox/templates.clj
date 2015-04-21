(ns toybox.templates
  (:require [hiccup.page :refer [html5]]
            [ring.util.anti-forgery :refer [anti-forgery-field]]))

(defn sign-out []
  [:form {:action "/logout"
          :method :post}
   (anti-forgery-field)
   [:input {:type :submit :value "Sign out?"}]])

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

(defn item-div [item & staff]
  [:div {:name :item}
   [:form {:action (if staff
                     "/staff/update-quantity"
                     "/add-to-cart")
           :method :post}
    (anti-forgery-field)
    [:input {:type "hidden"
             :name "itemname"
             :value (:itemname item)}]
    [:input {:type "hidden"
             :name "price"
             :value (:price item)}]
    [:input {:type "hidden"
             :name "quantity"
             :value 1}]
    [:input {:type "hidden"
             :name "itemid"
             :value (:itemid item)}]
    [:p {:name :id}       (str "Item ID: " (:itemid item))]
    [:p {:name :name}     (str "Item name: " (:itemname item))]
    [:p {:name :price}    (str "Price: " (:price item))]
    [:p {:name :quantity} (str "Quantity: " (:quantity item))]
    (if staff
      [:div {:name "staff-submit"}
       [:input {:type "text"
                :name "new-quantity"}]
       [:input {:type "submit"
                :value "Update quantity"}]]
      [:input {:type "submit"
               :value "Add to cart"}])]])

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
    (sign-out)]))

(defn unsigned-admin-page []
  (html5
   {:lang "en"}
   [:head]
   [:body
    (admin-form)]))

(def signed-admin-page signed-login-page)

(defn user-elements [role]
  (when (#{"user" "staff" "manager"} role)
    [:div {:id "userelements"}
     [:p [:a {:href "/my-orders"} "My orders"]]
     [:p [:a {:href "/cart"} "Cart"]]]))

(defn logout-elements [role]
  (when (#{"user" "staff" "manager"} role)
    [:div {:id "logout-elements"}
     [:p [:a {:href "/logout"} "Logout"]]]))

(defn staff-elements [role]
  (when (#{"staff" "manager"} role)
    [:div {:id "staffelements"}
     [:p [:a {:href "/staff/inventory"} "Staff inventory"]]
     [:p [:a {:href "/orders"} "All orders"]]
     [:p [:a {:href "/staff/pending-orders"} "Pending Orders"]]]))

(defn manager-elements [role]
  (when (#{"manager"} role)
    [:div {:id "managerelements"}
     [:p [:a {:href "/manager/promotion"} "Promotions"]]]))

(defn none-elements [role]
  (when ((complement #{"user" "manager" "staff"}) role)
    [:div {:id :none}
     [:div {:id "Register"}
      [:a {:href "/register"} "Register"]]
     [:div {:id "login"}
      [:p "Login"]
      (login-form)]]))

(defn nav-bar [role]
  [:nav [:p [:a {:href "/inventory"} "Inventory"]]
   (user-elements role)
   (staff-elements role)
   (manager-elements role)
   (none-elements role)
   (logout-elements role)])

(defn home-page [role]
  (html5
   {:lang "en"}
   [:head [:title "Home"]]
   [:body
    (nav-bar role)
    (when-not role
      [:div {:id "unsigned"}
       ;; [:div {:id "login"}
       ;;  [:h1 "Login"]
       ;;  (login-form)]
       [:div {:id "register"}
        [:h1 "Or register a new account"]
        (registration-form)]])


    ]))



(defn registration-page []
  (html5
   {:lang "en"}
   [:head [:title "Register"]]
   [:body
    [:p "register here."]
    (registration-form)]))


(defn admin-page []
  (html5
   {:lang "en"}
   [:head [:title "Register"]]
   [:body
    [:p "wowzor you are admin"]]))

(defn add-item-form []
  [:form {:action "/admin/add-item"
          :method :post}
   (anti-forgery-field)
   [:input {:type "text"
            :name "name"
            :inputmode "verbatim"
            :placeholder "item name"
            :required true}]
   [:input {:type "text"
            :name "price"
            :placeholder "price"
            :inputmode "verbatim"
            :required true}]
   [:input {:type "text"
            :name "quantity"
            :placeholder "quantity"
            :inputmode "verbatim"
            :required true}]
   [:input {:type "submit"
            :value "Login"}]])

(defn admin-inventory-page [items]
  (html5
   {:lang "en"}
   [:head [:title "Register"]]
   [:body
    [:p "wowzor admin."]
    [:p "want you add item?"]
    (add-item-form)
    (for [item items]
      (item-div item :admin))]))

(defn cart-item [item]
  [:div {:name :item}
   [:input {:type "hidden"
            :name "itemname"
            :value (:itemname item)}]
   [:input {:type "hidden"
            :name "price"
            :value (:price item)}]
   [:p {:name :id}       (str "Item ID: " (:itemid item))]
   [:p {:name :name}     (str "Item name: " (:itemname item))]
   [:p {:name :price}    (str "Price: " (:price item))]
   [:p {:name :quantity} (str "Quantity: " (:quantity item))]])

(defn cart-div [items]
  [:div {:id "cart"}
   (if (seq items)
     [:div {:id "nonempty"}
      (map cart-item (vals items))
      [:form {:action "/submit-cart"
              :method :post}
       (anti-forgery-field)
       [:input {:type "submit"
                :value "Submit order"}]]
      [:form {:action "/clear-cart"
              :method :post}
       (anti-forgery-field)
       [:input {:type "submit"
                :value "Clear cart"}]]]
     [:div {:id "empty"}
      [:p "Your cart is currently empty."]])])

(defn cart-page [role items]
  (html5
   {:lang "en"}
   [:head [:title "Staff page"]]
   [:body
    (nav-bar role)
    (cart-div items)]))

(defn inventory-page [role cart items]
  (html5
   {:lang "en"}
   [:head [:title "Register"]]
   [:body
    (nav-bar role)
    (cart-div cart)
    [:div {:id "inventory"}
     (map item-div items)]]))

(defn order-item-div [order]
  [:div {:name :orderwrapper}
   [:div {:name :details}
    [:p {:name :orderid} (str "Order #"(:orderid (first order)))]
    [:p {:name :useraccountid} (str "For customer #"(:useraccountid (first order)))]
    [:p {:name :placementtime} (str "Placement time: "(:placementtime (first order)))]
    [:p {:name :orderstatus} (str "Order status: "(:orderstatus (first order)))]]
   (for [item order]
     [:div {:name :order}
      [:p {:name :id}       (str "Item ID: " (:itemid item))]
      [:p {:name :name}     (str "Item name: " (:itemname item))]
      [:p {:name :price}    (str "Price: " (:price item))]
      [:p {:name :quantity} (str "Quantity: " (:quantity item))]])])

(defn order-div [order]
  (map order-item-div order))

(defn staff-inventory-page [role items]
  (html5
   {:lang "en"}
   [:head [:title "Register"]]
   [:body
    (nav-bar role)
    [:div {:id "inventory"}
     (map #(item-div % :staff) items)]]))

(defn pending-order [order]
  [:div {:name :pending}
   [:form {:action "/staff/ship"
           :method :post}
    (anti-forgery-field)
    [:input {:type "hidden"
             :name "orderid"
             :value (:orderid (first order))}]
    [:p {:name :orderid} (str "Order #"(:orderid (first order)))]
    [:p {:name :useraccountid} (str "For customer #"(:useraccountid (first order)))]
    [:p {:name :placementtime} (str "Placement time: "(:placementtime (first order)))]
    [:p {:name :orderstatus} (str "Order status: "(:orderstatus (first order)))]
    (for [item order]
      [:div {:name :order}
       [:p {:name :id}       (str "Item ID: " (:itemid item))]
       [:p {:name :name}     (str "Item name: " (:itemname item))]
       [:p {:name :price}    (str "Price: " (:price item))]
       [:p {:name :quantity} (str "Quantity: " (:quantity item))]])
    [:input {:type :submit :value "Ship it"}]]])

(defn pending-order-page [role orders]
  (html5
   {:lang "en"}
   [:head [:title "Pending Orders"]]
   [:body
    (nav-bar role)
    [:div {:id "pendingorders"}
     (map pending-order orders)]]))

(defn order-page [role orders]
  (html5
   {:lang "en"}
   [:head [:title "Pending Orders"]]
   [:body
    (nav-bar role)
    [:div {:id "pendingorders"}
     (order-div orders)]]))

(defn my-order-page [role orders]
  (html5
   {:lang "en"}
   [:head [:title "Pending Orders"]]
   [:body
    (nav-bar role)
    (order-div orders)]))
