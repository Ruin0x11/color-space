(ns ring-practice.booru
  (:require [org.httpkit.client :as http]
            [cheshire.core :as json]
            [clojure.string :as s]
            [mikera.image.core :as img]
            [clojure.java.io :as io])
  (:import [javax.imageio ImageIO]))

(def booru-url "https://yande.re/post.json")

(defn tag-string [tags]
  (s/join "+" tags))

(defn booru-query [tags page]
  {:query-params {:tags (tag-string tags)
                  :page page}})

(defn get-posts [tags]
  (-> @(http/get booru-url (booru-query tags 0))
      :body
      (json/parse-string true)))

(defn download-image [url]
  (-> url
      io/as-url
      ImageIO/read))

(def posts (get-posts ["kaname_madoka"]))
