package notches;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.seedfinding.mccore.rand.ChunkRand;
import com.seedfinding.mccore.util.data.Pair;
import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.mccore.util.pos.CPos;
import com.seedfinding.mccore.version.MCVersion;
import com.seedfinding.mcfeature.loot.LootTable;

import kludwisz.ancientcity.AncientCity;
import kludwisz.ancientcity.AncientCityGenerator;
import kludwisz.util.DecoratorRand;
import nl.kallestruik.noisesampler.NoiseSampler;
import nl.kallestruik.noisesampler.NoiseType;
import nl.kallestruik.noisesampler.minecraft.Dimension;


public class FinderTestPackedArrays {

	static final long MAX16 = 1L << 16;
	static final ChunkRand rand = new ChunkRand();
	static final DecoratorRand decoRand = new DecoratorRand();
	static final AncientCity CITY = new AncientCity(MCVersion.v1_19_2);
	static final HashSet<Long> LOOKUP = new HashSet<>();
	static AncientCityGenerator gen;

	final static int TESTSIZE = 500;
	public static void find() {
		AncientCityChest.compute();
		rand.setSeed(53742121497L);
		for (int i=0; i<1000; i++) {
			LOOKUP.add(rand.nextLong());
		}

		for (int i=0; i<TESTSIZE; i++) {
			processLoot(i);
		}
		
		avgT1 /= TESTSIZE;
		avgT2 /= TESTSIZE;
		avgT3 /= TESTSIZE;
		avgC /= TESTSIZE;
		
		System.out.println("Average time taken by different processes: ");
		System.out.println("Generation: " + avgT1 + " ms");
		System.out.println("Data processing: " + avgT2 + " ms");
		System.out.println("Worldseed searching: " + avgT3 + " ms");
		System.out.println("Chests per structseed: " + avgC);
	}

	private static long avgT1 = 0;
	private static long avgT2 = 0;
	private static long avgT3 = 0;
	private static long avgC = 0;
	
	private static void processLoot(long structseed) {
		//System.out.println(structseed);
		long point1 = System.nanoTime();
		
		CPos cityChunk = CITY.getInRegion(structseed, 0, 0, rand);
		gen = new AncientCityGenerator();
		gen.generate(structseed, cityChunk.getX(), cityChunk.getZ(), rand);
		
		long point2 = System.nanoTime();
		
		List<Pair<BPos, LootTable>> chests = gen.getChests();
		HashMap<CPos, Integer> treasureChestCallMap = new HashMap<>(); 
		
		for (Pair<BPos, LootTable> chest : chests) {
			CPos chestChunk = chest.getFirst().toChunkPos();
			treasureChestCallMap.putIfAbsent(chestChunk, 0);
			int val = treasureChestCallMap.get(chestChunk);
			val <<= 1;
			val |= chest.getSecond().lootPools.length==2 ? 1 : 0;
			treasureChestCallMap.replace(chestChunk, val);
		}
		
		ArrayList<Pair<CPos, Integer>> chestChunks = new ArrayList<>();
		for (CPos key : treasureChestCallMap.keySet()) {
			chestChunks.add(new Pair<>(key, treasureChestCallMap.get(key)));
		}
		
		long point3 = System.nanoTime();
		
		long worldseed;
		CPos cp;
		int checked = 0;
		int encodedChests;
		int chestIsTreasure;
		
		for (long up16=0; up16!=MAX16; up16++) {	
			worldseed = (up16<<48) | structseed;
			
			for (Pair<CPos, Integer> chestchunk : chestChunks) {
				cp = chestchunk.getFirst();
				long popseed = decoRand.getPopulationSeed(worldseed, cp.getX()<<4, cp.getZ()<<4);
				decoRand.setDecoratorSeed(popseed, 0, 7);
				
				encodedChests = chestchunk.getSecond();
				while (encodedChests != 0) {
					chestIsTreasure = encodedChests & 1;
					encodedChests >>= 1;
					long lootseed = decoRand.nextLong();
					checked++;
					
					if (chestIsTreasure == 1) {
						//int notchcount = AncientCityChest.getNotches(lootseed);
						//if (notchcount >= 12) {
						//if (lootseed == 1234567L) {
						if (LOOKUP.contains(lootseed)) {
							checkWorldSeed(worldseed, cityChunk, cp);
							return;
						}
						//}
						//}
					}
				}
			}
		}
		
		long point4 = System.nanoTime();
		
		avgT1 += (point2-point1)/1000000;
		avgT2 += (point3-point2)/1000000;
		avgT3 += (point4-point3)/1000000;
		avgC += checked;
	}
	
	private static void checkWorldSeed(long worldseed, CPos city, CPos chest) {
		NoiseSampler sampler = new NoiseSampler(worldseed, Dimension.OVERWORLD);
		
		Map<NoiseType, Double> noise = sampler.queryNoiseFromBlockPos(city.getX()<<4, 0, city.getZ()<<4, NoiseType.EROSION, NoiseType.DEPTH);
		double D = noise.get(NoiseType.DEPTH);
		if (D < 0.9d) return;
		double E = noise.get(NoiseType.EROSION);
		if (E > -0.225d) return;
		
		System.out.println("\nOver 10 Notches: " + " ->  " + worldseed + " @ " + chest.toBlockPos(-50));
	}
}