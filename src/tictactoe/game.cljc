(ns tictactoe.game
  (:use [clojure.set :only [map-invert]]))

(def initial-game
  {:board [[nil nil nil]
           [nil nil nil]
           [nil nil nil]]
   :current-player 0
   :tokens {0 "x"
            1 "o"}})

#_(defn current-player [game]
    (:current-player game))
(def current-player :current-player)

(defn next-player [game]
  (update game :current-player #(mod (inc %) 2)))

(defn- put [game x y token]
  (assoc-in game [:board y x] token))

(defn field [game x y]
  (get-in game [:board y x]))

(defn- error [message]
  (throw #?(:clj (new Exception message)
            :cljs (js/Error message))))

(def winning-combos
  (let [diagonals [(map (fn [i] [i i])
                        (range 3))
                   (map (fn [i] [i (- 2 i)])
                        (range 3))]]
    (concat diagonals
            (mapcat #(let [combo (map (partial vector %) (range 3))]
                       [combo (map reverse combo)])
                    (range 3)))))

(defn winner [{:keys [board] :as game}]
  (->> winning-combos
       (map #(map (fn [combo]
                    (apply field game combo))
                  %))
       (filter (partial apply =))
       (map first)
       (filter identity)
       first))

(defn determine-winner [{:keys [board] :as game}]
  (->> winning-combos
       (map #(map (fn [combo]
                    [combo (apply field game combo)])
                  %))
       (filter #(apply = (map last %)))
       (filter (comp last first))
       first
       ((fn [combo]
          (assoc game :win
                 (if-let [winner (->> combo first last (token->player game))]
                   {:winner winner
                    :cells (map first combo)}))))))

(defn- player->token [game player]
  (get-in game [:tokens player]))

(defn- token->player [game token]
  ((map-invert (:tokens game)) token))

(defn make-move [game x y]
  (if-let [w (winner game)]
    (error (str "game already won by player " (inc (token->player game w))))
    (let [token (player->token game (current-player game))]
      (if (field game x y)
        (error "occupied field")
        (-> game
            (put x y token)
            determine-winner
            (#(if (:win %)
                %
                (next-player %))))))))

(defn print-game [game]
  (println "current player:" (inc (current-player game)))
  (println "board:")
  (for [y (range 3)]
    (println (apply str (map (fn [x] (if (nil? x) "." x))
                             (get-in game [:board y]))))))


;; (use 'clojure.pprint)

#_ (-> (initial-game)
       (make-move 2 0)
       (make-move 1 1)
       (make-move 2 2)
       (make-move 1 2)
       (make-move 2 1)
       print-game)

#_(print-game (initial-game))
