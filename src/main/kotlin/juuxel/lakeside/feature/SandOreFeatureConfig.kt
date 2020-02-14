package juuxel.lakeside.feature

import com.mojang.datafixers.Dynamic
import com.mojang.datafixers.types.DynamicOps
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.state.property.IntProperty
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.minecraft.world.gen.feature.FeatureConfig

data class SandOreFeatureConfig(val ore: BlockState, val radius: Int, val levelProperty: IntProperty) : FeatureConfig {
    init {
        require(levelProperty in ore.properties) {
            "The block $ore does not have the $levelProperty property!"
        }
    }

    override fun <T> serialize(ops: DynamicOps<T>): Dynamic<T> =
        Dynamic(
            ops,
            ops.createMap(
                mapOf(
                    ops.createString("ore") to BlockState.serialize(ops, ore).value,
                    ops.createString("radius") to ops.createInt(radius),
                    ops.createString("level_property") to ops.createString(levelProperty.name)
                )
            )
        )

    companion object {
        fun deserialize(dynamic: Dynamic<*>): SandOreFeatureConfig {
            val ore = dynamic["ore"].map { BlockState.deserialize(it) }.orElse(Blocks.AIR.defaultState)

            return SandOreFeatureConfig(
                ore = ore,
                radius = dynamic["radius"].asInt(1),
                levelProperty = ore.block.stateManager.getProperty(
                    dynamic["level_property"].asString("level")
                ) as IntProperty
            )
        }
    }
}
