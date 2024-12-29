package notches;

import com.seedfinding.mccore.rand.ChunkRand;
import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.mccore.util.pos.CPos;
import com.seedfinding.mccore.util.pos.RPos;
import com.seedfinding.mccore.version.MCVersion;
import com.seedfinding.mcfeature.loot.LootTable;
import com.seedfinding.mcmath.util.Mth;
import kludwisz.ancientcity.AncientCity;
import kludwisz.ancientcity.AncientCityGenerator;

import java.io.File;
import java.util.List;
import java.util.Scanner;

public class ChunkChecker {
    public static void findAnyLootSeed(List<Long> lootSeeds) {
        try {
            Scanner fin = new Scanner(new File("src/main/resources/decoSeedsNotches.txt"));

            while (fin.hasNextLong()) {
                long seed = fin.nextLong();
                int cx = fin.nextInt();
                int cz = fin.nextInt();

                //System.out.println("Processing seed " + seed + " at " + cx + ", " + cz);
                processSeed(seed, cx, cz, lootSeeds);
            }
        }
        catch(Exception e) {
            System.out.println("Couldn't find file");
        }
    }

    private static final MCVersion VERSION = MCVersion.v1_19_2;
    private static final AncientCity AC = new AncientCity(VERSION);
    private static final AncientCityGenerator gen = new AncientCityGenerator();
    private static final ChunkRand rand = new ChunkRand();

    private static void processSeed(long seed, int cx, int cz, List<Long> lootSeeds) {
        // generate ancient cities in all 9 neighboring regions
        RPos rCenter = new CPos(cx, cz).toRegionPos(AC.getSpacing());

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                int rx = rCenter.getX() + dx;
                int rz = rCenter.getZ() + dz;
                CPos city = AC.getInRegion(seed, rx, rz, rand);
                gen.generate(seed, city.getX(), city.getZ(), rand);

                for (var chest : gen.getChestsWithLootSeeds()) {
                    LootTable lt = chest.getSecond();
                    if (lt.lootPools.length != 2) continue; // not treasure chest
                    long lootseed = chest.getThird() & Mth.MASK_48;
                    if (lootSeeds.contains(lootseed) && Finder.ancientCityCanSpawn(seed, gen.pieces[0].box.getCenter())) {
                        BPos pos = chest.getFirst();
                        System.out.println("FOUND A SEED!!!!! " + seed + " /tp " + pos.getX() + " " + pos.getY() + " " + pos.getZ());
                    }
                }
            }
        }
    }
}
