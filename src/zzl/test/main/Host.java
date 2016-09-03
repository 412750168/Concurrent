package zzl.test.main;

import zzl.test.service.ThreadPool;

public class Host {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ThreadPool threadPool = ThreadPool.getIntance();
		//threadPool.executeNewFixThreadPool();
		//threadPool.executeSemaphore();
		//threadPool.executeReentrantLock();
		//threadPool.executeBlockingQueue();
		/*try {
			threadPool.executeCompletionService();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		//threadPool.executeCountDownLatch();
		//threadPool.executeCyclicBarrier();
		threadPool.executeFutureAndScheduledExecutorService();
	}

}
