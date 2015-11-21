(ns fib.event)

(defn get-mouse-location [event]
  (let [mouse-area (.-target event)
        offset-x (.-offsetLeft mouse-area)
        offset-y (.-offsetTop mouse-area)
        mouse-x (-> event .-pageX (- offset-x))
        mouse-y (-> event .-pageY (- offset-y))
        ]
    [mouse-x mouse-y]))
