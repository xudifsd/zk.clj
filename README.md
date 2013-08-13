# zk.clj

Just a wrapper of clojure for curator zookeeper, but provided more clojure friendly interface.

Currently couldn't use maven or [lein](https://github.com/technomancy/leiningen) to install, if you want to have a try, download a [zookeeper](https://www.apache.org/dyn/closer.cgi/zookeeper/) unpack and start it, and clone this repo.

In the root dir of this repo try following:

    $ lein repl
    user=> (use 'org.xudifsd.zk.demo)
    user=> (bootstrap "localhost:2181");replace this with your host:port
    user=> (demo-barrier)
    ; the last expression will block
    ; you can unblock it by start another shell and repeat those
    ; expression again

learn more by looking the code in demo.clj

have fun :)
