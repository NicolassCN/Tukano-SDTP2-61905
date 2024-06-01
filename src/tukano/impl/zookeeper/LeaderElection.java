package tukano.impl.zookeeper;

import org.apache.zookeeper.*;
import java.util.Collections;
import java.util.List;

public class LeaderElection implements Watcher {

    private Zookeeper zk;
    private String root = "/election";
    private String myZnode;

    public LeaderElection(Zookeeper zk) {
        this.zk = zk;
    }

    @Override
    public void process(WatchedEvent event) {
        if (event.getType() == Event.EventType.NodeChildrenChanged) {
            try {
                electLeader();
            } catch (KeeperException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void electLeader() throws KeeperException, InterruptedException {
        List<String> children = zk.getChildren(root);
        Collections.sort(children);
        if (myZnode.equals(root + "/" + children.get(0))) {
            System.out.println("I am the leader");
        } else {
            String watchNode = root + "/" + children.get(Collections.binarySearch(children, myZnode.substring(myZnode.lastIndexOf("/") + 1)) - 1);
            zk.client().exists(watchNode, this);
            System.out.println("I am not the leader, watching " + watchNode);
        }
    }

    public void startElection() throws KeeperException, InterruptedException {
        myZnode = zk.client().create(root + "/c_", new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        electLeader();
    }

    public boolean isLeader() {
        try {
            List<String> children = zk.getChildren(root);
            Collections.sort(children);
            return myZnode.equals(root + "/" + children.get(0));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
