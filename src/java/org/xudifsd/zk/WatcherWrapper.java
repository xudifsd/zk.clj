package org.xudifsd.zk;

import clojure.lang.Keyword;
import clojure.lang.IFn;
import clojure.lang.PersistentHashMap;

import org.apache.zookeeper.WatchedEvent;

import org.apache.log4j.Logger;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;

import java.util.HashMap;
import java.lang.ClassCastException;

import static org.xudifsd.zk.Bridge.getTypeString;
import static org.xudifsd.zk.Bridge.getStateString;
import static org.xudifsd.zk.Bridge.getKeyword;

public class WatcherWrapper implements CuratorWatcher {
	private static final Logger logger = Logger.getLogger(WatcherWrapper.class);
	private CuratorFramework client;
	private IFn function;
	private String path;
	private WatcherType type;
	public static enum WatcherType {
		EXISTS, GETCHILDREN, GETDATA;
	}

	public WatcherWrapper(CuratorFramework client, IFn function, String path, WatcherType type) {
		this.client = client;
		this.function = function;
		this.path = path;
		this.type = type;
	}

	@Override
	public void process(WatchedEvent event) throws Exception {
		HashMap<Keyword, String> map = new HashMap<Keyword, String>();

		map.put(getKeyword("path"), event.getPath());
		map.put(getKeyword("type"), getTypeString(event.getType()));
		map.put(getKeyword("state"), getStateString(event.getState()));

		// if function returns true, we keep watching it
		boolean keepWatching = false;
		try {
			keepWatching = (Boolean)function.invoke(PersistentHashMap.create(map), client);
		} catch (ClassCastException e) {
			//pass
		}

		if (keepWatching) {
			switch (type) {
			case EXISTS:
				client.checkExists().usingWatcher(this).forPath(path);
				break;
			case GETCHILDREN:
				client.getChildren().usingWatcher(this).forPath(path);
				break;
			case GETDATA:
				client.getData().usingWatcher(this).forPath(path);
				break;
			}
		}
	}
}
