(defproject toybox "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.7.0-alpha6"]
                 [instaparse "1.3.6"]
                 [compojure "1.3.3"]
                 [buddy "0.5.1"]
                 [hiccup "1.0.5"]
                 [yesql "0.4.0"]
                 [org.postgresql/postgresql "9.3-1102-jdbc41"]
                 [ring/ring-defaults "0.1.4"]]
  :plugins [[lein-ring "0.9.3"]]
  :ring {:handler toybox.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.2.0"]]}})
