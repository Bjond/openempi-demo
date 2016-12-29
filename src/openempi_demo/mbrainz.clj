(ns openempi-demo.mbrainz
  (:require [zprint.core :as zp]
            [clojure.data.json :as json]
            [clj-http.client :as http]
            [clojure.data.xml :as xml]
            [clj-time.core :as t]
            [datomic.api :as d]
            [openempi-demo.core :refer :all]))

(def db-uri "datomic:dev://localhost:4334/mbrainz-1968-1973")

(defn count-artists [db]
  (d/q '[:find (count ?e)
         :where [?e :artist/name _][?e :artist/type :artist.type/person]] db))

(defn count-artists-by-gender [db gender]
  (d/q [:find '(count ?e)
        :where ['?e :artist/gender gender]
        '[?e :artist/type :artist.type/person]] db))

(defn first-n-artist-names [db n]
  (take n (d/q '[:find ?n
                 :where [?e :artist/name ?n]
                 [?e :artist/type :artist.type/person]] db)))

(defn count-name-words [db]
  (let [names (d/q '[:find ?n
                     :where [?e :artist/name ?n]
                     [?e :artist/type :artist.type/person]] db)]
    (frequencies (map #(-> %1
                           first
                           (clojure.string/split #" ")
                           count) names))))

(defn filter-name-by-count [db n]
  (let [names (d/q '[:find ?n
                     :where [?e :artist/name ?n]
                     [?e :artist/type :artist.type/person]] db)]
    (filter #(-> %1
                 first
                 (clojure.string/split #" ")
                 count
                 (= n)) names)))

(defn two-named-gendered-with-date [db]
  (let [result (d/q '[:find ?e ?n ?g ?y ?m ?d
                      :where
                      [?e :artist/name ?n]
                      [?e :artist/type :artist.type/person]
                      [?e :artist/gender ?g]
                      [?e :artist/startYear ?y]
                      [?e :artist/startMonth ?m]
                      [?e :artist/startDay ?d]] db)]
    (filter #(-> %1
                 second
                 (clojure.string/split #" ")
                 count
                 (= 2)) result)))

(defn pull-by-name [db name]
  (d/q [:find '(pull ?e [*])
        :where ['?e :artist/name name]] db))

;; 17592186045591 :artist.gender/female
;; 17592186045654 :artist.gender/male

(defn musician-to-person [data]
  (let [[entity-id name gender-id year month day] data
        split-name (clojure.string/split name #" ")
        first-name (first split-name)
        last-name (second split-name)
        gender-code (if (= gender-id 17592186045591) "F" "M")
        date-string (format "%s-%02d-%02d" year month day)]
    #_(println :first-name first-name :last-name last-name :gender gender-code :date date-string)
    {"gender" {"genderCode" gender-code}
     "familyName" last-name
     "givenName" first-name
     "dateOfBirth" date-string
     "personIdentifiers" {"identifier" entity-id
                          "identifierDomain" {"namespaceIdentifier" "Bjond"}}}))

(defn musician-to-person [data domain-namespace]
  (let [[entity-id name gender-id year month day] data
        split-name (clojure.string/split name #" ")
        first-name (first split-name)
        last-name (second split-name)
        gender-code (if (= gender-id 17592186045591) "F" "M")
        date-string (format "%s-%02d-%02d" year month day)]
    #_(println :first-name first-name :last-name last-name :gender gender-code :date date-string)
    {"gender" {"genderCode" gender-code}
     "familyName" last-name
     "givenName" first-name
     "dateOfBirth" date-string
     "personIdentifiers" {"identifier" (str domain-namespace "_" entity-id)
                          "identifierDomain" {"namespaceIdentifier" domain-namespace}}}))



(defn add-musician [skey data domain-namespace]
  (let [p (musician-to-person data domain-namespace)
        json (json/write-str p)]
    (add-person-json skey json)))

(defn add-musicians
  ([skey db domain-namespace]
   (let [them (two-named-gendered-with-date db)]
     (doseq [t them]
       (println :name (second t))
       (add-musician skey t domain-namespace))))
  ([skey db]
   (add-musicians skey db "Bjond")))
  

        
                      
