(ns election-guide.server
  (:require [clojure.java.io :as io]
            [compojure.core :refer [ANY GET PUT POST DELETE defroutes]]
            [compojure.route :refer [resources]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.gzip :refer [wrap-gzip]]
            [ring.middleware.logger :refer [wrap-with-logger]]
            [environ.core :refer [env]]
            [ring.adapter.jetty :refer [run-jetty]]
            [clojure.string :as string])
  (:gen-class))

(use 'csv-map.core)

(defroutes routes
  (GET "/" _
    {:status 200
     :headers {"Content-Type" "text/html; charset=utf-8"}
     :body (io/input-stream (io/resource "public/index.html"))})
  (resources "/"))

(def http-handler
  (-> routes
      (wrap-defaults api-defaults)
      wrap-with-logger
      wrap-gzip))

(def str-map {"foo" "bar" "Image url" "hillary/foo.jpg"})

(defn key-to-str [k]
  (string/capitalize
    (string/replace
      (string/replace
        (str k) #"^:" "") #"-" " ")))

(defn str-to-key [s]
  (keyword (string/lower-case (string/replace s #" " "-"))))

(defn map-to-keys [x]
  (into {} (map (fn [[k v]] [(str-to-key k) v]) x)))

(defn csv-to-map [file]
  {:candidates
    (into [] (map map-to-keys (into [] (parse-csv (slurp file)))))})

(defn add-categories [x]
  (into [] (map key-to-str)
    (remove #{:image-url :party :candidate} (map (fn [[k _]] k) (first (:candidates x))))))

(defn write-init-data [f]
  (let [init-data (csv-to-map f)
        data (into init-data {:categories (add-categories init-data)})]
    (spit "src/cljs/election_guide/data.cljs" (str "(ns election-guide.data)

      (def init-data
        " data ")"))))

; (write-init-data "giz-guide.csv")

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 10555))]
    (run-jetty http-handler {:port port :join? false})))


(def init-data
  {:candidates [{:image-url "images/hillary.jpg", :envinronment "Loves it", :party "Democrat", :space "Loves it", :funny-tech-moment "Loves it", :surveillance "Loves it", :candidate "Hillary Clinton", :cyber-warfare "Loves it", :net-neutrality "Loves it", :biomedical-research "Loves it"} {:image-url "", :envinronment "Hates it", :party "Democrat", :space "Hates it", :funny-tech-moment "Hates it", :surveillance "Hates it", :candidate "Bernie Sanders", :cyber-warfare "Hates it", :net-neutrality "Hates it", :biomedical-research "Hates it"} {:image-url "", :envinronment "Doesn't know, doesn't care", :party "Republican", :space "Doesn't know, doesn't care", :funny-tech-moment "Doesn't know, doesn't care", :surveillance "Doesn't know, doesn't care", :candidate "Donald Trump", :cyber-warfare "Doesn't know, doesn't care", :net-neutrality "Doesn't know, doesn't care", :biomedical-research "Doesn't know, doesn't care"} {:image-url "", :envinronment "Is flexible", :party "Republican", :space "Is flexible", :funny-tech-moment "Is flexible", :surveillance "Is flexible", :candidate "Ted Cruz", :cyber-warfare "Is flexible", :net-neutrality "Is flexible", :biomedical-research "Is flexible"}],
   :categories ["Envinronment" "Space" "Funny tech moment" "Surveillance" "Cyber warfare" "Net neutrality" "Biomedical research"]})

(:categories init-data)
