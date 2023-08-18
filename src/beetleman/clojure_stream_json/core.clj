(ns beetleman.clojure-stream-json.core
  (:require [charred.api :as charred]
            [clojure.java.io :as io]
            [mount.core :as mount]
            [ring.adapter.jetty :as jetty]
            [ring.util.io :as ring-io]
            [ring.util.response :as response]))



(mod 100 10)

(defn handler [request]
  (-> (ring-io/piped-input-stream
       (fn [output]
         (println output)
         (charred/write-json output
                             (map (fn [x]
                                    (when (zero? (mod x 100))
                                      (println x))
                                    (Thread/sleep 1)
{:x x})
                                  (range 1000000)))))
      response/response
      (response/content-type "application/json")))

(mount/defstate server
  :start (jetty/run-jetty #'handler
                          {:port  3000
                           :join? false})
  :stop (.stop server))

(comment
  (mount/start)

  )
