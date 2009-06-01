(load-file "timetric.clj")

(def timetric-connection
  {"token-key" "put-your-key-here"
   "token-secret" "put-your-secret-here"})

(def interesting-series
'("https://timetric.com/series/q0qbNDVaQVeKfisSaH8nyA/csv/"
  "https://timetric.com/series/lWrNTpjqSaSsIudZoa0aSw/csv/"))

(for [url interesting-series] (timetric/get-series timetric-connection url))
