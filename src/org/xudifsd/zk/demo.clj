(ns org.xudifsd.zk.demo
  (:import org.xudifsd.zk.ZkClient))

(defn bootstrap [^String server]
  (def ^:dynamic *client* (ZkClient. server))

  (defn watcher [event client]
    (prn event)
    true) ;keep watching it

  (.watch *client* watcher "/foo"))
