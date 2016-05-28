(ns tf2admin.views
  (:require [reagent.core :as r]
            [re-frame.core :as re-frame :refer [dispatch]]
            [tf2admin.components.terminal :refer [terminal]]))

;; home

(defn home-panel []
  (let [name (re-frame/subscribe [:name])
        address (r/atom "")
        password (r/atom "")]
    (fn []
      [:div
       (str "Hello from " @name ". This is the Home Page.")
       [:input.address {:type "text"
                        :value @address
                        :on-change #(reset! address (-> % .-target .-value))}]
       [:input.password {:type "password"
                         :value @password
                         :on-change #(reset! password (-> % .-target .-value))}]
       [:button {:on-click #(dispatch [:connect @address @password])}]])))


;; rcon

(defn rcon-panel []
  (let [address (re-frame/subscribe [:address])
        password (re-frame/subscribe [:password])]
    (fn []
      [:div.rcon-page
       (str "Rcon to " @address " with " @password)
       [terminal "asdf" #(dispatch [:send-command %])]
       [:a.quit {:href "#/"} "Go back"]])))

;; main

(defmulti panels identity)
(defmethod panels :home-panel [] [home-panel])
(defmethod panels :rcon-panel [] [rcon-panel])
(defmethod panels :default [] [:div#default "asdf"])

(defn main-panel []
  (let [active-panel (re-frame/subscribe [:active-panel])]
    #(-> [panels @active-panel])))
