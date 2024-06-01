package tukano.impl.rest.servers;

import org.glassfish.jersey.server.ResourceConfig;

import tukano.api.java.Blobs;
import tukano.impl.proxy.DropboxService;
import tukano.impl.rest.servers.utils.CustomLoggingFilter;
import tukano.impl.rest.servers.utils.GenericExceptionMapper;
import utils.Args;
import java.util.logging.Logger;


public class ProxyRestBlobsServer extends AbstractRestServer {
    
    public static final int PORT = 5679;
    private static Logger Log = Logger.getLogger(RestBlobsServer.class.getName());

    private DropboxService dropboxService;

    ProxyRestBlobsServer(int port, boolean clearState) {
        super(Log, Blobs.NAME, port);
        dropboxService = new DropboxService(super.serverURI);
        if (clearState) {
            clearDropboxState();
        }
    }

    @Override
    void registerResources(ResourceConfig config) {
        config.register(new RestBlobsResource(super.serverURI));
        config.register(new GenericExceptionMapper());
        config.register(new CustomLoggingFilter());
    }

    private void clearDropboxState() {
        try {
            dropboxService.deleteAllBlobs(serverURI, INETADDR_ANY);
        } catch (Exception e) {
            Log.severe("Failed to clear Dropbox state: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Args.use(args);
        boolean clearState = Boolean.parseBoolean(args[0]);
        new ProxyRestBlobsServer(Args.valueOf("-port", PORT), clearState).start();
    }
}
