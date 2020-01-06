package juuxel.lakeside.feature

import com.mojang.datafixers.Dynamic
import net.minecraft.block.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.world.Heightmap
import net.minecraft.world.IWorld
import net.minecraft.world.gen.chunk.ChunkGenerator
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig
import net.minecraft.world.gen.feature.Feature
import java.util.*
import kotlin.math.abs
import kotlin.math.hypot

class SandOreFeature(
    configDeserializer: (Dynamic<*>) -> SandOreFeatureConfig
) : Feature<SandOreFeatureConfig>(configDeserializer) {
    override fun generate(
        world: IWorld, generator: ChunkGenerator<out ChunkGeneratorConfig>,
        random: Random, pos: BlockPos, config: SandOreFeatureConfig
    ): Boolean {
        val currentPos = BlockPos.Mutable()
        val baseX = pos.x
        val baseZ = pos.z

        for (x in -config.radius..config.radius) {
            for (z in -config.radius..config.radius) {
                currentPos.x = baseX + x
                currentPos.z = baseZ + z
                currentPos.y = world.getTopY(Heightmap.Type.OCEAN_FLOOR_WG, baseX + x, baseZ + z) - 1
                val block = world.getBlockState(currentPos).block

                if (block === Blocks.SAND) {
                    val rawBound = hypot(abs(x).toDouble(), abs(z).toDouble()) / config.radius * 15.0
                    if (random.nextDouble() < 1 / rawBound) {
                        world.setBlockState(currentPos, config.ore, 2)
                    }
                }
            }
        }

        return true
    }
}
