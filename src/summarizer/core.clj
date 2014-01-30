(ns summarizer.core
  (:require [summarizer.boilerpipe :as bp])
  (:import [uk.ac.shef.dcs.oak.jate.util.control StopList Lemmatizer]
           [uk.ac.shef.dcs.oak.jate.core.npextractor CandidateTermExtractor NounPhraseExtractorOpenNLP WordExtractor]
           [uk.ac.shef.dcs.oak.jate.core.feature FeatureCorpusTermFrequency FeatureDocumentTermFrequency
                                                 FeatureRefCorpusTermFrequency
                                                 FeatureBuilderCorpusTermFrequencyMultiThread
                                                 FeatureBuilderDocumentTermFrequencyMultiThread
                                                 FeatureBuilderTermNestMultiThread
                                                 FeatureBuilderRefCorpusTermFrequency
                                                 FeatureBuilderCorpusTermFrequencyMultiThread]
           [uk.ac.shef.dcs.oak.jate.core.algorithm TermExFeatureWrapper TermExAlgorithm]
           [uk.ac.shef.dcs.oak.jate.core.feature.indexer GlobalIndexBuilderMem GlobalIndexMem]
           [uk.ac.shef.dcs.oak.jate.model Corpus Term]
           uk.ac.shef.dcs.oak.jate.util.counter.WordCounter))

(set! *warn-on-reflection* true)

(defonce ^StopList stop (StopList. true))
(defonce ^Lemmatizer lemmatizer (Lemmatizer.))
(defonce ^CandidateTermExtractor np-extractor (NounPhraseExtractorOpenNLP. stop lemmatizer))
(defonce ^CandidateTermExtractor word-extractor (WordExtractor. stop lemmatizer false 1))
(defonce ^GlobalIndexBuilderMem builder (GlobalIndexBuilderMem.))

(defn build-word-index ^GlobalIndexMem [path]
  (.build builder (bp/init-corpus path) word-extractor))

(defn build-term-doc-index ^GlobalIndexMem [path]
  (.build builder (bp/init-corpus path) np-extractor))

(defn build-corpus-term-freq ^FeatureCorpusTermFrequency [^WordCounter word-counter ^Lemmatizer lemmatizer ^GlobalIndexMem index]
  (-> (FeatureBuilderCorpusTermFrequencyMultiThread. word-counter lemmatizer)
      (.build index)))

(defn build-document-term-freq ^FeatureDocumentTermFrequency [^WordCounter word-counter ^Lemmatizer lemmatizer ^GlobalIndexMem index]
  (-> (FeatureBuilderDocumentTermFrequencyMultiThread. word-counter lemmatizer)
      (.build index)))

(defn get-bnc-ref ^FeatureRefCorpusTermFrequency [^String normal-path]
  (.build (FeatureBuilderRefCorpusTermFrequency. normal-path) nil))

(defn get-features ^TermExFeatureWrapper [^String path]
  (let [^WordCounter word-counter (WordCounter.)
        word-doc-index (build-word-index path)
        term-doc-index (build-term-doc-index path)
        word-freq (build-corpus-term-freq word-counter lemmatizer word-doc-index)
        term-freq (build-document-term-freq word-counter lemmatizer term-doc-index)
        bnc-ref (get-bnc-ref "nlp_resources/bnc_unifrqs.normal")]
    (TermExFeatureWrapper. term-freq word-freq bnc-ref)))

(defn extract-terms [^String url]
  (let [features (get-features url)]
    (.execute (TermExAlgorithm.) features)))
