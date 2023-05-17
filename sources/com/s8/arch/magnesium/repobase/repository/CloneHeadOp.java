package com.s8.arch.magnesium.repobase.repository;

import java.io.IOException;

import com.s8.arch.magnesium.core.callbacks.ExceptionMgCallback;
import com.s8.arch.magnesium.core.callbacks.ObjectsMgCallback;
import com.s8.arch.magnesium.handlers.h3.CatchExceptionMgTask;
import com.s8.arch.magnesium.handlers.h3.ConsumeResourceMgTask;
import com.s8.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.arch.magnesium.handlers.h3.UserH3MgOperation;
import com.s8.arch.magnesium.repobase.branch.MgBranchHandler;
import com.s8.arch.silicon.async.MthProfile;

/**
 * 
 * @author pierreconvert
 *
 */
class CloneHeadOp extends UserH3MgOperation<MgRepository> {


	@Override
	public boolean isModifyingResource() {
		return false;
	}
	
	
	
	public final MgRepositoryHandler handler;
	
	public final String branchId;
	
	public final ObjectsMgCallback onSucceed;
	
	public final ExceptionMgCallback onFailed;

	
	/**
	 * 
	 * @param handler
	 * @param onSucceed
	 * @param onFailed
	 */
	public CloneHeadOp(long timestamp, MgRepositoryHandler handler, String branchId, ObjectsMgCallback onSucceed, ExceptionMgCallback onFailed) {
		super(timestamp);
		this.handler = handler;
		this.branchId = branchId;
		this.onSucceed = onSucceed;
		this.onFailed = onFailed;
	}
	

	@Override
	public ConsumeResourceMgTask<MgRepository> createConsumeResourceTask(MgRepository repo) {
		return new ConsumeResourceMgTask<MgRepository>(repo) {

			@Override
			public H3MgHandler<MgRepository> getHandler() {
				return handler;
			}
			
			@Override
			public MthProfile profile() { 
				return MthProfile.FX0; 
			}

			@Override
			public String describe() {
				return "CLONE-HEAD on "+branchId+" branch of "+handler.getName()+ " repository";
			}

			@Override
			public void consumeResource(MgRepository repository) {
				try {
					MgBranchHandler branchHandler = repository.branchHandlers.get(branchId);
					if(branchHandler == null) {
						throw new IOException("No branch "+branchId+" for repo "+repository.address);
					}
					branchHandler.cloneHead(timeStamp, onSucceed, onFailed);
				}
				catch(Exception exception) { onFailed.call(exception); }
			}

			
		};
	}

	@Override
	public CatchExceptionMgTask createCatchExceptionTask(Exception exception) { 
		return new CatchExceptionMgTask(exception) {

			@Override
			public MthProfile profile() { 
				return MthProfile.FX0; 
			}

			@Override
			public String describe() {
				return "failed to access resource on "+handler.getName()+": catching exception";
			}

			@Override
			public void catchException(Exception exception) {
				onFailed.call(exception);
			}
		};
	}


	
}
