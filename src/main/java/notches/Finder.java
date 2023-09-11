package notches;

import java.util.ArrayList;
import java.util.HashMap;
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

public class Finder {
	static final long MAX16 = 1L << 16;
	static final ChunkRand rand = new ChunkRand();
	static final DecoratorRand decoRand = new DecoratorRand();
	static final AncientCity CITY = new AncientCity(MCVersion.v1_19_2);
	static AncientCityGenerator gen;
	public static int targetNotchCount; // set in main

	public static void find(long seedMin, long seedMax) {
		AncientCityChest.compute();
		for (long i = seedMin; i <= seedMax; i++) {
			processLoot(i);
		}
	}
	
	private static void processLoot(long structseed) {
		CPos cityChunk = CITY.getInRegion(structseed, 0, 0, rand);
		gen = new AncientCityGenerator();
		gen.generate(structseed, cityChunk.getX(), cityChunk.getZ(), rand);
		
		List<Pair<BPos, LootTable>> chests = gen.getChests();
		HashMap<CPos, ArrayList<Boolean>> treasureChestCallMap = new HashMap<>(); 
		
		for (Pair<BPos, LootTable> chest : chests) {
			CPos chestChunk = chest.getFirst().toChunkPos();
			treasureChestCallMap.putIfAbsent(chestChunk, new ArrayList<Boolean>());
			treasureChestCallMap.get(chestChunk).add(chest.getSecond().lootPools.length == 2);
		}
		
		ArrayList< Pair<CPos, List<Boolean>> > chestChunks = new ArrayList<>();
		for (CPos key : treasureChestCallMap.keySet()) {
			chestChunks.add(new Pair<>(key, treasureChestCallMap.get(key)));
		}
		
		long worldseed;
		CPos cp;
		
		for (long up16=0; up16!=MAX16; up16++) {	
			worldseed = (up16<<48) | structseed;
			
			for (Pair<CPos, List<Boolean>> chestchunk : chestChunks) {
				cp = chestchunk.getFirst();
				long popseed = decoRand.getPopulationSeed(worldseed, cp.getX()<<4, cp.getZ()<<4);
				decoRand.setDecoratorSeed(popseed, 0, 7);
				
				for (boolean isTreasure : chestchunk.getSecond()) {
					long lootseed = decoRand.nextLong();
					if (isTreasure) {
						int notchcount = AncientCityChest.getNotches(lootseed);
						if (notchcount >= 12) {
							checkWorldSeed(worldseed, cityChunk, cp);
							return;
						}
					}
				}
			}
		}
	}
	
	private static void checkWorldSeed(long worldseed, CPos city, CPos chestchunkpos) {
		NoiseSampler sampler = new NoiseSampler(worldseed, Dimension.OVERWORLD);
		
		Map<NoiseType, Double> noise = sampler.queryNoiseFromBlockPos(city.getX()<<4, 0, city.getZ()<<4, NoiseType.EROSION, NoiseType.DEPTH);
		double D = noise.get(NoiseType.DEPTH);
		if (D < 0.9d) return;
		double E = noise.get(NoiseType.EROSION);
		if (E > -0.225d) return;
		
		// found a good worldseed
		System.out.println(worldseed + " " + chestchunkpos.getX() + " " + chestchunkpos.getZ());
		
		//try {
		//	FileWriter fout = new FileWriter(new File(""), true);
		//	fout.append(Long.toString(worldseed) + " " + chestchunkpos.toBlockPos(-50).toString() + "\n");
		//	fout.close();
		//} catch (IOException ex) {}
	}
}
