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
   [:form {:action "/add-to-cart"
           :method :post}
    (anti-forgery-field)
    [:input {:type "hidden"
             :name "itemname"
             :value (:itemname item)}]
    [:input {:type "hidden"
             :name "price"
             :value (:price item)}]
    [:input {:type "hidden"
             :name "itemid"
             :value (:itemid item)}]
    [:p {:name :id}       (str "Item ID: " (:itemid item))]
    [:p {:name :name}     (str "Item name: " (:itemname item))]
    [:p {:name :price}    (str "Price: " (:price item))]
    [:p {:name :quantity} (str "Quantity: " (:quantity item))]
    (if staff
      "meh, not done yet"
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

(defn user-elements [logged-in]
  (when (#{"user" "staff" "manager"} (:userrole logged-in))
    [:div {:id "userelements"}
     [:p [:a {:href "/orders"} "Orders"]]
     [:p [:a {:href "/cart"} "Cart"]]]))

(defn staff-elements [logged-in]
  (when (#{"staff" "manager"} (:userrole logged-in))
    [:div {:id "staffelements"}
     [:p [:a {:href "/staff-inventory"} "Staff inventory"]]]))

(defn manager-elements [logged-in]
  (when (#{"manager"} (:userrole logged-in))
    [:div {:id "managerelements"}
     [:p [:a {:href "/pending-orders"} "Pending Orders"]]
     [:p [:a {:href "/promotion"} "Promotions"]]]))

(defn none-elements [logged-in]
  (when ((complement #{"user" "manager" "staff"}) logged-in)
    [:div {:id "login"}
     [:p "Login"]
     (login-form)]))

(defn nav-bar [logged-in]
  [:nav [:p [:a {:href "/inventory"} "Inventory"]]
   (user-elements logged-in)
   (staff-elements logged-in)
   (manager-elements logged-in)
   (none-elements logged-in)])

(defn home-page [logged-in]
  (html5
   {:lang "en"}
   [:head [:title "Home"]]
   [:body
    (nav-bar logged-in)
    (when-not logged-in
      [:div {:id "unsigned"}
       [:div {:id "login"}
        [:h1 "Login"]
        (login-form)]
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
   [:p {:name :price}    (str "Price: " (:price item))]])

(defn cart-div [items]
  [:div {:id "cart"}
   (if (seq items)
     [:div {:id "nonempty"}
      (map cart-item items)
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

(defn cart-page [r items]
  (html5
   {:lang "en"}
   [:head [:title "Staff page"]]
   [:body
    (nav-bar (:session r))
    (cart-div items)]))

(defn inventory-page [r cart items]
  (html5
   {:lang "en"}
   [:head [:title "Register"]]
   [:body
    (nav-bar (:session r))
    (cart-div cart)
    [:div {:id "inventory"}
     (map item-div items)]]))

(defn order-item-div [item]
  [:p (pr-str item)])

(defn order-div [order]
  (map order-item-div order))

(defn order-page [orders]
  (html5
   {:lang "en"}
   [:head [:title "Orders"]]
   [:body
    [:div {:id "orders"}
     (order-div orders)]]))


(defn manager-page []
  (html5
   {:lang "en"}
   [:head [:title "Manager page"]]
   [:body
    "dunno yet"]))

(defn staff-page []
  (html5
   {:lang "en"}
   [:head [:title "Staff page"]]
   [:body
    "dunno yet"]))

(defn user-page []
  (html5
   {:lang "en"}
   [:head [:title "User page"]]
   [:body
    "dunno yet"]))
