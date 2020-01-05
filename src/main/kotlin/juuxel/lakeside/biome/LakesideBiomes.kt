package juuxel.lakeside.biome

import com.terraformersmc.terraform.biome.builder.DefaultFeature.*
import com.terraformersmc.terraform.biome.builder.TerraformBiome
import juuxel.lakeside.Lakeside
import juuxel.lakeside.api.MoreOverworldBiomes
import net.fabricmc.fabric.api.biomes.v1.OverworldBiomes
import net.minecraft.entity.EntityType
import net.minecraft.util.registry.Registry
import net.minecraft.world.biome.Biome
import net.minecraft.world.biome.Biomes
import net.minecraft.world.gen.GenerationStep
import net.minecraft.world.gen.decorator.Decorator
import net.minecraft.world.gen.decorator.DecoratorConfig
import net.minecraft.world.gen.feature.Feature
import net.minecraft.world.gen.feature.SeagrassFeatureConfig
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder

object LakesideBiomes {
    private fun TerraformBiome.Builder.toTemplate() = TerraformBiome.Template(this)

    private val BASE_TEMPLATE: TerraformBiome.Template = TerraformBiome.builder()
        .configureSurfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.GRASS_CONFIG)
        .precipitation(Biome.Precipitation.RAIN)
        .waterColor(0x3F76E4).waterFogColor(0x050533)
        .addDefaultFeatures(
            LAND_CARVERS, STRUCTURES, DUNGEONS, MINEABLES, ORES, DISKS,
            SPRINGS, FROZEN_TOP_LAYER, DEFAULT_FLOWERS, DEFAULT_MUSHROOMS, DEFAULT_VEGETATION
        )
        .addSpawnEntry(Biome.SpawnEntry(EntityType.BAT, 10, 8, 8))
        .addSpawnEntry(Biome.SpawnEntry(EntityType.SPIDER, 100, 4, 4))
        .addSpawnEntry(Biome.SpawnEntry(EntityType.ZOMBIE, 95, 4, 4))
        .addSpawnEntry(Biome.SpawnEntry(EntityType.DROWNED, 100, 1, 1))
        .addSpawnEntry(Biome.SpawnEntry(EntityType.ZOMBIE_VILLAGER, 5, 1, 1))
        .addSpawnEntry(Biome.SpawnEntry(EntityType.SKELETON, 100, 4, 4))
        .addSpawnEntry(Biome.SpawnEntry(EntityType.CREEPER, 100, 4, 4))
        .addSpawnEntry(Biome.SpawnEntry(EntityType.SLIME, 100, 4, 4))
        .addSpawnEntry(Biome.SpawnEntry(EntityType.ENDERMAN, 10, 1, 4))
        .addSpawnEntry(Biome.SpawnEntry(EntityType.WITCH, 5, 1, 1))
        .toTemplate()

    private val LAKE_TEMPLATE = BASE_TEMPLATE.builder()
        .category(Biome.Category.RIVER)
        .depth(-0.35f).scale(0f)
        .temperature(0.7F).downfall(0.8F)
        .addDefaultFeatures(WATER_BIOME_OAK_TREES, DEFAULT_GRASS)
        .addCustomFeature(
            GenerationStep.Feature.VEGETAL_DECORATION,
            Feature.SEAGRASS.configure(SeagrassFeatureConfig(48, 0.4))
                .createDecoratedFeature(Decorator.TOP_SOLID_HEIGHTMAP.configure(DecoratorConfig.DEFAULT))
        )
        .addSpawnEntry(Biome.SpawnEntry(EntityType.SQUID, 2, 1, 4))
        .addSpawnEntry(Biome.SpawnEntry(EntityType.SALMON, 5, 1, 5))
        .toTemplate()

    private val ISLAND_TEMPLATE = BASE_TEMPLATE.builder()
        .depth(0.125F).scale(0.05F)
        .addSpawnEntry(Biome.SpawnEntry(EntityType.SHEEP, 12, 4, 4))
        .addSpawnEntry(Biome.SpawnEntry(EntityType.PIG, 10, 4, 4))
        .addSpawnEntry(Biome.SpawnEntry(EntityType.CHICKEN, 10, 4, 4))
        .addSpawnEntry(Biome.SpawnEntry(EntityType.COW, 8, 4, 4))
        .addSpawnEntry(Biome.SpawnEntry(EntityType.WOLF, 8, 4, 4))
        .addSpawnEntry(Biome.SpawnEntry(EntityType.RABBIT, 4, 2, 3))
        .toTemplate()

    val LAKE: Biome = LAKE_TEMPLATE.builder().build()

    val MOUNTAIN_LAKE: Biome = LAKE_TEMPLATE.builder()
        .temperature(0.2F).downfall(0.3F)
        .build()

    val FOREST_ISLAND: Biome = ISLAND_TEMPLATE.builder()
        .temperature(0.7F).downfall(0.8F)
        .category(Biome.Category.FOREST)
        .addDefaultFeatures(FOREST_FLOWERS, DUNGEONS, FOREST_TREES, FOREST_GRASS)
        .build()

    val TAIGA_ISLAND: Biome = ISLAND_TEMPLATE.builder()
        .depth(0.2F).scale(0.2F)
        .temperature(0.25F).downfall(0.8F)
        .category(Biome.Category.TAIGA)
        .addDefaultFeatures(LARGE_FERNS, DUNGEONS, TAIGA_TREES, TAIGA_GRASS, SWEET_BERRY_BUSHES)
        .addSpawnEntry(Biome.SpawnEntry(EntityType.FOX, 8, 2, 4))
        .build()

    fun init() {
        register("lake", LAKE)
        register("mountain_lake", MOUNTAIN_LAKE)
        register("forest_island", FOREST_ISLAND)
        register("taiga_island", TAIGA_ISLAND)

        // Smaller lakes
        MoreOverworldBiomes.addSmallVariant(Biomes.FOREST, LAKE, 25)
        MoreOverworldBiomes.addSmallVariant(Biomes.BIRCH_FOREST, LAKE, 25)
        MoreOverworldBiomes.addSmallVariant(Biomes.PLAINS, LAKE, 25)
        // TODO MoreOverworldBiomes.addSmallVariant(Biomes.MOUNTAINS, MOUNTAIN_LAKE, 45)

        // Bigger lakes
//        OverworldBiomes.addBiomeVariant(Biomes.MOUNTAINS, MOUNTAIN_LAKE, 0.05)
        MoreOverworldBiomes.addMediumVariant(Biomes.MOUNTAINS, MOUNTAIN_LAKE, 0.05)
        MoreOverworldBiomes.addMediumVariant(Biomes.FOREST, LAKE, 0.05)
        MoreOverworldBiomes.addMediumVariant(Biomes.PLAINS, LAKE, 0.03)

        // Beaches
        MoreOverworldBiomes.addPartialEdgeBiome(MOUNTAIN_LAKE, Biomes.BEACH, 0.2)
        MoreOverworldBiomes.addPartialEdgeBiome(FOREST_ISLAND, Biomes.BEACH, 0.2)
        OverworldBiomes.addEdgeBiome(TAIGA_ISLAND, Biomes.STONE_SHORE, 0.1)
        OverworldBiomes.addEdgeBiome(TAIGA_ISLAND, Biomes.BEACH, 0.3)
        OverworldBiomes.addEdgeBiome(TAIGA_ISLAND, TAIGA_ISLAND, 0.6)

        // Islands
        MoreOverworldBiomes.addSmallVariant(MOUNTAIN_LAKE, TAIGA_ISLAND, 5)
        MoreOverworldBiomes.addSmallVariant(LAKE, FOREST_ISLAND, 5)
    }

    private fun register(name: String, biome: Biome): Biome =
        Registry.register(Registry.BIOME, Lakeside.id(name), biome)
}
