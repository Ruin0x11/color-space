(ns ring-practice.handler
  (:require [bidi.ring :refer (make-handler)]
            [ring.util.response :as res]))

(defn index-handler
  [request]
  (res/response "Homepge"))

(defn article-handler
  [{:keys [route-params]}]
  (res/response (str "You are viewing article: " (:id route-params))))

(def handler
  (make-handler ["/" {"index" index-handler
                      ["articles/" :id] article-handler}]))
