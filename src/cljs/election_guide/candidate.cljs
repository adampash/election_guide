(ns election-guide.candidate
  (:require [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]
            [election-guide.mutations :as mutations]
            [clojure.string :as string]))


(defui Candidate
  static om/Ident
  (ident [this {:keys [candidate]}]
    [:candidate/by-candidate candidate])
  static om/IQuery
  (query [this]
    '[:candidate :party :image-url :selected])
  Object
  (render [this]
    (let [{:keys [candidate party image-url selected] :as props} (om/props this)]
      (dom/div #js {:className (str "candidate" (if selected " selected"))
                    :onClick
                      (fn [_]
                         (om/transact! this
                          `[(mutate/select-candidate ~{:name candidate}) :selected :candidates]))}
        (dom/img #js
          {:src (or image-url "images/hillary.jpg")
           :className (-> party js/String .toLowerCase)})
        (dom/h4 nil candidate)))))

(def candidate (om/factory Candidate {:keyfn :candidate}))
