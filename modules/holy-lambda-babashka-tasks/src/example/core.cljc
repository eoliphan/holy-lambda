(ns example.core
  (:gen-class)
  (:require
   [fierycod.holy-lambda.retriever :as r]
   [fierycod.holy-lambda.interceptor :as i]
   [fierycod.holy-lambda.agent :as agent]
   [fierycod.holy-lambda.native-runtime :as native]
   [fierycod.holy-lambda.response :as hr]
   [fierycod.holy-lambda.core :as h]))

(i/definterceptor LogIncommingRequest
  {:enter (fn [request]
            (println "Log incomming request:" request)
            request)})

(h/deflambda ExampleLambda <
  {:interceptors [LogIncommingRequest]}
  [request]
  #?(:bb
     (hr/text (str "Babashka is sweet friend of mine! Babashka version: " (System/getProperty "babashka.version")))
     :clj (hr/text "Why you didn't use babashka? ;/")))

(native/entrypoint [#'ExampleLambda])

(agent/in-context
 (println "In agent context"))
