package org.xudifsd.zk;

import clojure.lang.Keyword;
import clojure.lang.IFn;
import clojure.lang.PersistentHashMap;

import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.WatchedEvent;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;

import java.util.HashMap;
import java.lang.ClassCastException;

public class WatcherWrapper implements CuratorWatcher {
	private CuratorFramework client;
	private IFn function;
	private String path;

	public WatcherWrapper(CuratorFramework client, IFn function, String path) {
		this.client = client;
		this.function = function;
		this.path = path;
	}

	private static Keyword getKeyword(String s) {
		return Keyword.intern(s);
	}

	private static String getTypeString(Watcher.Event.EventType type) {
		String result = null;

		switch (type) {
		case NodeChildrenChanged:
			result = "NodeChildrenChanged";
			break;
		case NodeCreated:
			result = "NodeCreated";
			break;
		case NodeDataChanged:
			result = "NodeDataChanged";
			break;
		case NodeDeleted:
			result = "NodeDeleted";
			break;
		}
		return result;
	}

	private static String getStateString(Watcher.Event.KeeperState state) {
		String result = null;

		switch (state) {
		case AuthFailed:
			result = "AuthFailed";
			break;
		case ConnectedReadOnly:
			result = "ConnectedReadOnly";
			break;
		case Disconnected:
			result = "Disconnected";
			break;
		case Expired:
			result = "Expired";
			break;
		case NoSyncConnected:
			result = "NoSyncConnected";
			break;
		case SaslAuthenticated:
			result = "SaslAuthenticated";
			break;
		case SyncConnected:
			result = "SyncConnected";
			break;
		}
		return result;
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

		if (keepWatching)
			client.getChildren().usingWatcher(this).forPath(path);
	}
}
