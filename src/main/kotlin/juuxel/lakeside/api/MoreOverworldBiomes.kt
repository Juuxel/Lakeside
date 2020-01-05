package juuxel.lakeside.api

import net.fabricmc.fabric.api.biomes.v1.OverworldBiomes
import net.minecraft.world.biome.Biome
import net.minecraft.world.biome.layer.util.LayerRandomnessSource
import kotlin.math.ceil

object MoreOverworldBiomes {
    private val subBiomes: MutableMap<Biome, SubBiomeEntry> = HashMap()

    fun addPartialEdgeBiome(base: Biome, edge: Biome, chance: Double) {
        OverworldBiomes.addEdgeBiome(base, edge, chance)
        OverworldBiomes.addEdgeBiome(base, base, ceil(chance) - chance)
    }

    fun addSubBiome(base: Biome, subBiome: Biome, chance: Int) {
        subBiomes[base] = SubBiomeEntry(subBiome, chance)
    }

    fun transformSubBiome(base: Biome, random: LayerRandomnessSource): Biome? =
        subBiomes[base]?.let { entry ->
            if (random.nextInt(entry.chance) == 0) entry.subBiome
            else null
        }

    private data class SubBiomeEntry(val subBiome: Biome, val chance: Int)
}
