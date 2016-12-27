(ns openempi-demo.core
  (:gen-class)
  (:require [zprint.core :as zp]
            [clojure.data.json :as json]
            [clj-http.client :as http]
            [clojure.data.xml :as xml]))

;; --- some utilities

;; Courtesy of : https://nakkaya.com/2010/03/27/pretty-printing-xml-with-clojure/
(defn ppxml
  "Returns a pretty printed xml (string)"
  [xml]
  (let [in (javax.xml.transform.stream.StreamSource.
            (java.io.StringReader. xml))
        writer (java.io.StringWriter.)
        out (javax.xml.transform.stream.StreamResult. writer)
        transformer (.newTransformer 
                     (javax.xml.transform.TransformerFactory/newInstance))]
    (.setOutputProperty transformer 
                        javax.xml.transform.OutputKeys/INDENT "yes")
    (.setOutputProperty transformer 
                        "{http://xml.apache.org/xslt}indent-amount" "2")
    (.setOutputProperty transformer 
                        javax.xml.transform.OutputKeys/METHOD "xml")
    (.transform transformer in out)
    (-> out .getWriter .toString)))

(defn from-json
  "Translates json (string) to Clojure object (edn)"
  [json]
  (json/read-str json :key-fn keyword))

(defn get-body
  "Returns the body of a http response as a clojure data structure"
  [response]
  (-> response :body from-json))

(defn zp-body
  "Pretty prints the body of a http response"
  [response]
  (-> response get-body zp/zprint))

;; --- authentication/session-key

(defn auth-json [username pword]
  (let [url "http://localhost:8090/openempi-admin/openempi-ws-rest/security-resource/authenticate"
        json (json/write-str {"username" username
                              "password" pword})]
    #_(println :json json)
    (http/put url {:headers {"Content-type" "application/json"}
                   :body json})))

(defn auth-xml [username pword]
  (let [url "http://localhost:8090/openempi-admin/openempi-ws-rest/security-resource/authenticate"
        body (format "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>
<authenticationRequest><username>%s</username><password>%s</password></authenticationRequest>"
                     username
                     pword)]
    (http/put url {:headers {"Content-type" "application/xml"}
                   :body body})))

;; --- person management

(defn add-person-json [session-key json]
  (let [url "http://localhost:8090/openempi-admin/openempi-ws-rest/person-manager-resource/addPerson"]
    (http/put url {:headers {"OPENEMPI_SESSION_KEY" session-key
                             "Content-type" "application/json"}
                   :accept :json
                   :body json})))

(defn add-person-xml [session-key xml]
  (let [url "http://localhost:8090/openempi-admin/openempi-ws-rest/person-manager-resource/addPerson"]
    (http/put url {:headers {"OPENEMPI_SESSION_KEY" session-key
                             "Content-type" "application/xml"}
                   :accept :xml
                   :body xml})))

;; JSON data should be a "person" as
;; defined in openempi.
;; Some important required fields:
;; 1. dateChanged (must be later than the original dataChanged)
;; 2. personId
;; Returns 204 (No Content) when update is successful.
(defn update-person-json [session-key json]
  (let [url "http://localhost:8090/openempi-admin/openempi-ws-rest/person-manager-resource/updatePerson"]
    (http/put url {:headers {"OPENEMPI_SESSION_KEY" session-key
                             "Content-type" "application/json"}
                   :accept :json
                   :body json})))

(defn update-person-xml [session-key xml]
  (let [url "http://localhost:8090/openempi-admin/openempi-ws-rest/person-manager-resource/updatePerson"]
    (http/put url {:headers {"OPENEMPI_SESSION_KEY" session-key
                             "Content-type" "application/xml"}
                   :accept :json
                   :body xml})))

;; returns 204 (No Content) ... no matter what... so far.
(defn delete-person [session-key json]
  (let [url "http://localhost:8090/openempi-admin/openempi-ws-rest/person-manager-resource/deletePerson"]
    (http/put url {:headers {"OPENEMPI_SESSION_KEY" session-key
                             "Content-type" "application/json"}
                   :accept :json
                   :body json})))

;;; --- person query

(defn find-person-by-id [session-key id]
  (let [url "http://localhost:8090/openempi-admin/openempi-ws-rest/person-query-resource/findPersonById"
        data (xml/sexp-as-element ["personIdentifier" {}
                                   ["identifier" {} id]
                                   ["identifierDomain" {}
                                    #_["namespaceIdentifier" {} "Bjond"]
                                    ["universalIdentifier" "2.16.840.1.113883.3.6292.123.7865"]]])
        str (xml/emit-str data)]
    (http/post url {:headers {"OPENEMPI_SESSION_KEY" session-key
                              "Content-type" "application/xml"}
                    :accept :json
                    :body str})
    ))

(defn find-match [session-key xml]
  (let [url "http://localhost:8090/openempi-admin/openempi-ws-rest/person-query-resource/findMatchingPersonsByAttributes"]
    (http/post url {:headers {"OPENEMPI_SESSION_KEY" session-key
                              "Content-type" "application/xml"}
                    :accept :json
                    :body xml})))

;; --- some entity API stuff

;; Hard-coding person entity ID to 2 for now.
(defn find-person-record [session-key record-id]
  (let [url (str "http://localhost:8090/openempi-admin/openempi-ws-rest/1.0/records/?entityId=2&recordId="
                 record-id)]
    (http/get url {:headers {"OPENEMPI_SESSION_KEY" session-key}
                   :accept :json})))

(defn entities [session-key]
  (let [url "http://localhost:8090/openempi-admin/openempi-ws-rest/1.0/entities"]
    (http/get url {:headers {"OPENEMPI_SESSION_KEY" session-key}
                   :accept :json})))

;; --- main (unused)

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))


