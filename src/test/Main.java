package test;

import java.util.concurrent.ExecutionException;

import util.NetworkScanner;

public class Main {

	public static void main(final String... args) {
		NetworkScanner scanner = new NetworkScanner("127.0.0.1", "127.0.0.3", 0, 60000);
		try {
			scanner.scan();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
}