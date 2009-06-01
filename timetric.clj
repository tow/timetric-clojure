(load-file "base64.clj")

(defn parse-csv
  "Parse limited sort of CSV"
  [s]
  (map 
    (fn [coll] (rest coll))
    (re-seq #"(.*),\s*(.*)" s)))

(import '(java.util Date))

(defn date-from-unix-timestamp
  "Make a Java Date object from a Unix timestamp"
  [ts]
  (Date. (long (* 1000 ts))))

(defn timetric-value
  [s]
  (try (Double. s)
    (catch NumberFormatException e 
      ({"null" nil, "true" true, "false" false} s))))

(defn ts-pair                                                 
  [coll]             
  (list
    (date-from-unix-timestamp (Double. (first coll)))
    (timetric-value (nth coll 1))))

(defn parse-timetric-csv
  [s]
  (for [pair (parse-csv s)] (ts-pair pair)))


(def token-key "key")
(def token-secret "secret")

(import '(java.net URL)                                       
        '(java.net Authenticator)
        '(java.net PasswordAuthentication)
        '(java.lang StringBuilder)
        '(java.io BufferedReader InputStreamReader))

(defn http-basic-encode-credentials
  [key secret]
  (apply str (encode (str key ":" secret))))

(defn http-basic-auth-header
  [key secret]
  (str "Basic " (http-basic-encode-credentials key secret)))

(defn fetch-url-with-auth
  [address]
  (let [url (URL. address)]
    (let [connection (. url (openConnection))]
      (. connection setRequestProperty "Authorization"
        (http-basic-auth-header token-key token-secret))
      (with-open [stream (. connection getInputStream)]
      (let [buf (BufferedReader. (InputStreamReader. stream))]
        (apply str (for [x (line-seq buf)] (str x "\n"))))))))

(defn fetch-url                                               
  "Return the web page as a string."                          
  [address]
  (let [url (URL. address)]                                   
    (with-open [stream (. url (openStream))]                  
      (let [buf (BufferedReader. (InputStreamReader. stream))]
        (apply str (for [x (line-seq buf)] (str x "\n")))))))

(def interesting-series                                
'("https://timetric.com/series/q0qbNDVaQVeKfisSaH8nyA/csv/" 
"https://timetric.com/series/lWrNTpjqSaSsIudZoa0aSw/csv/"))

(for [url interesting-series] (parse-timetric-csv (fetch-url url)))
