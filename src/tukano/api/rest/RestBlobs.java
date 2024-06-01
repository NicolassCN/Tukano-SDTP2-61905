package tukano.api.rest;


import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path(RestBlobs.PATH)
public interface RestBlobs {
	
	public static final String TIMESTAMP = "timestamp";
	public static final String VERIFIER = "verifier";


	String PATH = "/blobs";
	String BLOB_ID = "blobId";
 
 	@POST
 	@Path("/{" + BLOB_ID +"}")
 	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	void upload(@PathParam(BLOB_ID) String blobId, @QueryParam(TIMESTAMP) long timestamp, @QueryParam(VERIFIER) String verifier, byte[] bytes);


 	@GET
 	@Path("/{" + BLOB_ID +"}") 	
 	@Produces(MediaType.APPLICATION_OCTET_STREAM)
 	byte[] download(@PathParam(BLOB_ID) String blobId, @QueryParam(TIMESTAMP) long timestamp, @QueryParam(VERIFIER) String verifier);
}
