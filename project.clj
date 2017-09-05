(defproject ring-practice "1.0.0-SNAPSHOT"
  :description "FIXME: write"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [ring/ring-core "1.6.2"]
                 [ring/ring-devel "1.6.2"]
                 [http-kit "2.3.0-alpha2"]
                 [bidi "2.1.2"]
                 [hiccup "1.0.5"]
                 [cheshire "5.8.0"]]

  :main ring-practice.core

  :repl-options {:init (start-server!)})
