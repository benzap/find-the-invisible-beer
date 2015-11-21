(ns fib.random)

(defn pick-value-in-range 
  "PIck a value in the range [start end]"
  [start end]
  (let [t (- end start)]
    (+ (rand t) start)))
