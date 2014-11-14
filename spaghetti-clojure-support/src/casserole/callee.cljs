(ns casserole.callee)

(enable-console-print!)

(println "Hello world!" (. js/JavaScriptExampleObject getInt))

(defn say-hello [person] (str "Hello" person))

(defn exec [f] (f))

(defn add-attr [obj name val]
  (-> obj
      js->clj
      (assoc name val)
      clj->js))