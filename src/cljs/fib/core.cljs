(ns fib.core)

(defonce beer-count (atom 0))
(defn inc-beer-count! []
  (let [count (swap! beer-count inc)
        dom-beer-count (.querySelector js/document "#beer-count")]
    (aset dom-beer-count "innerHTML" (str count))
    ))


;;
;; audio
;;
(defonce audio-listing (atom {}))
(defn preload-sound [sound-name sound-path]
  (let [audio (js/Audio.)]
    (doto audio
      (aset "preload" "auto")
      (aset "src" sound-path)
      )
    (swap! audio-listing assoc sound-name audio)
    
    ;; Log the audio files that are loaded
    (.addEventListener audio "canplaythrough" 
                       (fn []
                         (.log js/console (str "Loaded Audio: " sound-path))
                         ))
    ))
(preload-sound :proximity-sound-0 "resources/public/audio/test-0.wav")
(preload-sound :proximity-sound-1 "resources/public/audio/test-1.wav")
(preload-sound :proximity-sound-2 "resources/public/audio/test-2.wav")
(preload-sound :proximity-sound-3 "resources/public/audio/test-3.wav")
(preload-sound :proximity-sound-4 "resources/public/audio/test-4.wav")
(preload-sound :proximity-sound-5 "resources/public/audio/test-5.wav")
(preload-sound :proximity-sound-6 "resources/public/audio/test-6.wav")
(preload-sound :proximity-sound-7 "resources/public/audio/test-7.wav")
(preload-sound :proximity-sound-8 "resources/public/audio/test-8.wav")
(preload-sound :proximity-sound-9 "resources/public/audio/test-9.wav")
(preload-sound :proximity-sound-10 "resources/public/audio/test-10.wav")
(preload-sound :beer-found "resources/public/audio/test-found.wav")

(defn play-sound [sound-name]
  (if-let [audio (-> @audio-listing sound-name)]
    (.play audio)
    ))

(defn pick-value-in-range [start end]
  (let [t (- end start)]
    (+ (rand t) start)))

(defonce current-hidden-beer (atom nil))
(defn generate-hidden-beer []
  (let [beer-area (.querySelector js/document "#beer-area")
        screen-width (aget beer-area "offsetWidth")
        screen-height (aget beer-area "offsetHeight")
        rand-x (pick-value-in-range 25 (- screen-width 25))
        rand-y (pick-value-in-range 25 (- screen-height 25))
        beer-element (.createElement js/document "div")
        ]
    (doto beer-element
      (aset "className" "hidden-beer")
      (aset "style" "left" (str rand-x "px"))
      (aset "style" "top" (str rand-y "px"))
      )
    (.appendChild beer-area beer-element)
    (reset! current-hidden-beer beer-element)
    ))

(defonce game-started? (atom false))
(defn start-game []
  (reset! game-started? true)
  (inc-beer-count!)
  (generate-hidden-beer)
  (play-sound :proximity-sound-0)
  )

(defn track-beer-groping-hand [event]
  (when-let [beer-element @current-hidden-beer]
    (let [mouse-x (-> event .-layerX)
          mouse-y (-> event .-layerY)
          beer-x (.parseInt js/window (aget beer-element "style" "left"))
          beer-y (.parseInt js/window (aget beer-element "style" "top"))
          tx (- mouse-x beer-x)
          ty (- mouse-y beer-y)
          mag (Math/sqrt (+ (* tx tx) (* ty ty)))
          ]
      (cond
        (< mag 5)
        (play-sound :proximity-sound-10)
        (< mag 10)
        (play-sound :proximity-sound-9)
        (< mag 20)
        (play-sound :proximity-sound-8)
        (< mag 30)
        (play-sound :proximity-sound-7)
        (< mag 40)
        (play-sound :proximity-sound-6)
        (< mag 50)
        (play-sound :proximity-sound-5)
        (< mag 60)
        (play-sound :proximity-sound-4)
        (< mag 75)
        (play-sound :proximity-sound-3)
        (< mag 100)
        (play-sound :proximity-sound-2)
        (< mag 150)
        (play-sound :proximity-sound-1)
        :else
        (play-sound :proximity-sound-0)
      ))))

;;
;; bindings
;;

;; Start Game Event
(let [dom-start-button (.querySelector js/document "#beer-start-dialog")]
  (.addEventListener dom-start-button "click" start-game))

;; Mouse movement event
(let [dom-beer-area (.querySelector js/document "#beer-area")]
  (.addEventListener dom-beer-area "mousemove" track-beer-groping-hand))
