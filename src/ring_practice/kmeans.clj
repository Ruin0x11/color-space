(ns ring-practice.kmeans
  (:require [incanter.core :refer :all]
            [incanter.stats :as stats]))

(defn index-of [coll value]
"
  Examples:
    (use '(incanter core stats))
    (def data [2 4 6 7 5 3 1])
    (index-of data (apply max data))
    (def data (diag (repeat 10 1)))
    (map #(index-of % (apply max %)) data)
"
  (loop [i 0]
    (if (= value (nth coll i))
      i
      (recur (inc i)))))


(defn indices-of [coll value]
"
  Examples:
    (use '(incanter core stats))
    (def data [2 7 4 6 7 5 3 1])
    (indices-of data (apply max data))
    (def data (diag (repeat 10 1)))
    (map #(indices-of % (apply max %)) data)
"
  (for [i (range (count coll)) :when (= value (nth coll i))] i))

(defn k-means
"
   Examples:
     (use 'incanter.datasets)
     (def iris (sel (to-matrix (get-dataset :iris)) :cols (range 4)))
     (def clusters (k-means iris 3))
     ;(:member-indices clusters)
     (partition 50 (:member-indices clusters))
     (:iterations clusters)
     ;; calculate average distance of all the observations are from
     ;; its cluster's centroid
     (:mean-sq-dist clusters)

     (def mahalanobis-clusters (k-means iris 3 :mahalanobis true))
     ;(:member-indices mahalanobis-clusters)
     (partition 50 (:member-indices mahalanobis-clusters))
     (:iterations mahalanobis-clusters)
"
  ([data k & options]
    (let [opts (when options (apply assoc {} options))
          mahalanobis (:mahalanobis opts)
          p (ncol data)
          W (diag (repeat p 1))
          euclid-dist (fn [a b]
                        (stats/mahalanobis-distance
                          a
                          :centroid (trans b)
                          :W W))
          n (nrow data)
          mean-sq-dist (fn [centroids membership]
                         (stats/mean (map (fn [obs clust-idx]
                                      (sum
                                        (sq
                                          (minus obs
                                                 (sel centroids
                                                      :rows clust-idx)))))
                                      data
                                      membership)))]
      (loop [centroids (stats/sample data :size k)
             dist-mat (trans (map #(euclid-dist data %) centroids)) ;; euclidean to init
             last-members nil
             member-indices nil
             i 0]
        (let [last-members member-indices
              member-indices (map #(index-of % (apply min %)) dist-mat)
              cluster-indices (map (fn [idx]
                                     (indices-of member-indices idx))
                                   (range k))
              centroids (matrix (map #(map stats/mean (trans (sel data :rows %)) )
                             cluster-indices))
              dist-mat (if mahalanobis
                         (trans
                           (map (fn [row] (stats/mahalanobis-distance data :y row))
                                (map #(matrix (sel data :rows %))
                                     cluster-indices)))
                         ;; else euclidean
                         (trans (map #(euclid-dist data %) centroids)))
              ]
          (if (= member-indices last-members)
          ;(if true
            { :dist-matrix dist-mat
              :cluster-indices cluster-indices
              :centroids centroids
              :member-indices member-indices
              :iterations i
              :mean-sq-dist (mean-sq-dist centroids member-indices)}
            (recur centroids dist-mat last-members member-indices (inc i))))))))
