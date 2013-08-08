(ns org.xudifsd.zk.demo
  (:import org.xudifsd.zk.ZkClient))

(defn bootstrap [^String server]
  (def ^:dynamic *client* (ZkClient. server))

  (defn watcher [event client]
    (prn event)
    (if (= (:type event) "NodeDeleted")
      false ;when node was deleted we can't bind to this node again
      true)) ;keep watching it

  (let [path (.create *client* "/foo" "somedata" :EPHEMERAL)]
    (.exists *client* path watcher)))
