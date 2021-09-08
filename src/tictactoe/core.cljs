(ns ^:figwheel-hooks tictactoe.core
  (:require
   [goog.dom :as gdom]
   [reagent.core :as reagent :refer [atom]]
   [reagent.dom :as rdom]
   [tictactoe.game :as game]))

;; define your app data so that it doesn't get over-written on reload
(defonce state (atom game/initial-game))

(defn handle-click [x y]
  (swap! state #(game/make-move % x y)))

(defn tic-tac-toe []
  [:div
   [:h1 "Tic Tac Toe"]
   [:p (if-let [winner (get-in @state [:win :winner])]
         (str "Player " (inc winner) " won the game!")
         (str "Current player: " (inc (game/current-player @state))))]
   [:table
    [:tbody
     (let [winning-cells (get-in @state [:win :cells])]
       (doall
        (for [y (range 3)]
          [:tr {:key y} (doall
                         (for [x (range 3)]
                           [:td
                            {:key x
                             :class (if (some #{[x y]} winning-cells) "win")
                             :on-click (fn [] (handle-click x y))}
                            (str (game/field @state x y))]))])))]]
   [:p [:a {:href "#"
            :on-click #(reset! state game/initial-game)} "xStart over"]]])





;; Regeant / React stuff

(defn get-app-element []
  (gdom/getElement "app"))

(defn mount [el]
  (rdom/render [tic-tac-toe] el))

(defn mount-app-element []
  (when-let [el (get-app-element)]
    (mount el)))

;; conditionally start your application based on the presence of an "app" element
;; this is particularly helpful for testing this ns without launching the app
(mount-app-element)

;; specify reload hook with ^;after-load metadata
(defn ^:after-load on-reload []
  (mount-app-element)
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
