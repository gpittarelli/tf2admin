(ns tf2admin.server
  (:require [org.httpkit.server :refer [run-server]]
            [compojure.core :refer [GET defroutes]]
            [compojure.route :refer [resources not-found]]
            [ring.util.response :refer [file-response]])
  (:gen-class))

(defroutes routes
  (resources "/")
  (GET "/" [] (file-response "index.html" {:root "resources/public"}))
  (not-found "404"))

(def http-handler
  (cond-> routes))

(defn -main [& [port]]
  (run-server #'http-handler {:port 8081 :join? false}))
