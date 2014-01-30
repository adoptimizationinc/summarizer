(ns summarizer.boilerpipe
  (:import [uk.ac.shef.dcs.oak.jate.model Document Corpus]
           de.l3s.boilerpipe.extractors.CommonExtractors
           java.net.URL)
  (:require [boilerpipe-clj.core :as bp]
            [boilerpipe-clj.extractors :as extractors]))

(set! *warn-on-reflection* true)
(defn new-corpus ^Corpus [docs]
  (proxy
    [Corpus] []
    (iterator [] (.iterator @docs))
    (add [^Document d]
      (reset! docs (conj @docs d))
      true)
    (remove [^Document d] true)
    (contains [^Document d] true)
    (size [] 1)))

(defrecord BoilerPipeDoc [^String url]
  Document
  (getUrl [this] (URL. (:url this)))
  (getContent [this]
    (let [html-content (slurp (:url this))]
      (bp/get-text html-content))))

(defn init-corpus ^Corpus [url]
  (let [corpus (new-corpus (atom []))]
    (.add corpus (BoilerPipeDoc. url))
    corpus))
