(ns fib.dom)

(defn query [s]
  (.querySelector js/document s))

(defn hide [elem]
  (aset elem "style" "display" "none")
  )

(defn show [elem]
  (aset elem "style" "display" "initial")
  )

(defn get-element-dimensions [elem]
  (let [left (aget elem "style" "left")
        top (aget elem "style" "top")
        width (aget elem "offsetWidth")
        height (aget elem "offsetHeight")
        ]
    {:left left
     :top top
     :center-x (+ left (/ width 2))
     :center-y (+ top (/ height 2))
     }))

(defn remove-element-from-parent [elem]
  (when-let [parent-element (.-parentNode elem)]
    (.removeChild parent-element elem)))
