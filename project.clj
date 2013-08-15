(defproject zk.clj "0.1.0-SNAPSHOT"
  :description "More than just a wrapper of clojure for zookeeper"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :min-lein-version "2.0.0"
  :javac-options ["-source" "1.6" "-target" "1.6" "-g"]
  :java-source-paths ["src/java"]
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.apache.curator/curator-framework "2.2.0-incubating"]
                 [org.apache.curator/curator-recipes "2.2.0-incubating"]])
