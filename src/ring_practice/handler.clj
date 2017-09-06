(ns ring-practice.handler
  (:require [bidi.ring :refer [make-handler]]
            [ring.util.response :as res]
            [hiccup.core :refer [html]]
            [cheshire.core :as json]
            [mikera.image.core :as img]
            [ring-practice.booru :as booru]
            [ring-practice.analyse :as analyse]))

(def index
  (html
   [:html
    [:head]
    [:body
     [:div#app "App will go here."]]]))

(defn index-handler
  [request]
  (res/response index))

(defn article-handler
  [{:keys [route-params]}]
  (-> (json/generate-string {:id (:id route-params) :text "hoge"})
      res/response
      (res/content-type "application/json")))

(defn color-handler
  [request]
  (-> booru/posts
      first
      :preview_url
      booru/download-image
      (img/resize 10 10)
      (analyse/colors-map 4)
      json/generate-string
      res/response
      (res/content-type "application/json")))

(defn not-found [request]
  (res/response
   (html
    [:html
     [:body
      [:h3 "Page not found."]]])))

(def routes
  ["/" {"index" index-handler
        ["articles/" :id] article-handler
        "color" color-handler
        true not-found}])

(def handler
  (make-handler routes))
