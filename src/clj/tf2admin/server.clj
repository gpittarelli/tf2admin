(ns tf2admin.server
  (:require [org.httpkit.server :refer [run-server]])
  (:gen-class))

(def http-handler
  (cond-> routes
    is-dev? wrap-logging
    true (wrap-transit-params {:opts {}})
    true (wrap-transit-response {:encoding :json, :opts {}})
    true (wrap-defaults api-defaults)
    true ignore-trailing-slash
    is-dev? reload/wrap-reload
    true wrap-browser-caching-opts
    true wrap-gzip))

(run-server #'http-handler {:port port :join? false})
