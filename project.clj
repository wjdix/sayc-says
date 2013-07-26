(defproject sayc-app "0.1.0-SNAPSHOT"
  :description "An App To Help Learn SAYC"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/core.logic "0.8.3"]
                 [org.clojure/data.json "0.2.2"]
                 [ring/ring-jetty-adapter "1.1.0"]
                 [metis "0.3.3"]
                 [ring/ring-devel "1.1.0"]
                 [hiccup "1.0.3"]
                 [compojure "1.1.5"]]
  :min-lein-version "2.0.0"
  :profiles {:dev {:source-paths ["dev"]
                   :dependencies [[org.clojure/tools.namespace "0.2.3"]
                                  [org.clojure/java.classpath "0.2.0"]]}})
