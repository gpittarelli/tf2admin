(ns tf2admin.websocket
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [cljs.core.async :as async :refer [<! chan put! timeout]]
            [taoensso.sente :as sente :refer [cb-success?]]))

(def all-conns (atom []))

;;(go-loop []
;;  (when (pos? (count @all-conns))
;;    (println @all-conns))
;;  (swap! all-conns #(remove async/closed? %))
;;
;;  (recur))

(defn listen [addr]
  (let [new-listener (chan)
        conn (get
              (swap! all-conns
                     update
                     addr
                     (fn [previous-conn]
                       (if previous-conn
                         previous-conn
                         (sente/make-channel-socket! addr {:type :auto}))))
              addr)]
    (println conn)
    new-listener))
