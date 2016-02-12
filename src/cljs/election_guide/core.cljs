(ns election-guide.core
  (:require [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]
            [clojure.string :as string]
            [election-guide.category :refer [category]]
            [election-guide.candidate :refer [candidate Candidate]]
            [election-guide.mutations :refer [mutate]]
            [election-guide.data :as data]))

(enable-console-print!)

(def init-data data/init-data)

(defui RootView
  static om/IQuery
  (query [this]
    (let [subquery (om/get-query Candidate)]
      `[{:candidates ~subquery} :categories :selected]))
  Object
  (render [this]
    (let [{:keys [candidates categories selected]} (om/props this)]
      (dom/div #js {:className "container"}
        (dom/div nil
          (dom/div #js {:className "candidates"}
            (map candidate candidates))
          (dom/div #js {:className "categories"}
            (map category categories)))))))

(defmulti read om/dispatch)

(defn candidate-selected [state key]
  (let [candidate (get-in state key)
        selected (:selected state)]
    (assoc candidate :selected (= (:candidate candidate) (:name selected)))))

(defn get-candidates [state key]
  (let [st @state]
    (into []
      (map
        #(candidate-selected st %) (get st key)))))


(defmethod read :candidates
  [{:keys [state] :as env} key params]
  {:value (get-candidates state key)})

(defmethod read :default
  [{:keys [state] :as env} key params]
  {:value (key @state)})



(def parser (om/parser {:read read :mutate mutate}))

(defonce reconciler
  (om/reconciler
    {:state   init-data
     :parser parser}))

(om/add-root! reconciler
  RootView (. js/document (getElementById "app")))
