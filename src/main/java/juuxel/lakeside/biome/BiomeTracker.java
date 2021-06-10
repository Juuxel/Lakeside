package juuxel.lakeside.biome;

import net.minecraft.world.biome.Biome;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public final class BiomeTracker {
    private static final Set<Biome> biomesWithLakes = Collections.newSetFromMap(new WeakHashMap<>());

    public static boolean hasLakes(Biome biome) {
        return biomesWithLakes.contains(biome);
    }

    public static void addBiomeWithLakes(Biome biome) {
        biomesWithLakes.add(biome);
    }
}
