(ns quotefile.core
  (:use compojure.core
        ring.adapter.jetty
        hiccup.core
        [clojure.contrib.sql :as sql])
  (:require [compojure.route :as route])
  (:import (java.sql DriverManager)))

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
     :something
     [:id :int "PRIMARY KEY"]
     [:name "varchar(32)"]))))


;; Actual app
(def quotes [
  "Sally: Google have some good search tools."
  "Sally: I'm a retard. (totally without provocation while walking through a hacky sack game)"
  "Logan: Celine! I've got balls on my site!
   Sally: Are they in the back end?"
  ])

(defn list-quotes []
  "Display the page that lists quotes"
  (map (fn [quote] [:li quote]) quotes))

(defn html-outline [title body]
    (html
      [:html
        [:head
          [:title title]]
        [:body body]]))

(defroutes example
  (GET "/" [] (html-outline "Quotefile" (list-quotes)))
  (GET "/:id" [id] (html-outline (str "Quote " id) (quotes (int id))))
  (route/not-found "Page not found"))

(future (run-jetty example {:port 8080}))


