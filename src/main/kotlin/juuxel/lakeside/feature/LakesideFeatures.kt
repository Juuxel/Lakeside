package juuxel.lakeside.feature

import juuxel.lakeside.Lakeside
import net.minecraft.util.registry.Registry
import net.minecraft.world.gen.feature.Feature
import net.minecraft.world.gen.feature.FeatureConfig

object LakesideFeatures {
    val SAND_ORE: Feature<SandOreFeatureConfig> = SandOreFeature(SandOreFeatureConfig.Companion::deserialize)

    fun init() {
        register("sand_ore", SAND_ORE)
    }

    private fun <FC : FeatureConfig, F : Feature<FC>> register(name: String, feature: F): F =
        Registry.register(Registry.FEATURE, Lakeside.id(name), feature)
}
