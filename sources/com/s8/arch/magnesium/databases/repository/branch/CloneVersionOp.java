package com.s8.arch.magnesium.databases.repository.branch;

import com.s8.arch.fluor.outputs.BranchExposureS8AsyncOutput;
import com.s8.arch.magnesium.callbacks.MgCallback;
import com.s8.arch.magnesium.handlers.h3.ConsumeResourceMgAsyncTask;
import com.s8.arch.magnesium.handlers.h3.H3MgHandler;
import com.s8.arch.magnesium.handlers.h3.RequestH3MgOperation;
import com.s8.arch.silicon.async.MthProfile;
import com.s8.io.bohr.neodymium.branch.NdBranch;
import com.s8.io.bohr.neodymium.exceptions.NdIOException;
import com.s8.io.bohr.neodymium.object.NdObject;

/**
 * 
 * @author pierreconvert
 *
 */
class CloneVersionOp extends RequestH3MgOperation<NdBranch> {


	public final MgBranchHandler branchHandler;

	public final long version;

	public final MgCallback<BranchExposureS8AsyncOutput> onSucceed;

	public final long options;


	/**
	 * 
	 * @param handler
	 * @param version
	 * @param onSucceed
	 * @param onFailed
	 */
	public CloneVersionOp(long timestamp, 
			MgBranchHandler handler, long version, MgCallback<BranchExposureS8AsyncOutput> onSucceed, long options) {
		super(timestamp);
		this.branchHandler = handler;
		this.version = version;
		this.onSucceed = onSucceed;
		this.options = options;
	}


	@Override
	public H3MgHandler<NdBranch> getHandler() {
		return branchHandler;
	}

	@Override
	public ConsumeResourceMgAsyncTask<NdBranch> createAsyncTask() {
		return new ConsumeResourceMgAsyncTask<NdBranch>(branchHandler) {


			@Override
			public MthProfile profile() { 
				return MthProfile.FX0; 
			}

			@Override
			public String describe() {
				return "CLONE-HEAD on "+branchHandler.getIdentifier()+" branch of "+branchHandler.getName()+ " repository";
			}

			@Override
			public boolean consumeResource(NdBranch branch) throws NdIOException {
				BranchExposureS8AsyncOutput output = new BranchExposureS8AsyncOutput();

				NdObject[] objects = branch.cloneVersion(version).exposure;
				output.objects = objects;

				onSucceed.call(output);
				return false;
			}

			@Override
			public void catchException(Exception exception) {
				BranchExposureS8AsyncOutput output = new BranchExposureS8AsyncOutput();
				output.reportException(exception);
				onSucceed.call(output);
			}			
		};
	}

}