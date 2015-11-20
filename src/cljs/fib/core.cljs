(ns fib.core)

(defonce MAX-INT 9007199254740992)
(defonce state 
  (atom 
   {:start-game? false
    :beer-count 0
    :beer-proximity MAX-INT
    :seek-interval 500 ;ms
    }))

(defonce audio-assets 
  [{:name :proximity-sound-0 :url "resources/public/audio/test-0.wav"}
   {:name :proximity-sound-1 :url "resources/public/audio/test-1.wav"}
   {:name :proximity-sound-2 :url "resources/public/audio/test-2.wav"}
   {:name :proximity-sound-3 :url "resources/public/audio/test-3.wav"}
   {:name :proximity-sound-4 :url "resources/public/audio/test-4.wav"}
   {:name :proximity-sound-5 :url "resources/public/audio/test-5.wav"}
   {:name :proximity-sound-6 :url "resources/public/audio/test-6.wav"}
   {:name :proximity-sound-7 :url "resources/public/audio/test-7.wav"}
   {:name :proximity-sound-8 :url "resources/public/audio/test-8.wav"}
   {:name :proximity-sound-9 :url "resources/public/audio/test-9.wav"}
   {:name :proximity-sound-10 :url "resources/public/audio/test-10.wav"}
   {:name :beer-found :url "resources/public/audio/test-found.wav"}
   ])

(defn inc-beer-count! []
  (swap! state update :beer-count inc)
  (let [count (-> @state :beer-count)
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

;; Preload the audio assets
(doseq [{name :name url :url} audio-assets]
  (preload-sound name url))

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


(defn start-game []
  (swap! state assoc :start-game? true)
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
      (.log js/console "Mag" mag)
      (swap! state assoc :beer-proximity mag)
      )))

(defn alert-beer-groping-hand []
  (let [mag (-> @state :beer-proximity)]
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
      )))

;;
;; bindings
;;

;; Start Game Event
(let [dom-start-button (.querySelector js/document "#beer-start-dialog")]
  (.addEventListener dom-start-button "click" start-game))

;; Mouse movement event
(let [dom-beer-area (.querySelector js/document "#beer-area")]
  (.addEventListener dom-beer-area "mousemove" track-beer-groping-hand))

;; Alert of proximity when the game is started
(.setInterval js/window 
              (fn []
                (when (-> @state :start-game?)
                  (alert-beer-groping-hand))
                ) 500)
