package juuxel.lakeside.layer

import juuxel.lakeside.api.MoreOverworldBiomes.transformSmallVariant
import net.minecraft.util.registry.Registry
import net.minecraft.world.biome.layer.type.CrossSamplingLayer
import net.minecraft.world.biome.layer.util.LayerRandomnessSource

/**
 * This layer adds small biome variants when there are enough neighbouring biomes.
 *
 * @see juuxel.lakeside.mixin.BiomeLayersMixin first round
 * @see juuxel.lakeside.mixin.EaseBiomeEdgeLayerMixin second round
 */
object AddSmallVariantsLayer : CrossSamplingLayer {
    override fun sample(context: LayerRandomnessSource, n: Int, e: Int, s: Int, w: Int, center: Int): Int {
        var neighborCount = 0
        if (n == center) neighborCount++
        if (e == center) neighborCount++
        if (s == center) neighborCount++
        if (w == center) neighborCount++

        if (neighborCount >= 2) {
            val base = Registry.BIOME[center]
            val transformed = transformSmallVariant(base!!, context)
            if (transformed != null) {
                return Registry.BIOME.getRawId(transformed)
            }
        }

        return center
    }
}
