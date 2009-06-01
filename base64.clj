(ns base64)

(def *encode-table* "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=")

(defn encode-num 
  [num]
  (let [a (bit-and num 63)
        b (bit-shift-right (bit-and num 4032) 6)
        c (bit-shift-right (bit-and num 258048) 12)
        d (bit-shift-right (bit-and num 16515072) 18)]
    (map (fn [x] (nth *encode-table* x )) (list d c b a))))

(defn padding [ints]
  (let [ints-zero-pad (take 2 (concat ints '(0)))]
  (let [num (+ (* 256 256 (nth ints-zero-pad 0)) (* 256 (nth ints-zero-pad 1)))]
  (take 4 (concat (take (+ (count ints) 1) (encode-num num)) (repeat \=))))))

(defn encode
  "Lazily encode a sequence as base64"
  [s]
  (if s
    (let [x (map int (take 3 s))]
    (if (= 3 (count x))
      (let [num (+ (nth x 2) (* 256 (nth x 1)) (* 256 256 (first x)))]
        (lazy-cat (encode-num num) (encode (drop 3 s))))
          (padding x)))))
