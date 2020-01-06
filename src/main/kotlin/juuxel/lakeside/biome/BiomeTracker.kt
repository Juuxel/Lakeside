package juuxel.lakeside.biome

import net.minecraft.world.biome.Biome

object BiomeTracker {
    private val biomesWithLakes = ArrayList<Biome>()

    fun addBiomeWithLakes(biome: Biome) {
        biomesWithLakes += biome
    }

    fun hasLakes(biome: Biome) = biome in biomesWithLakes
}
