package juuxel.lakeside.biome

import com.google.common.collect.Multimap
import com.google.common.collect.MultimapBuilder
import net.minecraft.util.registry.BuiltinRegistries
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.biome.Biome
import net.minecraft.world.biome.layer.util.LayerRandomnessSource

@Suppress("UnstableApiUsage")
object MoreOverworldBiomes {
    private val smallVariants: Multimap<Int, SmallVariantEntry> = MultimapBuilder.hashKeys().hashSetValues().build()
    private val islands: Multimap<Int, SmallVariantEntry> = MultimapBuilder.hashKeys().hashSetValues().build()

    fun addSmallVariant(base: RegistryKey<Biome>, variant: RegistryKey<Biome>, chance: Int) {
        smallVariants.put(getRawId(base), SmallVariantEntry(getRawId(variant), chance))
    }

    fun transformSmallVariant(base: Int, random: LayerRandomnessSource): Int =
        transform(smallVariants, base, random)

    fun addIsland(base: RegistryKey<Biome>, variant: RegistryKey<Biome>, chance: Int) {
        islands.put(getRawId(base), SmallVariantEntry(getRawId(variant), chance))
    }

    fun transformIsland(base: Int, random: LayerRandomnessSource): Int = transform(islands, base, random)

    private fun transform(
        variants: Multimap<Int, SmallVariantEntry>, base: Int, random: LayerRandomnessSource
    ): Int {
        if (variants.containsKey(base)) {
            val entries = variants[base]
            val bound = entries.sumBy { it.chance }
            val i = random.nextInt(bound)
            var offset = 0

            for (entry in entries) {
                if (i == offset) return entry.variant
                offset += entry.chance
            }
        }

        return base
    }

    private fun getRawId(key: RegistryKey<Biome>): Int =
        BuiltinRegistries.BIOME.getRawId(BuiltinRegistries.BIOME.getOrThrow(key))

    private data class SmallVariantEntry(val variant: Int, val chance: Int)
}
