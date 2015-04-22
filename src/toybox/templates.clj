(ns toybox.templates
  (:require [hiccup.page :refer [html5 include-css]]
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

(defn user-elements [role]
  (when (#{"user" "staff" "manager"} role)
    [:div {:id "userelements"}
     [:p [:a {:href "/my-orders"} "My Orders"]]
     [:p [:a {:href "/cart"} "Cart"]]]))

(defn logout-elements [role]
  (when (#{"user" "staff" "manager"} role)
    [:div {:id "logout-elements"}
     [:p [:a {:href "/logout"} "Logout"]]]))

(defn staff-elements [role]
  (when (#{"staff" "manager"} role)
    [:div {:id "staffelements"}
     [:p [:a {:href "/staff/inventory"} "Staff inventory"]]
     [:p [:a {:href "/staff/pending-orders"} "Pending Orders"]]]))

(defn manager-elements [role]
  (when (#{"manager"} role)
    [:div {:id "managerelements"}
     [:p [:a {:href "/manager/statistics"} "Statistics"]]
     [:p [:a {:href "/manager/promorate"} "Promorates"]]]))

(defn none-elements [role]
  (when ((complement #{"user" "manager" "staff"}) role)
    [:div {:id :none}
     [:div {:id "Register"}
      [:a {:href "/register"} "Register"]]
     [:div {:id "login"}
      (login-form)]]))

(defn nav-bar [role]
  [:nav [:p [:a {:href "/inventory"} "Inventory"]]
   (user-elements role)
   (staff-elements role)
   (manager-elements role)
   (none-elements role)
   (logout-elements role)])

(defn item-table [item itemid? name? price? quantity? promorate?]
  [:table
   (when itemid?    [:tr [:td "Item ID"]   [:td (:itemid item)]])
   (when name?      [:tr [:td "Name"]      [:td (:itemname item)]])
   (when price?     [:tr [:td "Price"]     [:td (:price item)]])
   (when quantity?  [:tr [:td "Quantity"]  [:td (:quantity item)]])
   (when promorate? [:tr [:td "Promorate"] [:td (if-let [p (:promorate item)] p "0.0")]])])

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

(defn item-div [item & staff]
  [:div {:name :item}
   [:form {:action (case (first staff)
                     :staff "/staff/update-quantity"
                     :manager "/manager/update-promorate"
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
    (item-table item true true true true true)
    (case (first staff)
      :staff [:div {:name "staff-submit"}
              [:input {:type "text"
                       :name "new-quantity"}]
              [:input {:type "submit"
                       :value "Update quantity"}]]
      :manager [:div {:name "manager-submit"}
                [:input {:type "text"
                         :name "new-promorate"}]
                [:input {:type "submit"
                         :value "Update promorate"}]]
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
   [:head
    (include-css "/css/main.css")]
   [:body
    (nav-bar nil)
    [:h1 "Login"]
    (login-form)
    [:h1 "Want a username?"]
    [:a {:href "/register"} "Register here."]]))

(defn signed-login-page []
  (html5
   {:lang "en"}
   [:head]
   [:body
    [:p "Currently signed in."]
    (sign-out)]))

(defn home-page [role]
  (html5
   {:lang "en"}
   [:head
    [:title "Home"]
    [:meta {:charset "UTF-8"}]
    (include-css "/css/main.css")
    (include-css "/css/home.css")]
   [:body
    (nav-bar role)]))

(defn registration-page []
  (html5
   {:lang "en"}
   [:head
    [:title "Register"]
    [:meta {:charset "UTF-8"}]
    (include-css "/css/main.css")]
   [:body
    (nav-bar nil)
    [:div {:id :register}
     [:h1 "Register a new account."]
     (registration-form)]]))

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

(defn cart-item [item]
  [:div {:name :item}
   [:input {:type "hidden"
            :name "itemname"
            :value (:itemname item)}]
   [:input {:type "hidden"
            :name "price"
            :value (:price item)}]
   (item-table item true true true true false)])

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
   [:head
    [:title "My cart"]
    [:meta {:charset "UTF-8"}]
    (include-css "/css/main.css")
    (include-css "/css/cart.css")]
   [:body
    (nav-bar role)
    (cart-div items)]))

(defn inventory-page [role cart items]
  (html5
   {:lang "en"}
   [:head
    [:title "Inventory"]
    [:meta {:charset "UTF-8"}]
    (include-css "/css/main.css")
    (include-css "/css/inventory.css")]
   [:body
    (nav-bar role)
    [:div {:name :wrapper}
     (cart-div cart)
     [:div {:id "inventory"}
      (map item-div items)]]]))

(defn order-item-div [order]
  [:div {:name :orderwrapper}
   [:div {:name :details}
    [:p {:name :orderid} (str "Order #"(:orderid (first order)))]
    [:p {:name :useraccountid} (str "For customer #"(:useraccountid (first order)))]
    [:p {:name :placementtime} (str "Placement time: "(:placementtime (first order)))]
    [:p {:name :orderstatus} (str "Order status: "(:orderstatus (first order)))]]
   (for [item order]
     [:div {:name :order}
      (item-table item true true true true false)
      ])])

(defn order-div [order]
  (map order-item-div order))

(defn staff-inventory-page [role items]
  (html5
   {:lang "en"}
   [:head
    [:title "Staff Inventory"]
    [:meta {:charset "UTF-8"}]
    (include-css "/css/main.css")
    (include-css "/css/inventory.css")
    (include-css "/css/staffinventory.css")]
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
       (item-table item true true true true false)])
    [:input {:type :submit :value "Ship it"}]]])

(defn pending-order-page [role orders]
  (html5
   {:lang "en"}
   [:head
    [:title "Pending Orders"]
    [:meta {:charset "UTF-8"}]
    (include-css "/css/main.css")
    (include-css "/css/pending.css")]
   [:body
    (nav-bar role)
    [:div {:id "pendingorders"}
     (if (seq orders)
       (map pending-order orders)
       [:p "No pending orders."])]]))

(defn order-page [role orders]
  (html5
   {:lang "en"}
   [:head
    [:title "Statistics"]
    [:meta {:charset "UTF-8"}]
    (include-css "/css/main.css")
    (include-css "/css/statistics.css")]
   [:body
    (nav-bar role)
    [:div {:id "pendingorders"}
     (order-div orders)]]))

(defn my-order-page [role orders]
  (html5
   {:lang "en"}
   [:head
    [:title "My Orders"]
    [:meta {:charset "UTF-8"}]
    (include-css "/css/main.css")
    (include-css "/css/order.css")]
   [:body
    (nav-bar role)
    (if (seq orders)
      (order-div orders)
      [:p "You don't have any orders."])]))

(defn manager-promorate-page [role items]
  (html5
   {:lang "en"}
   [:head
    [:title "Pending Orders"]
    [:meta {:charset "UTF-8"}]
    (include-css "/css/main.css")
    (include-css "/css/manager.css")]
   [:body
    (nav-bar role)
    (map #(item-div % :manager) items)]))

(defn manager-statistics-page [role]
  (html5
   {:lang "en"}
   [:head
    [:title "Statistics"]
    [:meta {:charset "UTF-8"}]
    (include-css "/css/main.css")
    (include-css "/css/manager.css")]
   [:body
    (nav-bar role)
    [:div {:id :statistics}
     [:p [:a {:href "/manager/statistics/week"} "Last week"]]
     [:p [:a {:href "/manager/statistics/month"} "Last month"]]
     [:p [:a {:href "/manager/statistics/year"} "Last year"]]]]))
