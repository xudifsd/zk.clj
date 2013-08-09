package org.xudifsd.zk;

import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;

import static org.xudifsd.zk.Bridge.getConnectionStateString;

import clojure.lang.IFn;

public class ConnectionStateListenerWrapper implements ConnectionStateListener {
	private IFn handler;

	public ConnectionStateListenerWrapper(IFn handler) {
		this.handler = handler;
	}

	@Override
	public void stateChanged(CuratorFramework client, ConnectionState newState) {
		this.handler.invoke(getConnectionStateString(newState), client);
	}
}
