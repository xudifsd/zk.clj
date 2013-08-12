(ns org.xudifsd.zk.demo
  (:import org.xudifsd.zk.ZkClient
           org.xudifsd.zk.AtomicLong))

(defn demo-atomic [client]
  (let [atomic (AtomicLong. client "/counter")]
    (doto atomic
      (.increment)
      (.add 100))
    (.get atomic)))

(defn bootstrap [^String server]
  (defn watcher [event client]
    (prn event)
    (if (= (:type event) "NodeDeleted")
      false ;when node was deleted we can't bind to this node again
      true)) ;keep watching it
  (defn state-watcher [state client]
    (prn state))

  (def ^:dynamic *client* (ZkClient. server state-watcher))

  (let [path (.create *client* "/foo" "somedata" :EPHEMERAL)]
    (.exists *client* path watcher))
  (demo-atomic (.getClient *client*)))
