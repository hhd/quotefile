;; Setup
(ns quotefile.core
  (:use compojure.core
        ring.adapter.jetty
        ring.middleware.reload
        ring.middleware.stacktrace
        hiccup.core
        hiccup.page-helpers)
  (:require [compojure.route :as route]
            [clojure.contrib.str-utils2 :as str]
            [quotefile.database :as qdb]))

;; Helpers
(defn html-template [title body]
    (html
      [:html
        [:head
          [:title title]]
        [:body body]]))

(defn format-quote [quote]
  (map (fn [l] [:p l]) (str/split-lines quote)))

;; Pages
(defn list-quotes []
  (html-template "Quotefile"
    (ordered-list
      (map (fn [quote] (format-quote (:quote quote)))
           (qdb/select-quotes)))))

;; Routes
(defroutes handler
  (GET "/" [] (list-quotes))
  (route/not-found "Page not found"))

;; Middleware and server
(def app
  (-> #'handler
    (wrap-reload '[quotefile.core])
    (wrap-stacktrace)))

(future (run-jetty app {:port 8080}))

