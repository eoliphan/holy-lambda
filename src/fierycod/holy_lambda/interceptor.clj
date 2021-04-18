(ns fierycod.holy-lambda.interceptor
  (:require
   [fierycod.holy-lambda.retriever :as retriever])
  (:import
   [clojure.lang IPersistentMap]))

(defn- forms->def
  ([asym form]
   (forms->def asym "" form))
  ([asym ?m-docstring ?form]
   (let [docstring (if (string? ?m-docstring) ?m-docstring "")
         form (if (string? ?m-docstring)
                ?form
                (concat [?m-docstring] [?form]))]
   [asym docstring form]))
  ([asym ?m-docstring ?form & ?forms]
   (let [docstring (if (string? ?m-docstring) ?m-docstring "")
         form (if (string? ?m-docstring)
                (concat [?form] ?forms)
                (concat [?m-docstring] [?form] ?forms))]
     [asym docstring form])))

(defn- wrap-interceptor
  [?sym ?handler ?type]
  `(if (not (fn? ~?handler))
    (throw (Exception. (str "Entry " ~?type " for interceptor \"" ~?sym "\" should be a function.")))
    (fn [payload#]
      (try
        (update-in (retriever/<-wait-for-response (~?handler payload#))
                   [::interceptors :complete ~?type]
                   (fnil conj [])
                   ~?sym)
        (catch Exception ex#
          (update-in payload#
                     [::interceptors :ex]
                     (fnil conj [])
                     {:ex ex#
                      :interceptor ~?sym
                      :type ~?type}))))))

(defmacro definterceptor
  "Defines an interceptor which might be passed to a lambda definition or bind to global interceptors"
  [?sym & ?attrs]
  (let [[?sym ?docstring ?body] (apply forms->def ?sym ?attrs)]
    (if (or (nil? ?body)
            (not (instance? IPersistentMap ?body))
            (and (nil? (:enter ?body))
                 (nil? (:leave ?body))))
      (throw (IllegalArgumentException. "Interceptor should be an map of: [:enter (fn [request] request)]? and [:leave (fn [response] response)]?"))
      (let [{:keys [enter leave]} ?body
            wrapped-enter (if-not enter
                            nil
                            (wrap-interceptor (str ?sym) enter :enter))
            wrapped-leave (if-not leave
                            nil
                            (wrap-interceptor (str ?sym) leave :leave))
            interceptor `{:enter ~wrapped-enter
                          :leave ~wrapped-leave}]
        `(def ~?sym ~?docstring ~interceptor)))))

(defn- process-interceptors
  [mixin payload type]
  (if-let [interceptors (seq (:interceptors mixin))]
    (loop [interceptors interceptors
           result payload]
      (if-not (seq interceptors)
        result
        (recur (rest interceptors) (if-let [interceptor (type (first interceptors))] (interceptor result) result))))
    payload))
