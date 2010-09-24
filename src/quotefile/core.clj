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

(defn format-quote [quote]
  (map (fn [l] [:p l]) (str/split-lines quote)))

(defn list-quotes []
  "Display the page that lists quotes"
  (ordered-list
    (map (fn [quote] (format-quote (:quote quote)))
         (qdb/select-quotes))))

(defn html-outline [title body]
    (html
      [:html
        [:head
          [:title title]]
        [:body body]]))

(defroutes handler
  (GET "/" [] (html-outline "Quotefile" (list-quotes)))
  (route/not-found "Page not found"))

(def app
  (-> #'handler
    (wrap-reload '[quotefile.core])
    (wrap-stacktrace)))

(future (run-jetty app {:port 8080}))


