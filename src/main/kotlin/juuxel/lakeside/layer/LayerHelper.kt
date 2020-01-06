package juuxel.lakeside.layer

import juuxel.lakeside.api.MoreOverworldBiomes
import net.minecraft.util.registry.Registry
import net.minecraft.world.biome.Biome
import net.minecraft.world.biome.layer.util.LayerRandomnessSource

object LayerHelper {
    fun transformSmallVariant(context: LayerRandomnessSource, n: Int, e: Int, s: Int, w: Int, center: Int) =
        transform(context, n, e, s, w, center, MoreOverworldBiomes::transformSmallVariant)

    fun transformIsland(context: LayerRandomnessSource, n: Int, e: Int, s: Int, w: Int, center: Int) =
        transform(context, n, e, s, w, center, MoreOverworldBiomes::transformIsland)

    private inline fun transform(
        context: LayerRandomnessSource,
        n: Int, e: Int, s: Int, w: Int,
        center: Int, transformer: (Biome, LayerRandomnessSource) -> Biome?
    ): Int {
        var neighborCount = 0
        if (n == center) neighborCount++
        if (e == center) neighborCount++
        if (s == center) neighborCount++
        if (w == center) neighborCount++

        if (neighborCount >= 2) {
            val base = Registry.BIOME[center]
            val transformed = transformer(base!!, context)
            if (transformed != null) {
                return Registry.BIOME.getRawId(transformed)
            }
        }

        return center
    }
}
