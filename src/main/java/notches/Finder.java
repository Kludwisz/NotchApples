package notches;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.seedfinding.mccore.rand.ChunkRand;
import com.seedfinding.mccore.util.data.Pair;
import com.seedfinding.mccore.util.math.Vec3i;
import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.mccore.util.pos.CPos;
import com.seedfinding.mccore.version.MCVersion;
import com.seedfinding.mcfeature.loot.LootTable;
import com.seedfinding.mcmath.util.Mth;

import kludwisz.ancientcity.AncientCity;
import kludwisz.ancientcity.AncientCityGenerator;
import kludwisz.util.DecoratorRand;
import nl.kallestruik.noisesampler.NoiseSampler;
import nl.kallestruik.noisesampler.NoiseType;
import nl.kallestruik.noisesampler.minecraft.Dimension;

public class Finder {
	// The target lootseeds:
	public static final long LOOT_SEED_14_NOTCH = 217704587079581L;
	public static final long LOOT_SEED_27_POTIONS = 4563034899199L;
	// ------------------------------------------------------------

	private static final long MAX16 = 1L << 16;
	private static final long MASK48 = Mth.MASK_48;
	private static final ChunkRand rand = new ChunkRand();
	private static final DecoratorRand decoRand = new DecoratorRand();
	private static final AncientCity CITY = new AncientCity(MCVersion.v1_19_2);
	private static final AncientCityGenerator gen = new AncientCityGenerator();;

	public static void find(long seedMinIncl, long seedMaxExcl) {
		for (long i = seedMinIncl; i < seedMaxExcl; i++) {
			processLoot(i);
		}
	}
	
	private static void processLoot(long structseed) {
		CPos cityChunk = CITY.getInRegion(structseed, 0, 0, rand);
		gen.generate(structseed, cityChunk.getX(), cityChunk.getZ(), rand);
		
		List<Pair<BPos, LootTable>> chests = gen.getChests();
		HashMap<CPos, Long> treasureChestCallMap = new HashMap<>(); 
		
		// filling the long in reverse order so that the first chest is in the 2 least significant bits
		for (int i = chests.size() - 1; i >= 0; i--) {
			Pair<BPos, LootTable> chest = chests.get(i);
			CPos chestChunk = chest.getFirst().toChunkPos();
			treasureChestCallMap.putIfAbsent(chestChunk, 0L);
			long val = treasureChestCallMap.get(chestChunk);
			val <<= 2; // there shouldn't be any overflow, there's likely no seed with 32 chests in a single chunk

			// 2L = normal chest, 3L = ice box chest
			long type = chest.getSecond().lootPools.length==2 ? 2L : 3L;
			
			// we don't care about ice box chests that exist at the end of the sequence,
			// the following line allows to skip them
			if (val==0L && type==3L) continue;
			
			val |= type;
			treasureChestCallMap.replace(chestChunk, val);
		}
		
		ArrayList<Pair<CPos, Long>> chestChunks = new ArrayList<>();
		for (CPos key : treasureChestCallMap.keySet()) {
			if (treasureChestCallMap.get(key) != 0L)
				chestChunks.add(new Pair<>(key, treasureChestCallMap.get(key)));
		}
		
		long encodedChests, chestType;
		long worldseed;
		CPos cp;
		
		for (long up16=0; up16<MAX16; up16++) {	
			worldseed = (up16<<48) | structseed;
			
			for (Pair<CPos, Long> chestchunk : chestChunks) {
				cp = chestchunk.getFirst();
				long popseed = decoRand.getPopulationSeed(worldseed, cp.getX()<<4, cp.getZ()<<4);
				decoRand.setDecoratorSeed(popseed, 0, 7);
				
				encodedChests = chestchunk.getSecond();
				while (encodedChests != 0) {
					chestType = encodedChests & 3;
					encodedChests >>= 2;
					long lootseed = decoRand.nextLong() & MASK48;
					
					if (lootseed == LOOT_SEED_14_NOTCH || lootseed == LOOT_SEED_27_POTIONS) {
						if (chestType == 2 && ancientCityCanSpawn(worldseed, gen.pieces[0].box.getCenter())) {
							ResultWriter.writeToStdout(worldseed, lootseed, cp);
						}
					}
				}
			}
		}
	}
	
	// huge thanks to Andrew for fixing this
	private static boolean ancientCityCanSpawn(long worldseed, Vec3i centerOfFirstPiece) {
		NoiseSampler sampler = new NoiseSampler(worldseed, Dimension.OVERWORLD);
		
		Map<NoiseType, Double> noise = sampler.queryNoiseFromBlockPos(centerOfFirstPiece.getX(), -27 >> 2, centerOfFirstPiece.getZ(), NoiseType.EROSION, NoiseType.DEPTH);
		
		double D = noise.get(NoiseType.DEPTH);
        double E = noise.get(NoiseType.EROSION);

        double dD = 1.1 - D;
        double dE = Math.max(E + 0.375, 0);
        double dsD = Math.max(D - 1.0, 0);

        return dD * dD + dE * dE < dsD * dsD;
	}
}
