package notches;

import java.io.File;
import java.util.Scanner;


public class Main {
	public static void main(String [] args) throws Exception {
		runBOINCApp(args);
	}

    private static void runBOINCApp(String [] args) throws Exception {
        long rangeMinInclusive = 0L;
        long rangeMaxExclusive = 0L;

        // The range for this project could technically be the full 2^48 structure seed range,
        // but that would be infeasible to run, the finder is just too slow.
        // A range like 2^36 should be enough to find world seeds that generate both chests.

        // TODO add all the boinc stuff

        Finder.find(rangeMinInclusive, rangeMaxExclusive);
    }

    // ------------------------------------------------------------

    @Deprecated
	private static void runMicroboincApp(String [] args) {
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
}
