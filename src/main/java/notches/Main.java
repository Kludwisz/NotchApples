package notches;

import java.io.File;
import java.util.Map;
import java.util.Scanner;

/*
import com.seedfinding.mccore.rand.ChunkRand;
import com.seedfinding.mccore.util.math.Vec3i;
import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.mccore.util.pos.CPos;
import com.seedfinding.mccore.version.MCVersion;

import kludwisz.ancientcity.AncientCity;
import kludwisz.ancientcity.AncientCityGenerator;
import nl.kallestruik.noisesampler.NoiseSampler;
import nl.kallestruik.noisesampler.NoiseType;
import nl.kallestruik.noisesampler.minecraft.Dimension;
*/

public class Main {
	public static void main(String [] args) {
		// worldSeedTest();
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
/*
	public static void testRun() {
		FinderTestPackedArrays.find();
	}
	
	static final AncientCity CITY = new AncientCity(MCVersion.v1_19_2);
	private static void worldSeedTest() {
		long structseed = 12345124901L;
		CPos city = CITY.getInRegion(structseed, 0, 0, new ChunkRand());
		ACGen gen = new ACGen();
		gen.generate(structseed, city.getX(), city.getZ(), new ChunkRand());
		Vec3i centerOfFirstPiece = gen.pieces.get(0).box.getCenter();

		long worldseed;
		int counter = 0;
		int total = 1<<16;
		
		for (long up16=0; up16<(1L<<16); up16++) {
			worldseed = (up16<<48) | structseed;
			NoiseSampler sampler = new NoiseSampler(worldseed, Dimension.OVERWORLD);
			
 			Map<NoiseType, Double> noise = sampler.queryNoiseFromBlockPos(centerOfFirstPiece.getX(), -27 >> 2, centerOfFirstPiece.getZ(), NoiseType.EROSION, NoiseType.DEPTH);
			
			double D = noise.get(NoiseType.DEPTH);
	        double E = noise.get(NoiseType.EROSION);

	        double dD = 1.1 - D;
	        double dE = Math.max(E + 0.375, 0);
	        double dsD = Math.max(D - 1.0, 0);

	        if (dD * dD + dE * dE < dsD * dsD) {
	        	counter++;
	        	//System.out.println(worldseed + " /tp " + city.getX()*16 + " -32 " + city.getZ()*16);
	        }
		}
		
		double prob = (double)counter / (double)total;
		prob *= 100.0D;
		
		System.out.println("Deep dark: " + counter);
		System.out.println("Probability (%): " + prob);
	}
*/
}
