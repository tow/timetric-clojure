(load-file "timetric.clj")

(def token-key "key")
(def token-secret "secret")

(def interesting-series
'("https://timetric.com/series/q0qbNDVaQVeKfisSaH8nyA/csv/" 
  "https://timetric.com/series/lWrNTpjqSaSsIudZoa0aSw/csv/"))

(for [url interesting-series] (parse-timetric-csv (fetch-url url)))
