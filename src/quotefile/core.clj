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
      (doctype :html4)
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
      (map (fn [quote]
             [:a {:href (str "/" (:id quote))}
               (format-quote (:quote quote))])
           (qdb/select-quotes)))))

(defn show-quote [id]
  (html-template "Quote"
     (html
       [:h1 "Quote " id]
       (format-quote (:quote (qdb/select-quote id))))))

;; Routes
(defroutes handler
  (GET "/" [] (list-quotes))
  (GET ["/:id", :id #"[0-9]+"] [id] (show-quote id))
  (route/not-found "Page not found"))

;; Middleware and server
(def app
  (-> #'handler
    (wrap-reload '[quotefile.core quotefile.database])
    (wrap-stacktrace)))

(future (run-jetty app {:port 8080}))

