package org.xudifsd.zk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.ZKPaths;

import org.apache.zookeeper.CreateMode;

import org.apache.log4j.Logger;

import org.xudifsd.zk.WatcherWrapper;

import clojure.lang.IFn;
import clojure.lang.Keyword;
import clojure.lang.PersistentVector;
import clojure.lang.IPersistentVector;
import clojure.lang.IPersistentMap;

import java.util.List;
import java.util.ArrayList;

import static org.xudifsd.zk.Bridge.getStatMap;
import org.xudifsd.zk.ConnectionStateListenerWrapper;

public class ZkClient {
	private static final Logger logger = Logger.getLogger(ZkClient.class);
	private CuratorFramework client = null;

	public ZkClient(String connectString) throws Exception {
		client = CuratorFrameworkFactory.newClient(connectString, new ExponentialBackoffRetry(1000, 3));
		client.start();
	}

	public ZkClient(String connectString, IFn handler) throws Exception {
		this(connectString);
		client.getConnectionStateListenable().addListener(new ConnectionStateListenerWrapper(handler));
	}

	public ZkClient(String connectString, String namespace) throws Exception {
		this(connectString);
		client = client.usingNamespace(namespace);
	}

	public ZkClient(String connectString, String namespace, IFn handler) throws Exception {
		this(connectString, namespace);
		client.getConnectionStateListenable().addListener(new ConnectionStateListenerWrapper(handler));
	}

	public CuratorFramework getClient() {
		return this.client;
	}

	public String create(String path, String data) throws Exception {
		return client.create().creatingParentsIfNeeded().forPath(path, data.getBytes("UTF-8"));
	}

	// returns created path, curator's framwork will add GUID prefix
	public String create(String path, String data, Keyword mode) throws Exception {
		CreateMode cmode = null;
		String name = mode.getName();

		if (name.equals("EPHEMERAL"))
			cmode = CreateMode.EPHEMERAL;
		else if (name.equals("EPHEMERAL_SEQUENTIAL"))
			cmode = CreateMode.EPHEMERAL_SEQUENTIAL;
		else if (name.equals("PERSISTENT"))
			cmode = CreateMode.PERSISTENT;
		else if (name.equals("PERSISTENT_SEQUENTIAL"))
			cmode = CreateMode.PERSISTENT_SEQUENTIAL;
		else
			throw new RuntimeException("unknow create mode");

		return client.create().creatingParentsIfNeeded().
					withProtection().withMode(cmode).
					forPath(path, data.getBytes("UTF-8"));
	}

	private List<String> getFullPath(String path, List<String> prev) {
		List<String> result = new ArrayList<String>();
		for (int i = 0; i < prev.size(); i++)
			result.add(ZKPaths.makePath(path, prev.get(i)));
		return result;
	}

	public IPersistentVector getChildren(String path) throws Exception {
		List<String> result = client.getChildren().forPath(path);

		return PersistentVector.create(getFullPath(path, result));
	}

	public IPersistentVector getChildren(String path, IFn handler) throws Exception {
		WatcherWrapper watcher = new WatcherWrapper(client, handler, path,
											WatcherWrapper.WatcherType.GETCHILDREN);
		List<String> result = client.getChildren().usingWatcher(watcher).forPath(path);
		return PersistentVector.create(getFullPath(path, result));
	}

	public String getData(String path) throws Exception {
		return new String(client.getData().forPath(path), "UTF-8");
	}

	public String getData(String path, IFn handler) throws Exception {
		WatcherWrapper watcher = new WatcherWrapper(client, handler, path,
											WatcherWrapper.WatcherType.GETDATA);
		return new String(client.getData().usingWatcher(watcher).forPath(path), "UTF-8");
	}

	public IPersistentMap exists(String path) throws Exception {
		return getStatMap(client.checkExists().forPath(path));
	}

	public IPersistentMap exists(String path, IFn handler) throws Exception {
		WatcherWrapper watcher = new WatcherWrapper(client, handler, path,
											WatcherWrapper.WatcherType.EXISTS);
		return getStatMap(client.checkExists().usingWatcher(watcher).forPath(path));
	}

	public void delete(String path) throws Exception {
		client.delete().forPath(path);
	}

	public void delete(String path, int version) throws Exception {
		client.delete().withVersion(version).forPath(path);
	}

	public IPersistentMap setData(String path, String data) throws Exception {
		return getStatMap(client.setData().forPath(path, data.getBytes("UTF-8")));
	}

	public IPersistentMap setData(String path, String data, int version) throws Exception {
		return getStatMap(client.setData().withVersion(version).forPath(path, data.getBytes("UTF-8")));
	}
}
