(ns summarizer.web
  (:use compojure.core
        clojure.pprint)
  (:require [compojure.route :as route]
            [ring.util.codec :as codec]
            [noir.util.middleware :as nm]
            [noir.response :as resp]
            [taoensso.carmine :as car :refer (wcar)]
            [summarizer.core :as core]))
(defonce in-process (ref #{}))

(defn redis-key [url] (str url "!!terms"))

(defn stringify-terms [url]
  (if-not (contains? @in-process url)
    (do
      (dosync
        (ref-set in-process (conj @in-process url)))
      (let [terms (map #(.getConcept %)
                       (take 15 (seq (core/extract-terms
                                       (codec/url-decode url)))))]
        (wcar {} (apply car/sadd (redis-key url) terms))
        (dosync
          (ref-set in-process (disj @in-process url)))
        terms))))

(defn get-terms [url]
  (let [terms (wcar {} (car/smembers (redis-key url)))]
    (if (empty? terms)
      (do
        (future (stringify-terms url))
        [])
      terms)))

(defroutes app-routes
  (GET "/" [] (resp/redirect "/index.html"))
  (GET "/summarize/:url" {params :params}
       (resp/json (get-terms (params :url))))
  (route/resources "/")
  (route/not-found "<h1>Page not found</h1>"))

(def app
  (nm/app-handler [app-routes]))
