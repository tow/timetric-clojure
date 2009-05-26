(defn parse-csv
  "Parse limited sort of CSV"
  [s]
  (vec 
    (map 
      (fn [coll]
        (vec (rest coll)))
      (re-seq #"(.*),\s*(.*)" s))))

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
  (vector
    (date-from-unix-timestamp (Double. (first coll)))
    (timetric-value (nth coll 1))))

(defn parse-timetric-csv
  [s]
  (vec
    (map ts-pair (parse-csv s))))


(import '(java.net URL)                                       
        '(java.lang StringBuilder)
        '(java.io BufferedReader InputStreamReader))

(defn fetch-url                                               
  "Return the web page as a string."                          
  [address]
  (let [url (URL. address)]                                   
    (with-open [stream (. url (openStream))]                  
      (let [buf (BufferedReader. (InputStreamReader. stream))]
        (apply str (for [x (line-seq buf)] (str x "\n")))))))

(def interesting-series                                
'("http://timetric.com/series/q0qbNDVaQVeKfisSaH8nyA/csv/" 
"http://timetric.com/series/lWrNTpjqSaSsIudZoa0aSw/csv/"))

(for [url interesting-series] (parse-timetric-csv (fetch-url url)))
