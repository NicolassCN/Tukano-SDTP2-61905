package tukano.impl.zookeeper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

public class Zookeeper implements Watcher {

    private static Logger Log = Logger.getLogger(Zookeeper.class.getName());

    private ZooKeeper _client;
    private final int TIMEOUT = 5000;

    public Zookeeper(String servers) throws Exception {
        this.connect(servers, TIMEOUT);
    }

    public synchronized ZooKeeper client() {
        if (_client == null || !_client.getState().equals(ZooKeeper.States.CONNECTED)) {
            throw new IllegalStateException("ZooKeeper is not connected.");
        }
        return _client;
    }

    public void registerWatcher(Watcher w) {
        client().register(w);
    }

    private void connect(String host, int timeout) throws IOException, InterruptedException {
        var connectedSignal = new CountDownLatch(1);
        _client = new ZooKeeper(host, TIMEOUT, (e) -> {
            if (e.getState().equals(Watcher.Event.KeeperState.SyncConnected)) {
                connectedSignal.countDown();
            }
        });
        connectedSignal.await();
    }

    public String createNode(String path, byte[] data, CreateMode mode) throws KeeperException, InterruptedException{
        try {
            return client().create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, mode);
        } catch (KeeperException.NodeExistsException x) {
            return path;
        } catch (Exception x) {
            throw new RuntimeException(x);
        }
    }

    public List<String> getChildren(String path) {
        try {
            return client().getChildren(path, false);
        } catch (Exception x) {
            throw new RuntimeException(x);
        }
    }

    public List<String> getAndWatchChildren(String path) {
        try {
            return client().getChildren(path, this);
        } catch (Exception x) {
            throw new RuntimeException(x);
        }
    }

    public boolean exists(String path, boolean watch) {
        try {
            return client().exists(path, watch) != null;
        } catch (Exception x) {
            throw new RuntimeException(x);
        }
    }

    @Override
    public void process(WatchedEvent event) {
        if (event.getType() == EventType.NodeChildrenChanged) {
            var path = event.getPath();
            Log.info("Got a path changed event:" + path);
            Log.info("Updated children:" + getChildren(path));
            getAndWatchChildren(path);
        }
    }
}
