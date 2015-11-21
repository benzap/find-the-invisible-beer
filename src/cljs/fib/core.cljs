(ns fib.core
  (:require [fib.random :as random]
            [fib.dom :as dom]
            [fib.event :as event]
            [fib.audio :as audio]))

(defonce MAX-INT 9007199254740992)
(defonce state 
  (atom 
   {:start-game? false
    :beer-count 0
    :beer-proximity MAX-INT
    :seek-interval 500 ;ms
    :current-hidden-beer nil
    }))

(defonce audio-assets 
  [{:name :proximity-sound-0 :url "resources/public/audio/beer-0.wav"}
   {:name :proximity-sound-1 :url "resources/public/audio/beer-1.wav"}
   {:name :proximity-sound-2 :url "resources/public/audio/beer-2.wav"}
   {:name :proximity-sound-3 :url "resources/public/audio/beer-3.wav"}
   {:name :proximity-sound-4 :url "resources/public/audio/beer-4.wav"}
   {:name :proximity-sound-5 :url "resources/public/audio/test-5.wav"}
   {:name :proximity-sound-6 :url "resources/public/audio/test-6.wav"}
   {:name :proximity-sound-7 :url "resources/public/audio/test-7.wav"}
   {:name :proximity-sound-8 :url "resources/public/audio/test-8.wav"}
   {:name :proximity-sound-9 :url "resources/public/audio/test-9.wav"}
   {:name :proximity-sound-10 :url "resources/public/audio/test-10.wav"}
   {:name :beer-found :url "resources/public/audio/test-found.wav"}
   ])

(def div-list
  {:beer-title (dom/query "#beer-title")
   :beer-area (dom/query "#beer-area")
   :beer-scoreboard (dom/query "#beer-scoreboard")
   :beer-count (dom/query "#beer-count")
   :beer-start-dialog (dom/query "#beer-start-dialog")
   :beer-dialog (dom/query "#beer-dialog")
   })

(defn inc-beer-count! []
  (swap! state update :beer-count inc)
  (let [count (-> @state :beer-count)
        dom-beer-count (div-list :beer-count)]
    (aset dom-beer-count "innerHTML" (str count))
    ))

;; Preload the audio assets
(doseq [{name :name url :url} audio-assets]
  (audio/preload-sound name url))

(defn beer-click-event [e]
  (when-let [beer-dom (-> @state :current-hidden-beer)]
    (dom/remove-element-from-parent beer-dom)
    (swap! state merge {:start-game? false :current-hidden-beer nil})
    (inc-beer-count!)
    (dom/show (div-list :beer-start-dialog))
    (audio/play-sound :beer-found)
    ))

(defn generate-hidden-beer []
  (let [beer-area (.querySelector js/document "#beer-area")
        screen-width (aget beer-area "offsetWidth")
        screen-height (aget beer-area "offsetHeight")
        rand-x (random/pick-value-in-range 25 (- screen-width 25))
        rand-y (random/pick-value-in-range 25 (- screen-height 25))
        beer-element (.createElement js/document "div")
        ]
    (doto beer-element
      (aset "className" "hidden-beer")
      (aset "style" "left" (str rand-x "px"))
      (aset "style" "top" (str rand-y "px"))
      )

    ;; apply click event
    (.addEventListener beer-element "click" beer-click-event)

    beer-element
    ))

(defn event-start-game []
  (let [hidden-beer (generate-hidden-beer)
        beer-area (div-list :beer-area)
        ]
    (swap! state assoc :start-game? true)
    (dom/hide (div-list :beer-start-dialog))

    ;;Place the beer in the arena
    (.appendChild beer-area hidden-beer)
    (swap! state assoc :current-hidden-beer hidden-beer)
    ))

(defn track-beer-groping-hand [event]
  (when-let [beer-element (-> @state :current-hidden-beer)]
    (let [
          [mouse-x mouse-y] (event/get-mouse-location event)
          beer-x (.parseInt js/window (aget beer-element "style" "left"))
          beer-y (.parseInt js/window (aget beer-element "style" "top"))
          tx (- mouse-x beer-x)
          ty (- mouse-y beer-y)
          mag (Math/sqrt (+ (* tx tx) (* ty ty)))
          ]
      (swap! state assoc :beer-proximity mag)
      )))

(defn alert-beer-groping-hand [beer-proximity]
  (cond
    (< beer-proximity 5)
    (audio/play-sound :proximity-sound-10)
    (< beer-proximity 10)
    (audio/play-sound :proximity-sound-9)
    (< beer-proximity 20)
    (audio/play-sound :proximity-sound-8)
    (< beer-proximity 30)
    (audio/play-sound :proximity-sound-7)
    (< beer-proximity 40)
    (audio/play-sound :proximity-sound-6)
    (< beer-proximity 50)
    (audio/play-sound :proximity-sound-5)
    (< beer-proximity 60)
    (audio/play-sound :proximity-sound-4)
    (< beer-proximity 75)
    (audio/play-sound :proximity-sound-3)
    (< beer-proximity 150)
    (audio/play-sound :proximity-sound-2)
    (< beer-proximity 300)
    (audio/play-sound :proximity-sound-1)
    (< beer-proximity 600)
    (audio/play-sound :proximity-sound-0)
    ))

;;
;; bindings
;;

;; Start Game Event
(let [dom-start-button (.querySelector js/document "#beer-start-dialog")]
  (.addEventListener dom-start-button "click" event-start-game))

;; Mouse movement event
(let [dom-beer-area (.querySelector js/document "#beer-area")]
  (.addEventListener dom-beer-area "mousemove" track-beer-groping-hand))

;; Alert of proximity when the game is started
(.setInterval js/window 
              (fn []
                (let [{start-game? :start-game?
                       beer-proximity :beer-proximity} 
                      @state]
                  (when start-game? 
                    (alert-beer-groping-hand beer-proximity)))
                ) (:seek-interval @state))
