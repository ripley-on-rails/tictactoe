(ns ^:figwheel-hooks tictactoe.view
  (:require
   [goog.dom :as gdom]
   [reagent.core :as reagent :refer [atom]]
   [reagent.dom :as rdom]
   [tictactoe.game :as game]))

(defonce app-state (atom game/initial-game)) 

(defn get-app-element [] 
  (gdom/getElement "app"))

(defn handle-click [x y]
  (swap! app-state (fn [state]
                     (if (or (game/field state x y)
                             (:win state))
                       state
                       (game/make-move state x y)))))

(defn game-board [] 
  [:div
   [:h1 "Tic Tac Toe"]
   [:p (if-let [winner (get-in @app-state [:win :winner])]
         (str "Player " (inc winner) " won the game")
         (str "It's player " (inc (:current-player @app-state)) "'s turn"))]
   [:table
    [:tbody
     (let [winnining-cells (get-in @app-state [:win :combo])]
       ;; We need to use `doall` as `for` creates a lazy seq and
       ;; Reactive deref is not supported in lazy seq
       (doall 
        (for [y (range 3)]
          ;; we need to set a unique key as we would in react when
          ;; when rendering multiple items
          [:tr {:key y}
           (doall 
            (for [x (range 3)]
              [:td
               {:key x
                :class (if (some #{[x y]} winnining-cells) "win")
                :on-click (fn [] (handle-click x y))}
               (game/field @app-state x y)]))])))]]
   [:p [:a {:href "#"
            :on-click #(reset! app-state game/initial-game)}
        "Start over"]]]) 

(defn mount [el]
  (rdom/render [game-board] el))

(defn mount-app-element []
  (when-let [el (get-app-element)]
    (mount el)))

;; conditionally start your application based on the presence of an "app" element
;; this is particularly helpful for testing this ns without launching the app
(mount-app-element)

;; specify reload hook with ^:after-load metadata
(defn ^:after-load on-reload []
  (mount-app-element)
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
