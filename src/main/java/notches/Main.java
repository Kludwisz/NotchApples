package notches;

import com.seedfinding.mcmath.util.Mth;
import com.seedfinding.mcseed.lcg.LCG;
import kludwisz.util.DecoratorRand;

import java.io.File;
import java.util.List;
import java.util.Scanner;


public class Main {
	public static void main(String [] args) throws Exception {
		// runBOINCApp(args);
        // /setblock -2753327 -48 12579947 minecraft:chest[facing=south,type=single,waterlogged=false]{LootTable:"minecraft:chests/ancient_city",LootTableSeed:286948066289951196L}
        //System.out.println((286948066289951196L ^ LCG.JAVA.multiplier) & Mth.MASK_48);

//        DecoratorRand drand = new DecoratorRand();
//        drand.setSeed(268686516099197L);
//        //drand.nextLong();
//        long lootseed = drand.nextLong() & Mth.MASK_48;
//        System.out.println(lootseed);
// 4563034899199
// 4563034899199
//
//        long pop = drand.getPopulationSeed(6583884573L, 1583303 << 4, 388056 << 4); //6583884573 1583303 388056
//        long dec = pop + 70000;
//        System.out.println(dec);

        // /setblock 16492072 -44 10191090 minecraft:chest[facing=west,type=single,waterlogged=false]{LootTable:"minecraft:chests/ancient_city",LootTableSeed:-4798017889974942977L}

        ChunkChecker.findAnyLootSeed(
                List.of(4563034899199L, 217704587079581L)
        );
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
