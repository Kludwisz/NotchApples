package notches;

import com.seedfinding.mccore.util.pos.CPos;

import java.util.HashMap;

public class ResultWriter {
    private static final HashMap<Long, String> SEED_TO_ID = new HashMap<>();
    static {
        SEED_TO_ID.put(217704587079581L, "notches");
        SEED_TO_ID.put(4563034899199L, "potions");
    }

    public static void writeToStdout(long worldseed, long lootseed, CPos chestChunkPos) {
        String id = SEED_TO_ID.getOrDefault(lootseed, null);
        if (id == null) {
            throw new IllegalStateException("Bad lootseed: " + lootseed);
        }

        System.out.println(id + " " + worldseed + " " + chestChunkPos.getX() + " " + chestChunkPos.getZ());
    }
}
