package com.s8.stack.arch.tests.db.h2;

import java.io.IOException;
import java.nio.file.Path;

import com.s8.arch.magnesium.handlers.h2.H2ModelPrototype;
import com.s8.blocks.bytes.demos.d0.FatDemoFile02;
import com.s8.io.bytes.linked.LinkedByteInflow;
import com.s8.io.bytes.linked.LinkedByteOutflow;
import com.s8.io.bytes.linked.LinkedBytes;
import com.s8.io.bytes.linked.LinkedBytesIO;


/**
 * 
 * @author pierreconvert
 *
 */
public class H2DemoPrototype implements H2ModelPrototype<FatDemoFile02> {
	
	private Path path;
	
	public H2DemoPrototype(Path path) {
		super();
		this.path = path;
	}

	@Override
	public FatDemoFile02 load() throws IOException {
		LinkedBytes bytes = LinkedBytesIO.read(path, false);
		LinkedByteInflow inflow = new LinkedByteInflow(bytes);
		FatDemoFile02 model = FatDemoFile02.deserialize(inflow);
		return model;
	}

	@Override
	public void save(FatDemoFile02 model) throws IOException {
		LinkedByteOutflow outflow = new LinkedByteOutflow(2048);
		model.serialize(outflow);
		LinkedBytes bytes = outflow.getHead();
		LinkedBytesIO.write(bytes, path, false);
	}

	@Override
	public long getBytecount(FatDemoFile02 model) throws IOException {
		return 0;
	}
}
