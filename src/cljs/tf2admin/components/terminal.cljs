(ns tf2admin.components.terminal
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [cljs.core.async :as async :refer [<! chan put! timeout]]
            [garden.core :refer [css]]
            [re-frame.core :refer [dispatch]]))

(def initial-state "")

(defmulti autocomplete (fn [_ e] (first e)))
(defmethod autocomplete :update [_ [_ e]] e)
(defmethod autocomplete :send [state [_ f]] (if (fn? f) (f state)) initial-state)

(defn terminal [scrollback on-command]
  (let [cmd-chan (chan)]

    (go-loop [state initial-state]
      (->> (<! cmd-chan)
           (autocomplete val)
           recur))

    (fn [scrollback on-command]
      [:div.terminal
       [:div.scrollback scrollback]
       [:input {:type "text"
                :on-change #(put! cmd-chan [:update (-> % .-target .-value)])}]
       [:button {:on-click #(put! cmd-chan [:send on-command])} "Send"]])))
