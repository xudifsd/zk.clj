package org.xudifsd.zk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.barriers.DistributedDoubleBarrier;

import clojure.lang.IFn;

public class Barrier {
	private DistributedDoubleBarrier barrier;

	public Barrier(CuratorFramework client, String barrierPath, int memberQty) {
		barrier = new DistributedDoubleBarrier(client, barrierPath, memberQty);
	}

	public Object goInto(IFn handler) throws Exception {
		barrier.enter();
		Object result = handler.invoke();
		barrier.leave();
		return result;
	}
}
