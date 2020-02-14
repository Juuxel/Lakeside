package juuxel.lakeside.decorator

import com.mojang.datafixers.Dynamic
import net.minecraft.util.math.BlockPos
import net.minecraft.world.Heightmap
import net.minecraft.world.IWorld
import net.minecraft.world.gen.chunk.ChunkGenerator
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig
import net.minecraft.world.gen.decorator.CountChanceDecoratorConfig
import net.minecraft.world.gen.decorator.Decorator
import java.util.Random
import java.util.stream.IntStream
import java.util.stream.Stream

class CountChanceTopSolidDecorator(
    configDeserializer: (Dynamic<*>) -> CountChanceDecoratorConfig
) : Decorator<CountChanceDecoratorConfig>(configDeserializer) {
    override fun getPositions(
        world: IWorld, generator: ChunkGenerator<out ChunkGeneratorConfig>, random: Random,
        config: CountChanceDecoratorConfig, pos: BlockPos
    ): Stream<BlockPos> = IntStream.range(0, config.count)
        .filter { random.nextFloat() < config.chance }
        .mapToObj {
            val x = random.nextInt(16) + pos.x
            val z = random.nextInt(16) + pos.z
            BlockPos(x, world.getTopY(Heightmap.Type.OCEAN_FLOOR_WG, x, z), z)
        }
}
