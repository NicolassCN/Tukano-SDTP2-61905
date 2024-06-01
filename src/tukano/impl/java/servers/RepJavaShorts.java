package tukano.impl.java.servers;

import org.apache.zookeeper.Watcher;
import tukano.api.java.Result;
import tukano.impl.rest.rep.ReplicationManager;

public class RepJavaShorts extends JavaShorts {

    private final ReplicationManager rep;

    public RepJavaShorts(ReplicationManager rep) {
        super();
        this.rep = rep;
        //rep.getZookeeper().registerWatcher(this::handleReplicationEvent);
    }

    private void handleReplicationEvent(Watcher.Event event) {
        // if (event.getType() == Watcher.Event.EventType.NodeCreated && event.getPath().startsWith("/operations/")) {
        //     byte[] data = rep.getZookeeper().getData(event.getPath(), false, null);
        //     ReplicationManager.Operation operation = ReplicationManager.Operation.fromBytes(data);
        //     applyReplicatedOperation(operation);
        // }
    }

    private void applyReplicatedOperation(ReplicationManager.Operation operation) {
        String methodName = operation.getMethodName();
        Object[] parameters = operation.getParameters();

        switch (methodName) {
            case "createShort":
                String userId = (String) parameters[0];
                String password = (String) parameters[1];
                handleResult(createShort(userId, password));
                break;
            case "deleteShort":
                String shortId = (String) parameters[0];
                String delPassword = (String) parameters[1];
                handleResult(deleteShort(shortId, delPassword));
                break;
            case "follow":
                String userId1 = (String) parameters[0];
                String userId2 = (String) parameters[1];
                boolean isFollowing = (boolean) parameters[2];
                String folPassword = (String) parameters[3];
                handleResult(follow(userId1, userId2, isFollowing, folPassword));
                break;
            case "like":
                String likeShortId = (String) parameters[0];
                String likeUserId = (String) parameters[1];
                boolean isLiked = (boolean) parameters[2];
                String likePassword = (String) parameters[3];
                handleResult(like(likeShortId, likeUserId, isLiked, likePassword));
                break;
            default:
                throw new IllegalArgumentException("Unknown operation: " + methodName);
        }
    }

    private void handleResult(Result<?> result) {
        if (result.error() != null) {
            throw new RuntimeException("Failed to apply replicated operation: " + result.error());
        }
    }
}
