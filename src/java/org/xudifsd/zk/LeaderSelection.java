package org.xudifsd.zk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.curator.framework.recipes.leader.LeaderLatch;

import static org.xudifsd.zk.Bridge.getParticipants;

import clojure.lang.IFn;
import clojure.lang.IPersistentVector;

public class LeaderSelection implements LeaderLatchListener {
	private LeaderLatch latch;
	private IFn handler;

	public LeaderSelection(CuratorFramework client, String path, String id, IFn handler) throws Exception {
		this.latch = new LeaderLatch(client, path, id);
		this.latch.addListener(this);
		this.handler = handler;
		this.latch.start();
	}

	@Override
	public void isLeader() {
		IPersistentVector participants;
		try {
			participants = getParticipants(latch.getParticipants());
		} catch (Exception e) {
			participants = null;
		}
		handler.invoke(true, participants);
	}

	@Override
	public void notLeader() {
		IPersistentVector participants;
		try {
			participants = getParticipants(latch.getParticipants());
		} catch (Exception e) {
			participants = null;
		}
		handler.invoke(false, participants);
	}
}
