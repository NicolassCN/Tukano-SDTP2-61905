package tukano.impl.grpc.servers;

import com.google.protobuf.ByteString;

import io.grpc.ServerServiceDefinition;
import io.grpc.stub.StreamObserver;
import tukano.impl.grpc.generated_java.BlobsGrpc;
import tukano.impl.grpc.generated_java.BlobsProtoBuf.*;
import tukano.impl.api.java.ExtendedBlobs;
import tukano.impl.java.servers.JavaBlobs;

public class GrpcBlobsServerStub extends AbstractGrpcStub implements BlobsGrpc.AsyncService {

	ExtendedBlobs impl;

	public GrpcBlobsServerStub(String blobUrl) {
		this.impl = new JavaBlobs(blobUrl);
	}

	@Override
	public ServerServiceDefinition bindService() {
		return BlobsGrpc.bindService(this);
	}

	@Override
	public void upload(UploadArgs request, StreamObserver<UploadResult> responseObserver) {
		var split = request.getBlobId().split("[?=&]");
		var blobId = split[0];
		long timestamp = Long.parseLong(split[2]);
		var verifier = split[4];
		var res = impl.upload(blobId, timestamp, verifier, request.getData().toByteArray());
		if (!res.isOK())
			responseObserver.onError(errorCodeToStatus(res.error()));
		else {
			responseObserver.onNext(UploadResult.newBuilder().build());
			responseObserver.onCompleted();
		}
	}

	@Override
	public void download(DownloadArgs request, StreamObserver<DownloadResult> responseObserver) {
		var split = request.getBlobId().split("[?=&]");
		var blobId = split[0];
		long timestamp = Long.parseLong(split[2]);
		var verifier = split[4];
		var res = impl.downloadToSink(blobId, timestamp, verifier, (data) -> {
			responseObserver.onNext(DownloadResult.newBuilder().setChunk(ByteString.copyFrom(data)).build());
		});
		if (res.isOK())
			responseObserver.onCompleted();
		else
			responseObserver.onError(errorCodeToStatus(res.error()));
	}
	
	@Override
	public void delete(DeleteArgs request, StreamObserver<DeleteResult> responseObserver) {
		var res = impl.delete(request.getBlobId(), request.getToken());
		if (res.isOK()) {
			responseObserver.onNext(DeleteResult.newBuilder().build());
			responseObserver.onCompleted();
		}
		else
			responseObserver.onError(errorCodeToStatus(res.error()));

    }

	@Override
	public void deleteAllBlobs(DeleteAllBlobsArgs request, StreamObserver<DeleteAllBlobsResult> responseObserver) {
		var res = impl.deleteAllBlobs(request.getUserId(), request.getToken());
		if (res.isOK()) {
			responseObserver.onNext(DeleteAllBlobsResult.newBuilder().build());
			responseObserver.onCompleted();
		}
		else
			responseObserver.onError(errorCodeToStatus(res.error()));

    }
}
