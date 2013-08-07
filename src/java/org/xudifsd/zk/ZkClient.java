package org.xudifsd.zk;

import org.apache.curator.CuratorZookeeperClient;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.io.IOException;

public class ZkClient {
	private final long sessionTimeout = 180000;
	private CuratorFramework client = null;

	public ZkClient(String connectString) {
		try {
			client = CuratorFrameworkFactory.newClient(connectString, new ExponentialBackoffRetry(1000, 3));
		} catch (Exception e) {
			e.printStackTrace();
		}
		client.start();
	}
}
