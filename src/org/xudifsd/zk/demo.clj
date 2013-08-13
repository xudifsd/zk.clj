(ns org.xudifsd.zk.demo
  (:import org.xudifsd.zk.ZkClient
           org.xudifsd.zk.recipes.AtomicLong
           org.xudifsd.zk.recipes.LeaderSelection)
  (:use org.xudifsd.zk.utils))

(defn bootstrap [^String server]
  (defn watcher [event client]
    (prn event)
    (if (= (:type event) "NodeDeleted")
      false ;when node was deleted we don't want to bind to this node again
      true)) ;keep watching it

  (defn state-watcher [state client]
    (prn state))

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

  (def ^:dynamic *client* (ZkClient. server "myapp" state-watcher))

  (let [path (.create *client* "/foo" "somedata" :EPHEMERAL)]
    (.exists *client* path watcher))

  (demo-atomic (.getClient *client*))

  (defn demo-barrier []
    (with-barrier (.getClient *client*) "/barrier" 2
                  (prn "in sync")))

  (defn demo-semaphore []
    (with-semaphore (.getClient *client*) "/semaphore" 1
                    (read)));note 1 must be

  (LeaderSelection. (.getClient *client*) "/leader" "1" leader-selection-listener); should replace "1" to more meaningful info like host:port
  )
