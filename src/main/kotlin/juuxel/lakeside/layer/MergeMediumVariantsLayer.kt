package juuxel.lakeside.layer

import net.minecraft.world.biome.layer.type.MergingLayer
import net.minecraft.world.biome.layer.util.IdentityCoordinateTransformer
import net.minecraft.world.biome.layer.util.LayerRandomnessSource
import net.minecraft.world.biome.layer.util.LayerSampler

object MergeMediumVariantsLayer : MergingLayer, IdentityCoordinateTransformer {
    override fun sample(
        random: LayerRandomnessSource,
        sampler1: LayerSampler,
        sampler2: LayerSampler,
        x: Int, z: Int
    ): Int {
        // Note that sampler2 is actually scaled, so this won't work at all!
        // Anyway, the medium biomes are still too big at scale 2 and too small at scale 1.
        val variant = sampler2.sample(x, z)
        return if (variant != AddMediumVariantsLayer.DEFAULT_BIOME) variant else sampler1.sample(x, z)
    }
}
