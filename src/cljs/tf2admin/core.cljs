(ns tf2admin.core
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [tf2admin.routes :as routes]
            [tf2admin.views :as views]
            [tf2admin.config :as config]))

(def default-db
  {:name "re-frame"})
(re-frame/register-handler :initialize-db (fn  [_ _] default-db))

(re-frame/register-handler
 :send-command
 (fn [db [_ active-panel]]
   (assoc db :active-panel active-panel)))

(re-frame/register-handler
 :set-active-panel
 (fn [db [_ active-panel]]
   (assoc db :active-panel active-panel)))

;; Subscriptions
(re-frame/register-sub
 :name
 (fn [db]
   (reaction (:name @db))))

(re-frame/register-sub
 :active-panel
 (fn [db _]
   (reaction (:active-panel @db))))


(when config/debug?
  (println "dev mode"))

(defn mount-root []
  (reagent/render [views/main-panel]
    (.getElementById js/document "app")))

(defn ^:export init []
  (routes/app-routes)
  (re-frame/dispatch-sync [:initialize-db])
  (mount-root))
