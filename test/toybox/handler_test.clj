(ns toybox.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [toybox.handler :refer :all]
            [toybox.query :as q]))

(def testuser {:username "ausername"
               :password "apassword"})

(deftest test-app
  (q/reset-tables!)
  (testing ":get /"
    (let [response (app-routes (mock/request :get "/"))]
      (is (= (:status response) 200))))

  (testing ":get /invalid"
    (let [response (app-routes (mock/request :get "/invalid"))]
      (is (= (:status response) 404))))

  (testing ":get /register"
    (let [response (app-routes (mock/request :get "/register"))]
      (is (= (:status response) 200))))

  (testing ":post /register"
    (let [unique-register-request (mock/request :post "/register" testuser)
          
          unique-register-response (api unique-register-request)

          duplicate-register-request unique-register-request
          duplicate-register-response (api duplicate-register-request)

          empty-username-request (mock/request :post "/register" {:username ""
                                                                  :password "apassword"})
          empty-username-response (api empty-username-request)]
      (is (= 200 (:status unique-register-response)))
      (is (= 400 (:status duplicate-register-response)))
      ;; FIXME: Disallow empty username.
      ;;(is (= 400 (:status empty-username-response)))
      ))

  (testing ":post /login good user"
    (let [login-request (mock/request :post "/login" testuser)
          login-response (api login-request)]
      (= (:status login-response) 200)))

  (testing ":post /login nonexistent user"
    (let [login-request (mock/request :post "/login" {:username "idontexist"
                                                      :password "ipromise"})
          login-response (api login-request)]
      (= (:status login-response) 400)))

  (testing ":post /login wrong password"
    (let [login-request (mock/request :post
                                      "/login"
                                      (update-in testuser
                                                 [:password]
                                                 (fn [s]
                                                   (str s "nonce"))))
          login-response (api login-request)]
      (= (:status login-response) 400)))

  (testing "authenticated :post /"
    (let [login-request (mock/request :post "/login" testuser)
          login-response (api login-request)]
      (= (:status login-response) 200)))
  ;; (testing "authenticated :get /cart"
  ;;   )
  )
