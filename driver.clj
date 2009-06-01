(load-file "timetric.clj")

(def timetric-connection
  {"token-key" "key"
   "token-secret" "secret"})

(def interesting-series
'("https://timetric.com/series/q0qbNDVaQVeKfisSaH8nyA/csv/"
  "https://timetric.com/series/lWrNTpjqSaSsIudZoa0aSw/csv/"))

(for [url interesting-series] (get-timetric-series timetric-connection url))
