package juuxel.lakeside

import juuxel.lakeside.biome.LakesideBiomes
import net.fabricmc.api.ModInitializer
import net.minecraft.util.Identifier

object Lakeside : ModInitializer {
    fun id(name: String) =
        Identifier("lakeside", name)

    override fun onInitialize() {
        LakesideBiomes.init()
    }
}
