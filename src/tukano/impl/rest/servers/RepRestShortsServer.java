package tukano.impl.rest.servers;

import java.util.logging.Logger;
import org.glassfish.jersey.server.ResourceConfig;
import tukano.api.java.Shorts;
import tukano.impl.rest.rep.ReplicationManager;
import tukano.impl.rest.servers.utils.CustomLoggingFilter;
import tukano.impl.rest.servers.utils.GenericExceptionMapper;
import tukano.impl.rest.servers.utils.VersionHeaderHandler;
import utils.Args;

public class RepRestShortsServer extends AbstractRestServer {
    public static final int PORT = 4568;

    private static Logger Log = Logger.getLogger(RestShortsServer.class.getName());

    private final ReplicationManager rep;

    RepRestShortsServer(ReplicationManager rep) {
        super(Log, Shorts.NAME, PORT);
        this.rep = rep;
    }

    @Override
    void registerResources(ResourceConfig config) {
        config.register(new RepRestShortsResource(rep));
        config.register(new GenericExceptionMapper());
        config.register(new CustomLoggingFilter());
        config.register(new VersionHeaderHandler());
    }

    public static void main(String[] args) {
        try {
            Args.use(args);
            ReplicationManager rep = new ReplicationManager(Shorts.NAME);
            new RepRestShortsServer(rep).start();
        } catch (Exception e) {
            Log.severe("Failed to start server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
