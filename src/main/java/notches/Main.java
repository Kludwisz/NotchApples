package notches;

import java.io.File;
import java.util.Scanner;

public class Main {
	public static void main(String [] args) {
		// testRun();
		runMicroboincApp(args);
	}
	
	public static void runMicroboincApp(String [] args) {
		File inputFile = null;
        for (int i = 0; i < args.length; ++i) {
            if (args[i].contains("--input")) {
                inputFile = new File(args[i + 1]);
            }
        }
        if (inputFile == null) {
            System.exit(1);
        }
        try {
            final Scanner scanner = new Scanner(inputFile);
            while (scanner.hasNextLong()) {
                final long seedMin = scanner.nextLong();
                final long seedMax = scanner.nextLong();
                Finder.targetNotchCount = 12;
                Finder.find(seedMin, seedMax);
            }
            scanner.close();
        }
        catch (Exception e) {
            System.out.println(e);
            System.exit(2);
        }
        System.out.flush();
	}

	public static void testRun() {
		FinderTest.find();
	}
}
