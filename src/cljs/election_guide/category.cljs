(ns election-guide.category
  (:require [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]
            [election-guide.mutations :as mutations]
            [clojure.string :as string]))

(defui Category
  static om/IQuery
  (query [this]
    '[:name])
  Object
  (render [this]
    (let [category (om/props this)]
      (dom/h3
        #js {:className "category"
             :onClick (fn [_]
                       (om/transact! this
                        `[(mutate/select-category ~{:type "category" :title category}) :selected]))}

           category))))

(def category (om/factory Category {:keyfn #(inc %)}))
