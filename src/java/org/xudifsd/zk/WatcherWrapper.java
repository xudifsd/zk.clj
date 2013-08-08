package org.xudifsd.zk;

import clojure.lang.Keyword;
import clojure.lang.IFn;

import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.WatchedEvent;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;

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

	private static Keyword mapToKeyword(Watcher.Event.EventType type) {
		Keyword result = null;

		switch (type) {
		case NodeChildrenChanged:
			result = getKeyword("NodeChildrenChanged");
			break;
		case NodeCreated:
			result = getKeyword("NodeCreated");
			break;
		case NodeDataChanged:
			result = getKeyword("NodeDataChanged");
			break;
		case NodeDeleted:
			result = getKeyword("NodeDeleted");
			break;
		}
		return result;
	}

	@Override
	public void process(WatchedEvent event) throws Exception {
		// if function returns true, we keep watching it
		boolean keepWatching = (Boolean)function.invoke(mapToKeyword(event.getType()), client);

		if (keepWatching)
			client.getChildren().usingWatcher(this).forPath(path);
	}
}
