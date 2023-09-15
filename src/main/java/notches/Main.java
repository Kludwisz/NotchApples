package notches;

import java.io.File;
import java.util.Map;
import java.util.Scanner;

import com.seedfinding.mccore.util.pos.CPos;

import nl.kallestruik.noisesampler.NoiseSampler;
import nl.kallestruik.noisesampler.NoiseType;
import nl.kallestruik.noisesampler.minecraft.Dimension;

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

	public static void testRun() {
		FinderTestPackedArrays.find();
	}
	
	private static void worldSeedTest() {
		CPos city = new CPos(13, 37);
		
		long structseed = 12345678901L;
		long worldseed;
		int counter = 0;
		int total = 1<<16;
		
		for (long up16=0; up16<(1L<<16); up16++) {
			worldseed = (up16<<48) | structseed;
			NoiseSampler sampler = new NoiseSampler(worldseed, Dimension.OVERWORLD);
			
			Map<NoiseType, Double> noise = sampler.queryNoiseFromBlockPos(city.getX()<<4, 0, city.getZ()<<4, NoiseType.EROSION, NoiseType.DEPTH);
			double D = noise.get(NoiseType.DEPTH);
			if (D < 0.9d) continue;
			double E = noise.get(NoiseType.EROSION);
			if (E > -0.225d) continue;
			
			counter++;
		}
		
		double prob = (double)counter / (double)total;
		prob *= 100.0D;
		
		System.out.println("Deep dark: " + counter);
		System.out.println("Probability (%): " + prob);
	}
}
