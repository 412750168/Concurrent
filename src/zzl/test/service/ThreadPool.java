package zzl.test.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.*;

public class ThreadPool {

	private static ThreadPool threadPool;

	private ThreadPool() {

	}

	public static ThreadPool getIntance() {
		if (threadPool == null) {
			threadPool = new ThreadPool();
			return threadPool;
		}
		return threadPool;
	}

	private class MyExecutorThrad extends Thread {
		private int index;

		public MyExecutorThrad(int index) {
			super();
			this.index = index;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			System.out.println("fixedThreadPool" + "[" + this.index + "] start ...");
			try {
				Thread.sleep((int) (Math.random() * 10000));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("fixedThreadPool" + "[" + this.index + "] end ...");
		}

	}

	private class MySemaphore extends Thread {
		private Semaphore semaphore;
		private int id;

		public MySemaphore(Semaphore semaphore, int id) {
			super();
			this.semaphore = semaphore;
			this.id = id;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub

			try {
				synchronized (semaphore) {
					if (semaphore.availablePermits() > 0)
						System.out.println("customer:[" + this.id + "]进入厕所" + "有空位");
					else
						System.out.println("customer:[" + this.id + "]进入厕所，没有空位，排位");

				}
				semaphore.acquire();
				System.out.println("customer:[" + this.id + "]获得坑位");
				Thread.sleep((int) (Math.random() * 10000));
				System.out.println("customer:[" + this.id + "]使用完毕");

				semaphore.release();

			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}

	}

	private class TestReentrantLock {
		private ReentrantLock lock = new ReentrantLock();

		public void print(int str) {
			try {
				lock.lock();
				System.out.println(str + "获得");
				Thread.sleep((int) (Math.random() * 1000));
			} catch (Exception exception) {
				exception.printStackTrace();
			} finally {
				System.out.println(str + "释放");
				lock.unlock();
			}
		}
	}

	private class MyReentranLock extends Thread {
		private TestReentrantLock testReentrantLock;
		private int id;

		public MyReentranLock(TestReentrantLock testReentrantLock, int id) {
			super();
			this.testReentrantLock = testReentrantLock;
			this.id = id;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			testReentrantLock.print(id);
		}

	}

	public static class MyBlockQueue extends Thread {
		public static BlockingQueue<String> queue = new LinkedBlockingDeque<String>(3);
		private int index;
		private Scanner scanner;

		public MyBlockQueue() {
			super();
			scanner = new Scanner(System.in);
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (true) {
				try {
					index = scanner.nextInt();
					queue.put(String.valueOf(index));
					System.out.println("[" + index + "] in queue!!");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	public class MyCompletionService implements Callable<String> {

		private int id;

		public MyCompletionService(int id) {
			super();
			this.id = id;
		}

		@Override
		public String call() throws Exception {
			// TODO Auto-generated method stub
			Integer time = (int) (Math.random() * 10000);
			try {
				System.out.println(this.id + " start!");
				Thread.sleep(time);
				System.out.println(this.id + " end!");

			} catch (Exception e) {
				e.printStackTrace();
			}
			return id + ":" + time;
		}

	}
	////////////////////////////////////////////////////////////////////

	public static class TestCyclicBarrier {
		// 徒步需要的时间: Shenzhen, Guangzhou, Shaoguan, Changsha, Wuhan
		public static int[] timeWalk = { 5, 8, 15, 15, 10 };
		// 自驾游
		public static int[] timeSelf = { 1, 3, 4, 4, 5 };
		// 旅游大巴
		public static int[] timeBus = { 2, 4, 6, 6, 7 };

		static String now() {
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			return sdf.format(new Date()) + ": ";
		}

		static class Tour implements Runnable {
			private int[] times;
			private CyclicBarrier barrier;
			private String tourName;

			public Tour(CyclicBarrier barrier, String tourName, int[] times) {
				this.times = times;
				this.tourName = tourName;
				this.barrier = barrier;
			}

			public void run() {
				try {
					Thread.sleep(times[0] * 1000);
					System.out.println(now() + tourName + " Reached Shenzhen");
					barrier.await();
					Thread.sleep(times[1] * 1000);
					System.out.println(now() + tourName + " Reached Guangzhou");
					barrier.await();
					Thread.sleep(times[2] * 1000);
					System.out.println(now() + tourName + " Reached Shaoguan");
					barrier.await();
					Thread.sleep(times[3] * 1000);
					System.out.println(now() + tourName + " Reached Changsha");
					barrier.await();
					Thread.sleep(times[4] * 1000);
					System.out.println(now() + tourName + " Reached Wuhan");
					barrier.await();
				} catch (InterruptedException e) {
				} catch (BrokenBarrierException e) {
				}
			}
		}
	}

	public void executeNewFixThreadPool() {
		ExecutorService service = Executors.newFixedThreadPool(4);
		for (int i = 0; i < 10; i++) {
			service.execute(new MyExecutorThrad(i));
			System.out.println("MyExecutorThread:" + i);
		}
		service.shutdown();
		System.out.println("service shutdown");
	}

	public void executeSemaphore() {
		ExecutorService service = Executors.newCachedThreadPool();
		Semaphore semaphore = new Semaphore(2);
		for (int i = 0; i < 10; i++) {
			service.submit(new MySemaphore(semaphore, i));
		}
		service.shutdown();
		semaphore.acquireUninterruptibly();
		System.out.println("使用完毕，需要打扫");
		semaphore.release();
	}

	public void executeReentrantLock() {
		ExecutorService service = Executors.newCachedThreadPool();
		TestReentrantLock lock = new TestReentrantLock();
		for (int i = 0; i < 10; i++) {
			service.submit(new MyReentranLock(lock, i));
		}

		service.shutdown();
	}

	public void executeBlockingQueue() {
		ExecutorService service = Executors.newCachedThreadPool();
		service.submit(new MyBlockQueue());

		Thread thread = new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					while (true) {
						Thread.sleep((int) (Math.random() * 1000));
						// if(MyBlockQueue.queue.isEmpty())
						// break;
						String str = MyBlockQueue.queue.take();
						System.out.println(str + " has take!!");
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		};
		service.submit(thread);
		service.shutdown();
	}

	public void executeCompletionService() throws Exception {
		ExecutorService service = Executors.newCachedThreadPool();
		CompletionService<String> completionService = new ExecutorCompletionService<>(service);
		for (int i = 0; i < 10; i++) {
			completionService.submit(new MyCompletionService(i));
		}
		for (int i = 0; i < 10; i++)
			System.out.println(completionService.take().get());

		service.shutdown();
	}

	public void executeCountDownLatch() {

		final CountDownLatch begin = new CountDownLatch(1);
		final CountDownLatch end = new CountDownLatch(10);

		final ExecutorService service = Executors.newFixedThreadPool(10);

		for (int index = 0; index < 10; index++) {
			final int No = index + 1;
			Runnable runnable = new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						begin.await();
						Thread.sleep((int) (Math.random() * 10000));
						System.out.println("No." + No + " arrived!");
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						end.countDown();
					}
				}
			};
			service.submit(runnable);
		}

		System.out.println("Game start!!");
		begin.countDown();
		try {
			end.await();
			System.out.println("Game over!!");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		service.shutdown();
	}

	public void executeCyclicBarrier() {
		// 三个旅行团
		CyclicBarrier barrier = new CyclicBarrier(3);
		ExecutorService exec = Executors.newFixedThreadPool(3);
		exec.submit(new TestCyclicBarrier.Tour(barrier, "WalkTour", TestCyclicBarrier.timeWalk));
		exec.submit(new TestCyclicBarrier.Tour(barrier, "SelfTour", TestCyclicBarrier.timeSelf));
		// 当我们把下面的这段代码注释后，会发现，程序阻塞了，无法继续运行下去。
		exec.submit(new TestCyclicBarrier.Tour(barrier, "BusTour", TestCyclicBarrier.timeBus));
		exec.shutdown();
	}

	public void executeFutureAndScheduledExecutorService() {
		final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
		final Runnable beeper = new Runnable() {
			int count = 0;

			public void run() {
				System.out.println(new Date() + " beep " + (++count));
			}
		};
		// 1秒钟后运行，并每隔2秒运行一次
		final ScheduledFuture beeperHandle = scheduler.scheduleAtFixedRate(beeper, 1, 2, TimeUnit.SECONDS);
		// 2秒钟后运行，并每次在上次任务运行完后等待5秒后重新运行
		final ScheduledFuture beeperHandle2 = scheduler.scheduleWithFixedDelay(beeper, 2, 5, TimeUnit.SECONDS);
		// 30秒后结束关闭任务，并且关闭Scheduler
		scheduler.schedule(new Runnable() {
			public void run() {
				beeperHandle.cancel(true);
				beeperHandle2.cancel(true);
				scheduler.shutdown();
			}
		}, 30, TimeUnit.SECONDS);
	}

}
