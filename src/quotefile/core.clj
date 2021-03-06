;; Setup
(ns quotefile.core
  (:use compojure.core
        ring.adapter.jetty
        ring.util.response
        ring.middleware.reload
        ring.middleware.stacktrace
        hiccup.core
        hiccup.form-helpers
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
    (html [:h1 "Quotefile"]
          [:a {:href "/new"} "Add a quote"]
          (ordered-list
            (map (fn [quote]
              (html [:a {:href (str "/" (:id quote))}
                      (format-quote (:quote quote))]
                    [:p
                     [:a {:href (str "/" (:id quote) "/delete")} "Delete"]]))
            (qdb/select-quotes))))))

(defn show-quote [id]
  (html-template "Quote"
     (html
       [:h1 "Quote " id]
       [:a {:href "/"} "All quotes"]
       (format-quote (:quote (qdb/select-quote id))))))

(defn new-quote []
  (html-template "New"
    (html [:h1 "New quote"]
          [:a {:href "/"} "All quotes"]
          (form-to [:post "/"]
            (text-area "quote")
            (submit-button "Submit")))))

;; Routes
(defroutes handler
  (GET "/" [] (list-quotes))
  (GET ["/:id", :id #"[0-9]+"] [id] (show-quote id))
  (GET ["/:id/delete", :id #"[0-9]+"] [id]
       (do (qdb/delete-quote id)
           (redirect "/")))
  (GET "/new" [] (new-quote))
  (POST "/" {{quote "quote"} :params}
        (do (qdb/insert-quote quote)
            (redirect "/")))
  (route/not-found "Page not found"))

;; Middleware and server
(def app
  (-> #'handler
    (wrap-reload '[quotefile.core quotefile.database])
    (wrap-stacktrace)))

(future (run-jetty app {:port 8080}))

