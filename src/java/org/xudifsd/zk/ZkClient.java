package org.xudifsd.zk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import org.apache.zookeeper.CreateMode;

import org.xudifsd.zk.WatcherWrapper;

import clojure.lang.IFn;
import clojure.lang.Keyword;
import clojure.lang.PersistentVector;
import clojure.lang.IPersistentVector;

public class ZkClient {
	private CuratorFramework client = null;

	public ZkClient(String connectString) throws Exception {
		client = CuratorFrameworkFactory.newClient(connectString, new ExponentialBackoffRetry(1000, 3));
		client.start();
	}

	public ZkClient(String connectString, String namespace) throws Exception {
		client = CuratorFrameworkFactory.newClient(connectString, new ExponentialBackoffRetry(1000, 3));
		client.start();
		client = client.usingNamespace(namespace);
	}

	// returns created path, curator's framwork will add GUID prefix
	public String create(String path, String data) throws Exception {
		return client.create().creatingParentsIfNeeded().forPath(path, data.getBytes());
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

		return client.create().creatingParentsIfNeeded().withProtection().withMode(cmode).forPath(path, data.getBytes());
	}

	public IPersistentVector getChildren(String path) throws Exception {
		return PersistentVector.create(client.getChildren().forPath(path));
	}

	public void watch(IFn function, String path) throws Exception {
		client.getChildren().usingWatcher(new WatcherWrapper(client, function, path)).forPath(path);
	}
}
