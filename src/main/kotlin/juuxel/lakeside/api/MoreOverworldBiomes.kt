package juuxel.lakeside.api

import net.fabricmc.fabric.api.biomes.v1.OverworldBiomes
import net.minecraft.util.registry.Registry
import net.minecraft.world.biome.Biome
import net.minecraft.world.biome.layer.util.LayerRandomnessSource
import kotlin.math.ceil

object MoreOverworldBiomes {
    private val smallVariants: MutableMap<Biome, SmallVariantEntry> = HashMap()
    private val islands: MutableMap<Biome, SmallVariantEntry> = HashMap()

    @Deprecated("Use addEdgeBiome", ReplaceWith("OverworldBiomes.addEdgeBiome(base, edge, chance)", "net.fabricmc.fabric.api.biomes.v1.OverworldBiomes"))
    fun addPartialEdgeBiome(base: Biome, edge: Biome, chance: Double) {
        OverworldBiomes.addEdgeBiome(base, edge, chance)
        OverworldBiomes.addEdgeBiome(base, base, ceil(chance) - chance)
    }

    fun addSmallVariant(base: Biome, variant: Biome, chance: Int) {
        require(base !in smallVariants) {
            "Small variant for base biome ${Registry.BIOME.getId(base)} is already registered!"
        }
        smallVariants[base] = SmallVariantEntry(variant, chance)
    }

    fun transformSmallVariant(base: Biome, random: LayerRandomnessSource): Biome? =
        smallVariants[base]?.let { entry ->
            if (random.nextInt(entry.chance) == 0) entry.variant
            else null
        }

    fun addIsland(base: Biome, variant: Biome, chance: Int) {
        require(base !in islands) {
            "Island for base biome ${Registry.BIOME.getId(base)} is already registered!"
        }
        islands[base] = SmallVariantEntry(variant, chance)
    }

    fun transformIsland(base: Biome, random: LayerRandomnessSource): Biome? =
        islands[base]?.let { entry ->
            if (random.nextInt(entry.chance) == 0) entry.variant
            else null
        }

    private data class SmallVariantEntry(val variant: Biome, val chance: Int)
}
