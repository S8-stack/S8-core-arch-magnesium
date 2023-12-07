package com.s8.core.arch.magnesium.databases.repository.store;

import java.io.IOException;

import com.s8.api.flow.S8User;
import com.s8.api.flow.repository.requests.ForkRepositoryS8Request;
import com.s8.api.flow.repository.requests.ForkRepositoryS8Request.Status;
import com.s8.core.arch.magnesium.databases.DbMgCallback;
import com.s8.core.arch.magnesium.databases.RequestDbMgOperation;
import com.s8.core.arch.magnesium.databases.repository.entry.MgRepositoryHandler;
import com.s8.core.arch.magnesium.handlers.h3.ConsumeResourceMgAsyncTask;
import com.s8.core.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.core.arch.silicon.async.MthProfile;
import com.s8.core.io.json.types.JSON_CompilingException;

/**
 * 
 * @author pierreconvert
 *
 */
class ForkRepoOp extends RequestDbMgOperation<RepoMgStore> {


	/**
	 * 
	 */
	public final RepoMgDatabase storeHandler;

	
	/**
	 * 
	 */
	public final ForkRepositoryS8Request request;



	/**
	 * 
	 * @param handler
	 * @param version
	 * @param onSucceed
	 * @param onFailed
	 */
	public ForkRepoOp(long timestamp, S8User initiator, DbMgCallback callback, RepoMgDatabase handler, 
			ForkRepositoryS8Request request) {
		super(timestamp, initiator, callback);

		/* fields */
		this.storeHandler = handler;
		this.request = request;
	}


	@Override
	public H3MgHandler<RepoMgStore> getHandler() {
		return storeHandler;
	}


	@Override
	public ConsumeResourceMgAsyncTask<RepoMgStore> createAsyncTask() {
		return new ConsumeResourceMgAsyncTask<RepoMgStore>(storeHandler) {


			@Override
			public MthProfile profile() { 
				return MthProfile.FX0; 
			}

			@Override
			public String describe() {
				return "CREATE-REPO for "+handler.getName()+ " repository";
			}

			@Override
			public boolean consumeResource(RepoMgStore store) throws JSON_CompilingException, IOException {

				MgRepositoryHandler originRepoHandler = store.getRepositoryHandler(request.originRepoAddress);
				if(originRepoHandler != null) {
					
					MgRepositoryHandler targetRepoHandler = store.createRepositoryHandler(request.targetRepositoryAddress);
					if(targetRepoHandler != null) {
						originRepoHandler.forkRepo(timeStamp, initiator, callback, targetRepoHandler, request);
					}	
					else {
						request.onResponded(Status.TARGET_REPO_ADDRESS_CONFLICT, 0x0L);
						callback.call();
					}
				}
				else {
					request.onResponded(Status.ORIGIN_REPOSITORY_DOES_NOT_EXIST, 0x0L);
					callback.call();
				}
				return false;
			}

			@Override
			public void catchException(Exception exception) {
				request.onFailed(exception);
				callback.call();
			}
		};
	}

}
