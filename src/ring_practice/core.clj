(ns ring-practice.core
  (:require [org.httpkit.server :as kit]
            [ring.middleware.session :refer [wrap-session]]
            [ring.middleware.flash :refer [wrap-flash]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring-practice.handler :refer [handler]]))

(def app
  (-> handler
      wrap-session
      wrap-flash
      wrap-reload))

(defonce server (atom nil))

(defn stop-server! []
  (when-not (nil? @server)
    ;; graceful shutdown: wait 100ms for existing requests to be finished
    ;; :timeout is optional, when no timeout, stop immediately
    (@server :timeout 100)
    (reset! server nil)))

;; The #' is useful when you want to hot-reload code
;; You may want to take a look: https://github.com/clojure/tools.namespace
;; and http://http-kit.org/migration.html#reload
(defn start-server! []
  (println "starting server on port 3000")
  (reset! server (kit/run-server #'app {:port 3000})))

(defn -main [& args]
  (start-server!))
