package com.provectus;

import com.provectus.impl.CalculatePi;

import java.math.BigDecimal;
import java.util.concurrent.*;

/**
 * @author nivanov, <a href="mailto:Nikita.Ivanov@returnonintelligence.com">Ivanov Nikita</a>
 * @since 08-Oct-17
 */
public class Main {
	public static void main(String... args){
		int digitsToFind = 5;
		if (args.length > 0) {
			digitsToFind = Integer.parseInt(args[0]);
		}

		Solution calculatePi = new CalculatePi();

		Future<BigDecimal> future = calculatePi.calculatePi(digitsToFind);
		try {
			BigDecimal result = future.get();
			System.out.println(result);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
}
