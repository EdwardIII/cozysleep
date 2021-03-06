(defproject cozysleep "0.1.0-SNAPSHOT"
  :description "Sleep better at night with http monitoring"
  :url "https://edwardiii.co.uk"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [ [org.clojure/clojure "1.10.1"]
                  [clj-http "3.10.3"]
                  [org.clojure/java.jdbc "0.7.11"]
                  [org.xerial/sqlite-jdbc "3.32.3.2"]
                  [clojure.java-time "0.3.2"]
                  ;; TODO: handle prod and dev dependencies
                  [cider/cider-nrepl "0.9.1"]
                  [org.clojure/tools.trace "0.7.10"]]
  :repl-options {:init-ns cozysleep.core}
  :main cozysleep.core
  :aliases {"docs" ["run" "-m" "cozysleep.core/update-readme!"]}
  :profiles {:dev {:plugins [[com.jakemccrary/lein-test-refresh "0.24.1"]]}})
