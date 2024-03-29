package com.s8.arch.magnesium.stores.m1.modules;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.s8.arch.magnesium.stores.m1.M1Store;
import com.s8.arch.silicon.watch.WatchTask;


/**
 * 
 * @author pierreconvert
 *
 * @param <M>
 */
public class Clock<M> {

	

	/**
	 * 
	 */
	private M1Store<M>.Facet store;
	
	
	
	/**
	 * 2^62-1
	 */
	public final static long CUT_OFF = 4611686018427387903L;
	
	private AtomicLong coreClock;
	
	private Lock lock;
	
	/**
	 * 
	 * @param store
	 */
	public Clock(M1Store<M>.Facet store) {
		super();
		this.store = store;
		this.coreClock = new AtomicLong(0);
		lock = new ReentrantLock();
	}
	
	
	/**
	 * 
	 */
	public long getTimestamp() {
		
		long t = coreClock.getAndIncrement();
		
		
		if(t > CUT_OFF) {
			t-= CUT_OFF;
			
			lock.lock();
			
			// re-check that clock has not already been shifted
			if(coreClock.get() > CUT_OFF) {
				
				coreClock.getAndUpdate(time -> time - CUT_OFF);
				
				store.getEngine().pushWatchTask(new WatchTask() {
					
					@Override
					public WatchTask run() {
						store.traverse((address, handler) -> {
							handler.timestamp -= CUT_OFF;
						});
						return null;
					}
					
					@Override
					public String describe() {
						return "shifting timestamps";
					}
				});	
			}
			lock.unlock();
		}
		return t;
	}
}
