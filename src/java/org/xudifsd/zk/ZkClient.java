package org.xudifsd.zk;

import org.apache.curator.CuratorZookeeperClient;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import org.xudifsd.zk.WatcherWrapper;

import clojure.lang.IFn;

import java.io.IOException;

public class ZkClient {
	private final long sessionTimeout = 180000;
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

	public void create(String path, String data) throws Exception {
		client.create().forPath(path, data.getBytes());
	}

	public void watch(IFn function, String path) throws Exception {
		client.getChildren().usingWatcher(new WatcherWrapper(client, function, path)).forPath(path);
	}
}
