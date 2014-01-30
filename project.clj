(defproject summarizer "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :plugins [[lein-ring "0.8.10"]]
  :ring {:handler summarizer.web/app}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [log4j/log4j "1.2.17"]
                 [io.curtis/boilerpipe-clj "0.3.0"]
                 [com.taoensso/carmine "2.4.5"]
                 [lib-noir "0.8.0"]
                 [compojure "1.1.6"]
                 [jate/jate "1.11"]])
