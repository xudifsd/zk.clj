(ns org.xudifsd.zk.utils
  (:import org.xudifsd.zk.Barrier
           org.xudifsd.zk.Semaphore))

(defmacro ignore-exceptions [exceptions & body]
  `(try
     (do ~@body)
     ~@(map (fn [exception]
              `(catch ~exception ~'e ~''exception-catched))
            exceptions)))

(defmacro with-barrier [client barrier-path barrier-num & body]
  `(.goInto (Barrier. ~client ~barrier-path ~barrier-num)
            (fn [] (do ~@body))))

(defmacro with-semaphore [client semaphore-path semaphore-num & body]
  `(.goInto (Semaphore. ~client ~semaphore-path ~semaphore-num)
            (fn [] (do ~@body))))
