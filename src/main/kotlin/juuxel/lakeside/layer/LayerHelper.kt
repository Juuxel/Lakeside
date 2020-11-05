package juuxel.lakeside.layer

import juuxel.lakeside.biome.MoreOverworldBiomes
import net.minecraft.world.biome.layer.util.LayerRandomnessSource

object LayerHelper {
    fun transformSmallVariant(context: LayerRandomnessSource, n: Int, e: Int, s: Int, w: Int, center: Int) =
        transform(context, n, e, s, w, center, MoreOverworldBiomes::transformSmallVariant)

    fun transformIsland(context: LayerRandomnessSource, n: Int, e: Int, s: Int, w: Int, center: Int) =
        transform(context, n, e, s, w, center, MoreOverworldBiomes::transformIsland)

    private inline fun transform(
        context: LayerRandomnessSource,
        n: Int, e: Int, s: Int, w: Int,
        center: Int, transformer: (Int, LayerRandomnessSource) -> Int
    ): Int {
        var neighborCount = 0
        if (n == center) neighborCount++
        if (e == center) neighborCount++
        if (s == center) neighborCount++
        if (w == center) neighborCount++

        if (neighborCount >= 2) {
            val transformed = transformer(center, context)
            if (transformed != center) {
                return transformed
            }
        }

        return center
    }
}
