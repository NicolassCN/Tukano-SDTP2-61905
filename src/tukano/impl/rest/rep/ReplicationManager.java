package tukano.impl.rest.rep;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import tukano.impl.zookeeper.Zookeeper;
import tukano.impl.zookeeper.LeaderElection;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

public class ReplicationManager {

    private static final Logger Log = Logger.getLogger(ReplicationManager.class.getName());

    private long sequenceNumber = 0;
    private final ConcurrentLinkedQueue<Operation> operationLog = new ConcurrentLinkedQueue<>();
    private final Zookeeper zookeeper;
    private final LeaderElection leaderElection;

    public ReplicationManager(String zkServers) throws Exception {
        this.zookeeper = new Zookeeper(zkServers);
        this.leaderElection = new LeaderElection(zookeeper);
        leaderElection.startElection();
    }

    public synchronized long getNextSequenceNumber() {
        return sequenceNumber++;
    }

    public void logOperation(Operation operation) {
        if (isLeader()) {
            operationLog.add(operation);
            try {
                String path = "/operations/" + operation.getSequenceNumber();
                zookeeper.createNode(path, operation.toBytes(), CreateMode.PERSISTENT);
            } catch (KeeperException | InterruptedException e) {
                Log.severe("Failed to log operation to ZooKeeper: " + e.getMessage());
            }
        }
    }

    public void replicateOperation(Operation operation) {
        if (!isLeader()) {
            operationLog.add(operation);
        }
    }

    public Operation[] getOperations() {
        return operationLog.toArray(new Operation[0]);
    }

    public boolean isLeader() {
        try {
            return leaderElection.isLeader();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static class Operation {
        private final long sequenceNumber;
        private final String methodName;
        private final Object[] parameters;

        public Operation(long sequenceNumber, String methodName, Object[] parameters) {
            this.sequenceNumber = sequenceNumber;
            this.methodName = methodName;
            this.parameters = parameters;
        }

        public long getSequenceNumber() {
            return sequenceNumber;
        }

        public String getMethodName() {
            return methodName;
        }

        public Object[] getParameters() {
            return parameters;
        }

        public byte[] toBytes() {
            try {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.writeValueAsBytes(this);
            } catch (IOException e) {
                throw new RuntimeException("Failed to serialize operation", e);
            }
        }

        public static Operation fromBytes(byte[] data) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(data, Operation.class);
            } catch (IOException e) {
                throw new RuntimeException("Failed to deserialize operation", e);
            }
        }
    }
}
