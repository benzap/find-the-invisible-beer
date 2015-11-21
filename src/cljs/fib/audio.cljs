(ns fib.audio)

(defonce audio-listing (atom {}))

(defn preload-sound [sound-name sound-path]
  (let [audio (js/Audio.)]
    (doto audio
      (aset "preload" "auto")
      (aset "src" sound-path)
      )
    (swap! audio-listing assoc sound-name audio)
    ))

(defn play-sound [sound-name]
  (if-let [audio (-> @audio-listing sound-name)]
    (.play audio)
    ))
