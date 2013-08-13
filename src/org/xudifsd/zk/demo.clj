(ns org.xudifsd.zk.demo
  (:import org.xudifsd.zk.ZkClient
           org.xudifsd.zk.AtomicLong
           org.xudifsd.zk.LeaderSelection))

(defn demo-atomic [client]
  (let [atomic (AtomicLong. client "/counter")]
    (doto atomic
      (.increment)
      (.add 100))
    (.get atomic)))

(defn leader-selection-listener [^Boolean is-leader participants] ;participants maybe nil
  (pr "is-leader: ")
  (prn is-leader)
  (pr "participants")
  (prn participants))

(defn bootstrap [^String server]
  (defn watcher [event client]
    (prn event)
    (if (= (:type event) "NodeDeleted")
      false ;when node was deleted we don't want to bind to this node again
      true)) ;keep watching it

  (defn state-watcher [state client]
    (prn state))

  (def ^:dynamic *client* (ZkClient. server "myapp" state-watcher))

  (let [path (.create *client* "/foo" "somedata" :EPHEMERAL)]
    (.exists *client* path watcher))

  (demo-atomic (.getClient *client*))

  ; demo Barrier
  ; (import 'org.xudifsd.zk.Barrier)
  ; (use 'org.xudifsd.utils)
  ; (def bar (Barrier. (.getClient *client*) "/barrier" 2))
  ; (with-barrier bar (prn "in sync"))

  (LeaderSelection. (.getClient *client*) "/leader" "1" leader-selection-listener); should replace "1" to more meaningful info like host:port
  )
