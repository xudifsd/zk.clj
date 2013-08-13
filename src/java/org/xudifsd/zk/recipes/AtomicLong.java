package org.xudifsd.zk.recipes;

import org.apache.curator.framework.recipes.atomic.DistributedAtomicLong;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.RetryPolicy;

/**
 * If operation is not successed, we try again without wait.
 * ATTENTION, this class may generate a *lot* of network traffic
 * if there are many clients.
 * */
public class AtomicLong {
	private DistributedAtomicLong agent;

	public AtomicLong(CuratorFramework client, String counterPath) {
		this(client, counterPath, new ExponentialBackoffRetry(1000, 3));
	}

	public AtomicLong(CuratorFramework client, String counterPath, RetryPolicy policy) {
		agent = new DistributedAtomicLong(client, counterPath, policy);
	}

	// retry if failed
	public long add(long delta) throws Exception {
		while (true) {
			AtomicValue<Long> value = agent.add(delta);
			if (value.succeeded())
				return value.postValue();
		}
	}

	// retry if failed
	public long subtract(long delta) throws Exception {
		while (true) {
			AtomicValue<Long> value = agent.subtract(delta);
			if (value.succeeded())
				return value.postValue();
		}
	}

	public boolean compareAndSet(long expectedValue, long newValue) throws Exception {
		AtomicValue<Long> value = agent.compareAndSet(expectedValue, newValue);
		return value.succeeded();
	}

	public long get() throws Exception {
		return agent.get().postValue();//guaranteed to succeed
	}

	// retry if failed
	public long increment() throws Exception {
		while (true) {
			AtomicValue<Long> value = agent.increment();
			if (value.succeeded())
				return value.postValue();
		}
	}

	// retry if failed
	public long decrement() throws Exception {
		while (true) {
			AtomicValue<Long> value = agent.decrement();
			if (value.succeeded())
				return value.postValue();
		}
	}
}
