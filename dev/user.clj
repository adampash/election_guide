(ns user
  (:require [election-guide.server]
            [ring.middleware.reload :refer [wrap-reload]]
            [figwheel-sidecar.repl-api :as figwheel]))
  ; (:import [java.lang.Runtime]))

(import 'java.lang.Runtime)


(defn start-sass [config]
  (swap! config assoc :sass-watcher-process
    (.exec (Runtime/getRuntime) "sass --watch src/scss:resources/tmp"))
  (swap! config assoc :autoprefixer-process
    (.exec (Runtime/getRuntime) "postcss --use autoprefixer --watch --dir resources/public/css/ resources/tmp/style.css")))

(defn stop-sass [config]
  (println (:sass-watcher-process config))
  (when-let [process (:sass-watcher-process config)]
    (println "Figwheel: Stopping SASS watch process")
    (.destroy process))
  (when-let [process (:autoprefixer-process config)]
    (println "Figwheel: Stopping Autoprefixer watch process")
    (.destroy process)))

;; Let Clojure warn you when it needs to reflect on types, or when it does math
;; on unboxed numbers. In both cases you should add type annotations to prevent
;; degraded performance.
(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
(def http-handler
  (wrap-reload #'election-guide.server/http-handler))


(def config (atom {}))

(defn run []
  (figwheel/start-figwheel!)
  (start-sass config))

(defn stop []
  (figwheel/stop-figwheel!)
  (stop-sass @config))

(def browser-repl figwheel/cljs-repl)
