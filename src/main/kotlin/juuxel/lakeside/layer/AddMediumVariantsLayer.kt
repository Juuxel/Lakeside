package juuxel.lakeside.layer

import juuxel.lakeside.api.MoreOverworldBiomes
import net.minecraft.util.registry.Registry
import net.minecraft.world.biome.layer.type.CrossSamplingLayer
import net.minecraft.world.biome.layer.util.LayerRandomnessSource

object AddMediumVariantsLayer : CrossSamplingLayer {
    const val DEFAULT_BIOME = 1

    override fun sample(random: LayerRandomnessSource, n: Int, e: Int, s: Int, w: Int, center: Int): Int {
        if (n == center && e == center && s == center && w == center) {
            val base = Registry.BIOME[center]
            val transformed = MoreOverworldBiomes.transformMediumVariant(base!!, random)
            if (transformed != null) {
                return Registry.BIOME.getRawId(transformed)
            }
        }

        return center
    }
}
