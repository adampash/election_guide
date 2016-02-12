(ns election-guide.mutations
  (:require [om.next :as om]))

(defmulti mutate om/dispatch)

(defmethod mutate `mutate/select-category
  [{:keys [state]} key params]
  {:action
    (fn []
      (swap! state assoc :selected params))})

(defmethod mutate `mutate/select-candidate
  [{:keys [state]} key params]
  {:action
    (fn []
      (swap! state assoc :selected params))})
      ; (swap! state assoc-in
      ;  [:candidate/by-candidate name :selected] true))})
