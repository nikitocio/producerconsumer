package com.provectus.impl;

import com.provectus.Solution;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.concurrent.*;

/**
 * @author nivanov, <a href="mailto:Nikita.Ivanov@returnonintelligence.com">Ivanov Nikita</a>
 * @since 08-Oct-17
 */
public class CalculatePi implements Solution {

	private BlockingQueue<BigDecimal> sharedQueue;
	private volatile boolean piCalculated = false;

	public Future<BigDecimal> calculatePi(int numberOfCorrectDigits) {
		ExecutorService es = Executors.newFixedThreadPool(2);

		sharedQueue = new LinkedBlockingQueue();
		es.submit(new Producer());
		Future<BigDecimal> future = es.submit(new Consumer(numberOfCorrectDigits));
		es.shutdown();
		return future;
	}

	/**
	 *  Consumer - inner class which take BigDecimals form shared queue,
	 *  summarize them and return result when accuracy achieved
	 */
	public class Consumer implements Callable<BigDecimal> {

		private int numberOfCorrectDigits;

		public Consumer(int numberOfCorrectDigits) {
			this.numberOfCorrectDigits = numberOfCorrectDigits;
		}

		public BigDecimal call() throws Exception {
			BigDecimal s1 = new BigDecimal(0);
			BigDecimal s2 = new BigDecimal(0);

			BigDecimal accuracy = new BigDecimal(Math.pow(10, -numberOfCorrectDigits));

			long i = 1;

			while(true) {
				try {

					BigDecimal taken = sharedQueue.take();
					System.out.println("Consumed: " + taken);

					if (i % 2 != 0) {
						s1 = s2.add(taken);
					} else {
						s2 = s1.subtract(taken);
						BigDecimal sub = s1.subtract(s2);
						if (sub.compareTo(accuracy) < 0) {
							break;
						}
					}
					i++;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			};

			piCalculated =  true;
			return s1.add(s2).divide(new BigDecimal(2)).setScale(numberOfCorrectDigits, BigDecimal.ROUND_DOWN);
		}
	}

	/**
	 *  Producer - inner class which generate BigDecimals and put them into shared queue
	 */
	public class Producer implements Runnable {
		public void run() {
			long i = 1;
			while(!piCalculated){
				try {
					if (sharedQueue.size() == 100){
						Thread.sleep(1);
					}
					BigDecimal iItem = new BigDecimal(4).divide(new BigDecimal(2 * i - 1),  MathContext.DECIMAL128);
					System.out.println("Produced: " + iItem);
					sharedQueue.put(iItem);
					i++;
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

}
