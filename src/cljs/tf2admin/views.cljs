(ns tf2admin.views
  (:require [re-frame.core :as re-frame]
            [tf2admin.components.terminal :refer [terminal]]))
;; home

(defn home-panel []
  (let [name (re-frame/subscribe [:name])]
    (fn []
      [:div (str "Hello from " @name ". This is the Home Page.")
       [:div [:a {:href "#/rcon"} "go to Rcon Terminal"]]])))


;; rcon

(defn rcon-panel []
  [:div.rcon-page
   "Rcon!"
   [terminal "asdf" #(re-frame/dispatch [:send-command %])]
   [:a.quit {:href "#/"} "Go back"]])

;; main

(defmulti panels identity)
(defmethod panels :home-panel [] [home-panel])
(defmethod panels :rcon-panel [] [rcon-panel])
(defmethod panels :default [] [:div#default "asdf"])

(defn main-panel []
  (let [active-panel (re-frame/subscribe [:active-panel])]
    (fn []
      [panels @active-panel])))
