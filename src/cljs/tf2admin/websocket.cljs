(ns tf2admin.websocket
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [cljs.core.async :as async :refer [<! chan put! timeout mix mult tap]]
            [taoensso.sente :as sente :refer [cb-success?]]))

(def all-conns (atom {}))

;;(go-loop []
;;  (when (pos? (count @all-conns))
;;    (println @all-conns))
;;  (swap! all-conns #(remove async/closed? %))
;;
;;  (recur))

(defn- get-or-make-connection! [path opts]
  (-> all-conns
      (swap! update path
             (fn [previous-conn]
               (if previous-conn
                 previous-conn
                 (update (sente/make-channel-socket! path opts)
                         :ch-recv
                         mult))))
      (get path)))

(defn listen
  ([path] (listen path {}))
  ([path opts]
   (let [new-listener (chan)
         conn (get-or-make-connection! path (assoc opts :type :auto))]
     (println "conn to " path opts)
     (tap (:ch-recv conn) new-listener)
     new-listener)))

(defn send [path m]
  (println "send" m "to" path)
  ((:send-fn (get @all-conns path)) m))
