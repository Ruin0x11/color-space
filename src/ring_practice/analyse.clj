(ns ring-practice.analyse
  (:require [ring-practice.kmeans :refer [k-means]]
            [mikera.image.core :as img]
            [incanter.core :as incanter]
            [clojure.core.matrix.dataset :as dataset]))

(def ^:dynamic image (img/load-image (str (System/getenv "HOME") "/build/clj/ring-practice/test.jpg")))

(defn submatrix [matrix indices]
  (incanter/sel matrix :rows indices))

(defn cluster-colors [k matrix]
  (k-means matrix k))

(defn image-matrix [image]
  (->> image
       img/get-pixels
       (map col/values-rgb)
       dataset/dataset
       incanter/to-matrix
       ))

(defn avg-color [pixel-mat]
  (map stats/mean (incanter/trans pixel-mat)))

(defn member-indices [matrix clusters]
  (let [indices (:member-indices clusters)]
    (->> indices
         (map-indexed (fn [index item] {item [index]}) )
         (apply merge-with into)
         vals)))

(defn colors [image k]
  (let [matrix (image-matrix image)]
    (->> matrix
         (cluster-colors k)
         (member-indices image)
         (map (partial submatrix matrix))
         (map avg-color)
         (map (partial map (comp int #(* 255 %)))))))

(defn try-colors
  "Wrapper to prevent exceptions caused by k-means"
  [image k]
  (try (colors image k)
       (catch Exception e (repeat k [0 0 0]))))

(defn colors-map [image k]
  {:colors (map (partial zipmap [:r :g :b]) (try-colors image k))}) 
