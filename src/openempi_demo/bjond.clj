(ns openempi-demo.bjond
  (:require [zprint.core :as zp]
            [clojure.data.json :as json]
            [clj-http.client :as http]))

(def url-base "http://localhost:8080")

;; /server-core/services/personservice/read/person?personid=

;; /server-core/services/empiservice/add

(def p1 {"gender" {"genderCode" "M"}
         "familyName" "Cooper"
         "middleName" "Fenimore"
         "givenName" "James"
         "dateOfBirth" "2000-01-01"
         "personIdentifiers" {"identifier" "bjond_id_1"
                              "identifierDomain" {"namespaceIdentifier" "Bjond"}}})

(def p1-json (json/write-str p1))

(def key {"familyName" "Cooper"
          "givenName" "James"})

(def key-json (json/write-str key))

(defn add-person [json]
  (let [url (str url-base "/server-core/services/empiservice/add")]
    (http/post url {:body json})))

(defn import-person [json]
  (let [url (str url-base "/server-core/services/empiservice/import")]
    (http/post url {:body json})))

(defn find-person [id]
  (let [url (str url-base "/server-core/services/empiservice/find")]
    (http/get url {:query-params {"id" id
                                  "domainNamespaceID" "Bjond"}})))

(defn find-matches [json]
  (let [url (str url-base "/server-core/services/empiservice/findMatches")]
    (http/post url {:body json})))
