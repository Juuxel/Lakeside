package juuxel.lakeside.block

import com.google.common.collect.ImmutableMap
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.FallingBlock
import net.minecraft.block.Waterloggable
import net.minecraft.entity.EntityContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.fluid.FluidState
import net.minecraft.fluid.Fluids
import net.minecraft.item.ItemPlacementContext
import net.minecraft.sound.SoundCategory
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.IntProperty
import net.minecraft.state.property.Properties
import net.minecraft.tag.FluidTags
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World

class LimoniteSandBlock(settings: Settings) : FallingBlock(settings), Waterloggable {
    init {
        defaultState = defaultState
            .with(LEVEL, 1)
            .with(WATERLOGGED, false)
    }

    override fun onUse(
        state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult
    ): ActionResult {
        val stack = player.getStackInHand(hand)
        val level = state[LEVEL]
        if (stack.item === asItem() && level < MAX_LEVEL) {
            world.setBlockState(pos, state.with(LEVEL, level + 1))
            world.playSound(
                null, pos, soundGroup.placeSound, SoundCategory.BLOCKS,
                (soundGroup.volume + 1.0f) / 2.0f,
                soundGroup.pitch * 0.8f
            )

            if (!player.abilities.creativeMode) {
                stack.decrement(1)
            }

            return ActionResult.SUCCESS
        }

        return ActionResult.PASS
    }

    @Suppress("DEPRECATION")
    override fun getFluidState(state: BlockState): FluidState =
        if (state[WATERLOGGED]) Fluids.WATER.getStill(false)
        else super.getFluidState(state)

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState =
        defaultState.with(WATERLOGGED, ctx.world.getFluidState(ctx.blockPos).matches(FluidTags.WATER))

    override fun getOutlineShape(state: BlockState, view: BlockView, pos: BlockPos, context: EntityContext) =
        SHAPES[state[LEVEL]] ?: error("Could not find outline shape for $state")

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        super.appendProperties(builder)
        builder.add(LEVEL, WATERLOGGED)
    }

    companion object {
        private const val MAX_LEVEL = 4

        private val SHAPES: Map<Int, VoxelShape> = ImmutableMap.of(
            1, Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 4.0, 16.0),
            2, Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0),
            3, Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 12.0, 16.0),
            4, VoxelShapes.fullCube()
        )

        val LEVEL: IntProperty = IntProperty.of("level", 1, MAX_LEVEL)
        val WATERLOGGED: BooleanProperty = Properties.WATERLOGGED
    }
}
