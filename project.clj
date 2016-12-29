(defproject openempi-demo "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :repositories [["bjond-maven-private" { :url "http://nexus.bjondhealth.com/nexus/content/groups/private"}]
                 ["bjond-maven-public" { :url "http://nexus.bjondhealth.com/nexus/content/groups/public"}]]
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/data.json "0.2.6"]
                 [org.apache.httpcomponents/httpclient "4.3.5"]
                 [clj-http "2.3.0"]
                 [clj-time "0.12.2"]
                 [org.clojure/data.xml "0.0.8"]
                 [zprint "0.2.9"]
                 [com.datomic/datomic-pro "0.9.5390" :exclusions [joda-time]]]
  :main ^:skip-aot openempi-demo.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
