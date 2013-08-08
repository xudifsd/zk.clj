(ns org.xudifsd.zk.utils)

(defmacro ignore-exceptions [exceptions & body]
  `(try
     (do ~@body)
     ~@(map (fn [exception]
              `(catch ~exception ~'e ~''exception-catched))
            exceptions)))
