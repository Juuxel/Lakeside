package juuxel.lakeside.feature

import com.mojang.datafixers.Dynamic
import com.mojang.datafixers.types.DynamicOps
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.world.gen.feature.FeatureConfig

data class SandOreFeatureConfig(val ore: BlockState, val radius: Int) : FeatureConfig {
    override fun <T> serialize(ops: DynamicOps<T>): Dynamic<T> =
        Dynamic(
            ops,
            ops.createMap(
                mapOf(
                    ops.createString("ore") to BlockState.serialize(ops, ore).value,
                    ops.createString("radius") to ops.createInt(radius)
                )
            )
        )

    companion object {
        fun deserialize(dynamic: Dynamic<*>) =
            SandOreFeatureConfig(
                ore = dynamic["ore"].map { BlockState.deserialize(it) }.orElse(Blocks.AIR.defaultState),
                radius = dynamic["radius"].asInt(1)
            )
    }
}
