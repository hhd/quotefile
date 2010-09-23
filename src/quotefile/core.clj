(ns quotefile.core
  (:use compojure.core
        ring.adapter.jetty
        hiccup.core)
  (:require [compojure.route :as route]))

(def quotes [
  "Sally: Google have some good search tools."
  "Sally: I'm a retard. (totally without provocation while walking through a hacky sack game)"
  "Logan: Celine! I've got balls on my site!
   Sally: Are they in the back end?"
  ])

(defn list-quotes []
  "Display the page that lists quotes"
  (apply str (map (fn [quote] (html [:li quote])) quotes)))

(defn html-outline [title body]
    (html
      [:html
        [:head
          [:title title]]
        [:body body]]))

(defroutes example
  (GET "/" [] (html-outline "Quotefile" (list-quotes)))
  (GET "/:id" [id] (html [:h1 "Quote " id]))
  (route/not-found "Page not found"))

(run-jetty example {:port 8080})


