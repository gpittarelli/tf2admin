(ns tf2admin.components.terminal
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [cljs.core.async :as async :refer [<! chan put! timeout]]
            [reagent.core :as r]
            [garden.core :refer [css]]))

(def initial-state "")

(defmulti autocomplete (fn [_ e] (first e)))
(defmethod autocomplete :update [_ [_ e]] e)
(defmethod autocomplete :send [state [_ f]] (if (fn? f) (f state)) initial-state)

(defn terminal [scrollback on-command]
  (let [cmd-chan (chan)
        command (r/atom "")]

    (go-loop [state initial-state]
      (->> (<! cmd-chan)
           (autocomplete 1)
           (reset! command)
           recur))

    (fn [scrollback on-command]
      [:div.terminal
       [:div.scrollback scrollback]
       [:input {:type "text"
                :value @command
                :on-change #(put! cmd-chan [:update (-> % .-target .-value)])}]
       [:button {:on-click #(put! cmd-chan [:send on-command])} "Send"]])))
