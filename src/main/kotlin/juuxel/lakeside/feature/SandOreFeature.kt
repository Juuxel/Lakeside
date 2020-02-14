package juuxel.lakeside.feature

import com.mojang.datafixers.Dynamic
import net.minecraft.block.Blocks
import net.minecraft.state.property.Properties
import net.minecraft.util.math.BlockPos
import net.minecraft.world.Heightmap
import net.minecraft.world.IWorld
import net.minecraft.world.gen.chunk.ChunkGenerator
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig
import net.minecraft.world.gen.feature.Feature
import java.util.Random
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
        val levels = config.levelProperty.values
        val maxLevel = levels.max()!!

        for (x in -config.radius..config.radius) {
            for (z in -config.radius..config.radius) {
                currentPos.x = baseX + x
                currentPos.z = baseZ + z
                currentPos.y = world.getTopY(Heightmap.Type.OCEAN_FLOOR_WG, baseX + x, baseZ + z) - 1
                val block = world.getBlockState(currentPos).block
                currentPos.y++
                val fluidAbove = !world.getFluidState(currentPos).isEmpty
                currentPos.y--

                if (block === Blocks.SAND && fluidAbove) {
                    val rawBound = hypot(x.toDouble(), z.toDouble()) / config.radius * 15.0
                    if (random.nextDouble() < 1 / rawBound) {
                        val level: Int = if (abs(x) < 3 && abs(z) < 3 && fluidAbove) {
                            val levelIndex = random.nextInt(levels.size)
                            levels.elementAt(levelIndex)
                        } else {
                            maxLevel
                        }
                        world.setBlockState(
                            currentPos,
                            config.ore.with(config.levelProperty, level).with(Properties.WATERLOGGED, fluidAbove),
                            2
                        )
                    }
                }
            }
        }

        return true
    }
}
