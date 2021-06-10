package juuxel.lakeside.biome;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public final class MoreOverworldBiomes {
    private static final Multimap<Integer, SmallVariantEntry> smallVariants = MultimapBuilder.hashKeys().hashSetValues().build();
    private static final Multimap<Integer, SmallVariantEntry> islands = MultimapBuilder.hashKeys().hashSetValues().build();

    public static void addSmallVariant(RegistryKey<Biome> base, RegistryKey<Biome> variant, int chance) {
        smallVariants.put(getRawId(base), new SmallVariantEntry(getRawId(variant), chance));
    }

    public static int transformSmallVariant(int base, LayerRandomnessSource random) {
        return transform(smallVariants, base, random);
    }

    public static void addIsland(RegistryKey<Biome> base, RegistryKey<Biome> variant, int chance) {
        islands.put(getRawId(base), new SmallVariantEntry(getRawId(variant), chance));
    }

    public static int transformIsland(int base, LayerRandomnessSource random) {
        return transform(islands, base, random);
    }

    private static int transform(Multimap<Integer, SmallVariantEntry> variants, int base, LayerRandomnessSource random) {
        if (variants.containsKey(base)) {
            var entries = variants.get(base);
            var bound = 0;

            for (var entry : entries) {
                bound += entry.chance();
            }

            var i = random.nextInt(bound);
            var offset = 0;

            for (var entry : entries) {
                if (i == offset) return entry.variant();
                offset += entry.chance();
            }
        }

        return base;
    }

    private static int getRawId(RegistryKey<Biome> key) {
        return BuiltinRegistries.BIOME.getRawId(BuiltinRegistries.BIOME.getOrThrow(key));
    }

    private record SmallVariantEntry(int variant, int chance) {}
}
