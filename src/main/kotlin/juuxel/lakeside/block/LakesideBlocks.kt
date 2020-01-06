package juuxel.lakeside.block

import juuxel.lakeside.Lakeside
import net.fabricmc.fabric.api.block.FabricBlockSettings
import net.fabricmc.fabric.api.tools.FabricToolTags
import net.minecraft.block.Block
import net.minecraft.block.FallingBlock
import net.minecraft.block.Material
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.util.registry.Registry

object LakesideBlocks {
    val LIMONITE_SAND: Block = FallingBlock(
        FabricBlockSettings.of(Material.STONE)
            .strength(3.0F, 3.0F)
            .breakByTool(FabricToolTags.SHOVELS, 1)
            .sounds(BlockSoundGroup.SAND)
            .build()
    )

    fun init() {
        register("limonite_sand", LIMONITE_SAND) { group(ItemGroup.BUILDING_BLOCKS) }
    }

    private fun register(name: String, block: Block, itemConfig: Item.Settings.() -> Item.Settings = { this }) {
        val id = Lakeside.id(name)
        Registry.register(Registry.BLOCK, id, block)
        Registry.register(Registry.ITEM, id, BlockItem(block, Item.Settings().itemConfig()))
    }
}
