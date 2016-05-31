(ns tf2admin.server
  (:require
   [clojure.core.async :as async :refer [<! chan put! timeout go-loop]]
   [org.httpkit.server :refer [run-server]]
   [clj-rcon.core :as rcon]
   [taoensso.sente :as sente]
   [taoensso.sente.server-adapters.http-kit :refer [sente-web-server-adapter]]
   [compojure.core :refer [GET POST defroutes]]
   [compojure.route :refer [resources not-found]]
   [ring.middleware.params :refer [wrap-params]]
   [ring.middleware.keyword-params :refer [wrap-keyword-params]]
   [ring.middleware.reload :as reload]
   [ring.util.response :refer [file-response]])
  (:gen-class))

(let [{:keys [ch-recv send-fn ajax-post-fn ajax-get-or-ws-handshake-fn
              connected-uids]}
      (sente/make-channel-socket! sente-web-server-adapter {})]
  (def ring-ajax-post                ajax-post-fn)
  (def ring-ajax-get-or-ws-handshake ajax-get-or-ws-handshake-fn)
  (def ch-chsk                       ch-recv) ; ChannelSocket's receive channel
  (def chsk-send!                    send-fn) ; ChannelSocket's send API fn
  (def connected-uids                connected-uids) ; Watchable, read-only atom
  )

(def connections (atom {}))

(go-loop []
  (let [{:keys [event id ?data client-id ring-req] :as e} (<! ch-chsk)]
    (println "msg: " id ?data client-id)
    (case id
      :chsk/uidport-open
      (do
        (swap! connections update client-id
               (rcon/connect (:url ?data) (:password ?data)))
        (println "Conn map" @connections))

      :tf2admin.rcon/send-command
      (println "send" ?data "to" (:params ring-req))
;;      (rcon/exec @)

      (println "Unhandled message type" id ?data)))
  (recur))

(defroutes routes
  (GET "/rcon" req (ring-ajax-get-or-ws-handshake req))
  (POST "/rcon" req (ring-ajax-post req))
  (POST "/asdf" [] "asdf")
  (resources "/")
  (GET "/" [] (file-response "index.html" {:root "resources/public"}))
  (not-found "404"))

(def http-handler
  (-> routes
      wrap-keyword-params
      wrap-params))

(defn -main [& [port]]
  (run-server (reload/wrap-reload #'http-handler) {:port 8081 :join? false}))
