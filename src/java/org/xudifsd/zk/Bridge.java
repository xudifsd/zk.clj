package org.xudifsd.zk;

import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.recipes.leader.Participant;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collection;

import clojure.lang.PersistentHashMap;
import clojure.lang.IPersistentMap;
import clojure.lang.IPersistentVector;
import clojure.lang.PersistentVector;
import clojure.lang.Keyword;

public class Bridge {
	public static Keyword getKeyword(String s) {
		return Keyword.intern(s);
	}

	public static String getConnectionStateString(ConnectionState state) {
		String result = null;

		switch (state) {
		case CONNECTED:
			result = "CONNECTED";
			break;
		case LOST:
			result = "LOST";
			break;
		case READ_ONLY:
			result = "READ_ONLY";
			break;
		case RECONNECTED:
			result = "RECONNECTED";
			break;
		case SUSPENDED:
			result = "SUSPENDED";
			break;
		}
		return result;
	}

	public static String getTypeString(Watcher.Event.EventType type) {
		String result = null;

		switch (type) {
		case NodeChildrenChanged:
			result = "NodeChildrenChanged";
			break;
		case NodeCreated:
			result = "NodeCreated";
			break;
		case NodeDataChanged:
			result = "NodeDataChanged";
			break;
		case NodeDeleted:
			result = "NodeDeleted";
			break;
		}
		return result;
	}

	public static String getStateString(Watcher.Event.KeeperState state) {
		String result = null;

		switch (state) {
		case AuthFailed:
			result = "AuthFailed";
			break;
		case ConnectedReadOnly:
			result = "ConnectedReadOnly";
			break;
		case Disconnected:
			result = "Disconnected";
			break;
		case Expired:
			result = "Expired";
			break;
		case NoSyncConnected:
			result = "NoSyncConnected";
			break;
		case SaslAuthenticated:
			result = "SaslAuthenticated";
			break;
		case SyncConnected:
			result = "SyncConnected";
			break;
		}
		return result;
	}

	public static IPersistentMap getStatMap(Stat stat) {
		HashMap<Keyword, Long> map = new HashMap<Keyword, Long>();

		map.put(getKeyword("cZxid"), stat.getCzxid());
		map.put(getKeyword("ctime"), stat.getCtime());
		map.put(getKeyword("mZxid"), stat.getMzxid());
		map.put(getKeyword("mtime"), stat.getMtime());
		map.put(getKeyword("pZxid"), stat.getPzxid());
		map.put(getKeyword("cversion"), (long)stat.getCversion());
		map.put(getKeyword("dataVersion"), (long)stat.getVersion());
		map.put(getKeyword("aclVersion"), (long)stat.getAversion());
		map.put(getKeyword("ephemeralOwner"), stat.getEphemeralOwner());
		map.put(getKeyword("dataLength"), (long)stat.getDataLength());
		map.put(getKeyword("numChildren"), (long)stat.getNumChildren());

		return PersistentHashMap.create(map);
	}

	public static IPersistentMap getParticipant(Participant participant) {
		HashMap<Keyword, Object> map = new HashMap<Keyword, Object>();

		map.put(getKeyword("id"), participant.getId());
		map.put(getKeyword("is-leader"), participant.isLeader());

		return PersistentHashMap.create(map);
	}

	public static IPersistentVector getParticipants(Collection<Participant> participants) {
		ArrayList<IPersistentMap> list = new ArrayList<IPersistentMap>();

		Iterator<Participant> it = participants.iterator();

		while (it.hasNext())
			list.add(getParticipant(it.next()));

		return PersistentVector.create(list);
	}
}
