(defproject quotefile "1.0.0-SNAPSHOT"
  :description "A Compojure 'Hello World' application"
  :dependencies [[org.clojure/clojure "1.1.0"]
                 [org.clojure/clojure-contrib "1.1.0"]
                 [compojure "0.4.0"]
                 [hiccup "0.2.6"]
                 [sqlitejdbc "0.5.6"]
                 [ring/ring-core "0.2.5"]
                 [ring/ring-devel "0.2.5"]
                 [ring/ring-jetty-adapter "0.2.5"]]
  :main quotefile.core)
