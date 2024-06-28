(ns tictactoe.game)

(def initial-game
  {:board [[nil nil nil]
           [nil nil nil]
           [nil nil nil]]
   :current-player 0
   :tokens ["x" "o"]})

(defn field [game x y]
  (get-in game [:board y x]))

(defn- put [game x y token]
  (assoc-in game [:board y x] token))

(defn- player->token [game player]
  (get-in game [:tokens player]))

(defn- token->player [game token]
  (.indexOf (:tokens game) token))

(defn- toggle-player [game]
  (update game :current-player #(mod (inc %) 2)))

(defn- error [message]
  (throw #?(:clj (new Exception message)
            :cljs (js/Error message))))

(def winning-combos
  (let [rows (for [y (range 3)]
               (for [x (range 3)]
                 [x y]))
        cols (for [x (range 3)]
               (for [y (range 3)]
                 [x y]))
        diagonals [(for [i (range 3)] [i i])
                   (for [i (range 3)] [i (- 2 i)])]]
    (concat rows cols diagonals)))

(defn- determine-winner [game]
  (let [win (first (for [combo winning-combos
                         :let [tokens (map (partial apply field game) combo)
                               player (token->player game (first tokens))]
                         :when (and (apply = tokens)
                                    (first tokens))]
                     {:winner player
                      :combo combo}))]
    (if win
      (assoc game :win win)
      game)))

(defn make-move [{:keys [current-player] :as game} x y]
  (if-let [winner (get-in game [:win :winner])]
    (error (str "game already won by player " (inc (token->player game winner))))
    (let [token (player->token game current-player)]
      (if (field game x y)
        (error (str "occupied field"))
        (-> game
            (put x y token)
            determine-winner
            (#(if (:win %)
                %
                (toggle-player %))))))))

(comment
  (-> initial-game
      (make-move 1 1)
      (make-move 0 0)
      (make-move 2 0)
      (make-move 0 1)
      (make-move 0 2)))
