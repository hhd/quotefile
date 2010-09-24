;; Setup
(ns quotefile.core
  (:use compojure.core
        ring.adapter.jetty
        hiccup.core)
  (:require [compojure.route :as route]
            [clojure.contrib.sql :as sql])
  (:import (java.sql DriverManager)))

(def quotes [
  "Sally: Google have some good search tools."
  "Sally: I'm a retard. (totally without provocation while walking through a hacky sack game)"
  "Logan: Celine! I've got balls on my site!
   Sally: Are they in the back end?"
  ])

;; Database stuff
(def db {:classname "org.sqlite.JDBC"
         :subprotocol "sqlite" ; Protocol to use
         :subname "db/db.sqlite3" ; Location of db
         :create true})

(. Class (forName "org.sqlite.JDBC")) ; Initialize the JDBC driver

(defn db-create
  "Creates the table for this model"
  []
  (sql/with-connection
   db
   (sql/transaction
    (sql/create-table
     :quotes
     [:id :integer "PRIMARY KEY AUTOINCREMENT"]
     [:quote "text"]))))

(defn insert-quote
  "Inserts a quote into the database"
  [quote]
  (sql/with-connection db
    (sql/insert-records "quotes"
      {:quote quote})))
  

(defn db-seed
  "Seeds the database with a few quotes"
  []
  (map (fn [q] (insert-quote q)) quotes))

;; Actual app
(defn list-quotes []
  "Display the page that lists quotes"
  (map (fn [quote] [:li quote]) quotes))

(defn html-outline [title body]
    (html
      [:html
        [:head
          [:title title]]
        [:body body]]))

(defroutes main
  (GET "/" [] (html-outline "Quotefile" (list-quotes)))
  (GET "/:id" [id] (html-outline (str "Quote " id) (quotes (int id))))
  (route/not-found "Page not found"))

(future (run-jetty main {:port 8080}))


