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

(import '(java.net URL)                                       
        '(java.lang StringBuilder)
        '(java.io BufferedReader InputStreamReader))

(defn http-basic-encode-credentials
  [key secret]
  (apply str (encode (str key ":" secret))))

(defn http-basic-auth-header
  [credentials]
  (str "Basic "
    (http-basic-encode-credentials
      (credentials "token-key") (credentials "token-secret"))))

(defn fetch-url-with-auth
  [credentials address]
  (let [url (URL. address)]
    (let [connection (. url (openConnection))]
      (. connection setRequestProperty "Authorization"
        (http-basic-auth-header credentials))
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

(defn get-timetric-series
   "Retrieve & parse a timetric URL"
   [credentials url]
   (parse-timetric-csv (fetch-url-with-auth credentials url)))
