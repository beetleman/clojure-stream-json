(ns beetleman.clojure-stream-json.core
  (:require [charred.api :as charred]
            [mount.core :as mount]
            [ring.adapter.jetty :as jetty]
            [ring.util.io :as ring-io]
            [ring.util.response :as response]
            [clojure.java.io :as io]))


(defn handler [_request]
  (-> (ring-io/piped-input-stream
       (fn [output]
         (with-open [w (io/writer output)]
           (while true ;; NDJSON
             (.write w
                     (charred/write-json-str {:name (str "Random name " (rand-int 1000))
                                              :id   (str (random-uuid))}))
             (.write w "\r\n"))))) ;; new line delimiter
      response/response
      (response/content-type "application/json")
      (response/header "Transfer-Encoding" "chunked")))

(mount/defstate server
  :start (jetty/run-jetty #'handler
                          {:port  3000
                           :join? false})
  :stop (.stop server))

(comment
  (mount/start)
  )
