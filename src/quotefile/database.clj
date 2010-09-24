(ns quotefile.database
  (:require [clojure.contrib.sql :as sql])
  (:import (java.sql DriverManager)))

(def db {:classname "org.sqlite.JDBC"
         :subprotocol "sqlite" ; Protocol to use
         :subname "db/db.sqlite3" ; Location of db
         :create true})

(. Class (forName "org.sqlite.JDBC")) ; Initialize the JDBC driver

(def quotes [
  "Sally: Google have some good search tools."
  "Sally: I'm a retard. (totally without provocation while walking through a hacky sack game)"
  "Logan: Celine! I've got balls on my site!
   Sally: Are they in the back end?"
  ])

(defn create-database
  "Creates the table for this model"
  []
  (sql/with-connection
   db
   (sql/transaction
    (sql/create-table
     :quotes
     [:id :integer "PRIMARY KEY AUTOINCREMENT"]
     [:quote "text"]))))

(defn seed-database
  "Seeds the database with a few quotes"
  []
  (map (fn [q] (insert-quote q)) quotes))

(defn insert-quote
  "Inserts a quote into the database"
  [quote]
  (sql/with-connection db
    (sql/insert-records "quotes"
      {:quote quote})))

(defn select-quotes
  "Grabs all quotes from the database"
  []
  (sql/with-connection db
    (sql/with-query-results results ["select * from quotes"]
      (doall results))))

