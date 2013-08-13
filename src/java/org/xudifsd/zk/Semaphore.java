package org.xudifsd.zk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreV2;
import org.apache.curator.framework.recipes.locks.Lease;

import clojure.lang.IFn;

public class Semaphore {
	private InterProcessSemaphoreV2 semaphore;

	public Semaphore(CuratorFramework client, String path, int maxLeases) {
		this.semaphore = new InterProcessSemaphoreV2(client, path, maxLeases);
	}

	public void goInto(IFn handler) throws Exception {
		Lease lease;
		lease = semaphore.acquire();
		handler.invoke();
		lease.close();
	}
}
