package notches;

import java.util.List;

import com.seedfinding.mccore.util.data.Pair;
import com.seedfinding.mcseed.rand.JRand;

public class AncientCityChest {
	private static final JRand rand = new JRand(0L);
	private static final int [] PRECOMPUTED_ITEMS = new int[86];
	private static final boolean [] MULTI_ENCHANTS = new boolean[37];
	
	public static int getNotches(long lootseed) {
		rand.setSeed(lootseed);
		int rolls = rand.nextInt(6) + 5;
		int w, item;
		int notches = 0;
		
		for (int i=0; i<rolls; i++) {
			w = rand.nextInt(86);
			//System.out.println(w + " " + PRECOMPUTED_ITEMS[w]);
			item = PRECOMPUTED_ITEMS[w];
			switch (item) {
			case 9:
				return notches;
			case 7:
				notches += rand.nextInt(2) + 1;
				break;
			case 8:
				// random enchant
				w = rand.nextInt(37);
				if (MULTI_ENCHANTS[w])
					rand.nextSeed();
				break;
			default:
				//System.out.println("here");
				for (int calls=0; calls<item; calls++)
					rand.nextSeed();
			}
		}
		
		return notches;
	}
	
	public static void compute() {
		int ix = 0;
		for (Pair<Integer, Integer> p : entries) {
			for (int i=0; i<p.getSecond(); i++) {
				PRECOMPUTED_ITEMS[ix] = p.getFirst();
				ix++;
			}
		}
		
		for (int i=0; i<37; i++) {
			MULTI_ENCHANTS[i] = true;
		}
		
		MULTI_ENCHANTS[6] = false;
		MULTI_ENCHANTS[10] = false;
		MULTI_ENCHANTS[19] = false;
		MULTI_ENCHANTS[24] = false;
		MULTI_ENCHANTS[25] = false;
		MULTI_ENCHANTS[31] = false;
		MULTI_ENCHANTS[32] = false;
		MULTI_ENCHANTS[35] = false;
		MULTI_ENCHANTS[36] = false;
	}
	
	// 7 = notch
	// 8 = random enchant book
	// 9 = return cause weird num of calls (enchant with levels items)
	// anything else -> skip X calls every time
	
	public static final List<Pair<Integer, Integer>> entries = List.of(
			new Pair<>(7, 1),
			new Pair<>(0, 3),
			new Pair<>(1, 2),
			new Pair<>(0, 2),
			new Pair<>(9, 2),
			new Pair<>(0, 10),
			new Pair<>(9, 2),
			
			new Pair<>(2, 3),
			new Pair<>(1, 18),
			new Pair<>(9, 3),
			new Pair<>(1, 13),
			new Pair<>(8, 5),
			new Pair<>(1, 22)
	);
}
