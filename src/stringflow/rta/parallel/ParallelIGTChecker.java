package stringflow.rta.parallel;

import stringflow.rta.IGTState;
import stringflow.rta.encounterigt.EncounterIGTMap;

public class ParallelIGTChecker {
	
	private int numThreads;
	private boolean threadsRunning[];
	private ParallelIGTExecutor executor;
	private EncounterIGTMap buffer;
	
	public ParallelIGTChecker(int numThreads, ParallelIGTExecutor executor) {
		this.numThreads = numThreads;
		this.executor = executor;
		this.threadsRunning = new boolean[numThreads];
		this.buffer = new EncounterIGTMap();
	}
	
	// Most of this code is stolen from dabomstew. https://github.com/Dabomstew/gb-rta-bruteforce/blob/master/src/dabomstew/rta/mtmoon/LassIGT0Checker.java
	public void checkIGTFrame(IGTState state, String path, long flags) {
		boolean started = false;
		while(!started) {
			synchronized(threadsRunning) {
				int threadIndex = -1;
				for(int i = 0; i < numThreads; i++) {
					if(!threadsRunning[i]) {
						threadIndex = i;
						break;
					}
				}
				if(threadIndex >= 0) {
					started = true;
					final int num = threadIndex;
					threadsRunning[threadIndex] = true;
					Runnable run = () -> {
						EncounterIGTMap result = executor.checkIGTFrame(num, state, path, flags);
						synchronized(threadsRunning) {
							buffer.addAll(result);
							threadsRunning[num] = false;
						}
					};
					new Thread(run).start();
				}
			}
			if(!started) {
				try {
					Thread.sleep(1);
				} catch(InterruptedException e) {
				}
			}
		}
	}
	
	public EncounterIGTMap flush() {
		boolean done = false;
		while(!done) {
			synchronized(threadsRunning) {
				done = true;
				for(int i = 0; i < numThreads; i++) {
					if(threadsRunning[i]) {
						done = false;
						break;
					}
				}
			}
			if(!done) {
				try {
					Thread.sleep(1);
				} catch(InterruptedException e) {
				}
			}
		}
		EncounterIGTMap result = new EncounterIGTMap(buffer);
		buffer.clear();
		return result;
	}
}
