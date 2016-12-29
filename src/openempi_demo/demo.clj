(ns openempi-demo.demo
  (:require [zprint.core :as zp]
            [clojure.data.json :as json]
            [clj-http.client :as http]
            [clojure.data.xml :as xml]
            [clj-time.core :as t]
            [openempi-demo.core :refer :all]))

(def p1 {"gender" {"genderCode" "M"}
         "familyName" "Cooper"
         "middleName" "Fenimore"
         "givenName" "James"
         "dateOfBirth" "2000-01-01"
         "personIdentifiers" {"identifier" "bjond_id_1"
                              "identifierDomain" {"namespaceIdentifier" "Bjond"}}})

(def p1-json (json/write-str p1))

(defn updated-person [person-map new-given-name]
  (-> person-map
      (assoc "givenName" new-given-name)
      (assoc "dateChanged" (.toString (t/now)))))

(def p2
  (xml/sexp-as-element
   ["person" {}
    ["gender" {} ["genderCode" {} "F"]]
    ["familyName" {} "Doe"]
    ["givenName" {} "Jane"]
    ["dateOfBirth" {} "2002-02-02"]]))

#_(def p3 "<?xml version=\"1.0\" encoding=\"UTF-8\"?><person>
  <dateChanged>2016-12-19T22:23:00.000Z</dateChanged>
  <dateCreated>2016-12-19T22:21:27.237Z</dateCreated>
  <dateOfBirth>2000-01-10T00:00:00Z</dateOfBirth>
  <familyName>Doe</familyName>
  <gender>
    <genderCd>1</genderCd>
    <genderCode>F</genderCode>
    <genderDescription>Female</genderDescription>
    <genderName>Female</genderName>
  </gender>
  <givenName>Johnny</givenName>
  <personIdentifiers>
    <dateCreated>2016-12-19T22:21:27.247Z</dateCreated>
    <identifier>7bb7fdf0-c639-11e6-ac4f-0242ac120003</identifier>
    <identifierDomain>
      <identifierDomainId>18</identifierDomainId>
      <identifierDomainName>OpenEMPI</identifierDomainName>
      <namespaceIdentifier>2.16.840.1.113883.4.357</namespaceIdentifier>
      <universalIdentifier>2.16.840.1.113883.4.357</universalIdentifier>
      <universalIdentifierTypeCode>hl7</universalIdentifierTypeCode>
    </identifierDomain>
  </personIdentifiers>
</person>")

(def key1 (xml/sexp-as-element ["person" {}
                                ["gender" {} ["genderCode" {} "M"]]
                                ["familyName" {} "Cooper"]
                                ["givenName" {} "James"]]))

(def key1-xml (xml/emit-str key1))
  
(defn demo-add-and-find [session-key]
  (try
    (let [response (add-person-json session-key p1-json)]
      (println "Added person:")
      (zp-body response))
    (println)
    (let [id (get-in p1 ["personIdentifiers" "identifier"])
          response (find-person-by-id session-key id)]
      (println "Found person:")
      (zp-body response))
    (println)
    (finally
      (let [response (delete-person session-key p1-json)]
        (println "Deleted person")
        (println :status (:status response))))))



    
  
  
