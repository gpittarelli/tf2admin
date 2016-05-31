(ns tf2admin.views
  (:require [reagent.core :as r]
            [re-frame.core :as re-frame :refer [dispatch]]
            [tf2admin.components.terminal :refer [terminal]]
            [tf2admin.websocket :as ws]))

;; home

(defn home-panel []
  (let [name (re-frame/subscribe [:name])
        address (r/atom "")
        password (r/atom "")]
    (fn []
      [:div
       (str "Hello from " @name ". This is the Home Page.")
       [:label {:for "address"} "Address"]
       [:input.address {:type "text"
                        :name "address"
                        :value @address
                        :on-change #(reset! address (-> % .-target .-value))}]
       [:label {:for "password"} "Password"]
       [:input.password {:type "password"
                         :name "password"
                         :value @password
                         :on-change #(reset! password (-> % .-target .-value))}]
       [:button {:on-click #(dispatch [:connect @address @password])}]])))


;; rcon

(defn rcon-panel []
  (let [address (re-frame/subscribe [:address])
        password (re-frame/subscribe [:password])
        addr (str "/rcon?" @address)
        conn  (ws/listen addr {:host "localhost:8081"
                               :params {:url @address :password @password}})
        ]
    (fn []
      [:div.rcon-page
       (str "Rcon to " @address " with " @password)
       [terminal "asdf" #(ws/send addr [:tf2admin.rcon/send-command %])]
       [:a.quit {:href "#/"} "Go back"]])))

;; main

(defmulti panels identity)
(defmethod panels :home-panel [] [home-panel])
(defmethod panels :rcon-panel [] [rcon-panel])
(defmethod panels :default [] [:div#default "404"])

(defn main-panel []
  (let [active-panel (re-frame/subscribe [:active-panel])]
    (fn [] [panels @active-panel])))
