(defproject find-the-invisible-beer "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.170"]
                 [org.clojure/core.async "0.2.374"]
                 [reagent "0.5.1"]]
  :plugins [[lein-cljsbuild "1.1.1"]]
  :cljsbuild 
  {:builds [{:source-paths ["src/cljs"]
             :compiler {:output-to "resources/public/js/fib.js" 
                        :output-dir "resources/public/js"
                        :optimizations :whitespace
                        :pretty-print true
                        ;;:source-map "resources/public/js/fib.js.map"
                        }}]})
