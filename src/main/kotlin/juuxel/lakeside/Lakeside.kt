package juuxel.lakeside

import juuxel.lakeside.biome.LakesideBiomes
import juuxel.lakeside.block.LakesideBlocks
import juuxel.lakeside.decorator.LakesideDecorators
import juuxel.lakeside.feature.LakesideFeatures
import net.fabricmc.api.ModInitializer
import net.minecraft.util.Identifier

object Lakeside : ModInitializer {
    fun id(name: String) =
        Identifier("lakeside", name)

    override fun onInitialize() {
        LakesideBlocks.init()
        LakesideDecorators.init()
        LakesideFeatures.init()
        LakesideBiomes.init()
    }
}
