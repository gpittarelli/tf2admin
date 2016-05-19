(ns tf2admin.core
    (:require [reagent.core :as reagent]
              [re-frame.core :as re-frame]
              [tf2admin.handlers]
              [tf2admin.subs]
              [tf2admin.routes :as routes]
              [tf2admin.views :as views]
              [tf2admin.config :as config]))

(when config/debug?
  (println "dev mode"))

(defn mount-root []
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init [] 
  (routes/app-routes)
  (re-frame/dispatch-sync [:initialize-db])
  (mount-root))
