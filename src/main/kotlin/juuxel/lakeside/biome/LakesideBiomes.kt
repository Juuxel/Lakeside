package juuxel.lakeside.biome

import com.terraformersmc.terraform.biome.builder.DefaultFeature.*
import com.terraformersmc.terraform.biome.builder.TerraformBiome
import juuxel.lakeside.Lakeside
import juuxel.lakeside.api.MoreOverworldBiomes
import juuxel.lakeside.block.LakesideBlocks
import juuxel.lakeside.feature.LakesideFeatures
import juuxel.lakeside.feature.SandOreFeatureConfig
import juuxel.lakeside.util.visit
import net.fabricmc.fabric.api.biomes.v1.OverworldBiomes
import net.minecraft.entity.EntityType
import net.minecraft.util.registry.Registry
import net.minecraft.world.biome.Biome
import net.minecraft.world.biome.Biome.TemperatureGroup
import net.minecraft.world.biome.Biomes
import net.minecraft.world.biome.DefaultBiomeFeatures
import net.minecraft.world.gen.GenerationStep
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig
import net.minecraft.world.gen.decorator.CountDecoratorConfig
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
        .depth(-0.45f).scale(0f)
        .temperature(0.7F).downfall(0.8F)
        .addDefaultFeatures(WATER_BIOME_OAK_TREES, DEFAULT_GRASS)
        .addCustomFeature(
            GenerationStep.Feature.VEGETAL_DECORATION,
            Feature.SEAGRASS.configure(SeagrassFeatureConfig(48, 0.4))
                .createDecoratedFeature(Decorator.TOP_SOLID_HEIGHTMAP.configure(DecoratorConfig.DEFAULT))
        )
        .addCustomFeature(
            GenerationStep.Feature.UNDERGROUND_DECORATION,
            LakesideFeatures.SAND_ORE.configure(
                SandOreFeatureConfig(LakesideBlocks.LIMONITE_SAND.defaultState, 6)
            ).createDecoratedFeature(Decorator.COUNT_TOP_SOLID.configure(CountDecoratorConfig(1)))
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

    val WARM_LAKE: Biome = LAKE_TEMPLATE.builder()
        .addCustomFeature(
            GenerationStep.Feature.VEGETAL_DECORATION,
            Feature.RANDOM_PATCH.configure(DefaultBiomeFeatures.LILY_PAD_CONFIG)
                .createDecoratedFeature(Decorator.CHANCE_HEIGHTMAP.configure(ChanceDecoratorConfig(4)))
        )
        .build()

    val COLD_LAKE: Biome = LAKE_TEMPLATE.builder()
        .temperature(0.2F).downfall(0.3F)
        .build()

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
        .addDefaultFeatures(LARGE_FERNS, DUNGEONS, TAIGA_TREES, TAIGA_GRASS, SWEET_BERRY_BUSHES, EXTRA_MOUNTAIN_TREES)
        .addSpawnEntry(Biome.SpawnEntry(EntityType.FOX, 8, 2, 4))
        .build()

    fun init() {
        register("warm_lake", WARM_LAKE)
        register("cold_lake", COLD_LAKE)
        register("mountain_lake", MOUNTAIN_LAKE)
        register("forest_island", FOREST_ISLAND)
        register("taiga_island", TAIGA_ISLAND)

        /*// Smaller lakes
        MoreOverworldBiomes.addSmallVariant(Biomes.FOREST, WARM_LAKE, 25)
        MoreOverworldBiomes.addSmallVariant(Biomes.BIRCH_FOREST, WARM_LAKE, 25)
        MoreOverworldBiomes.addSmallVariant(Biomes.PLAINS, WARM_LAKE, 25)
        MoreOverworldBiomes.addSmallVariant(Biomes.TAIGA, COLD_LAKE, 25)
        MoreOverworldBiomes.addSmallVariant(Biomes.MOUNTAINS, MOUNTAIN_LAKE, 45)

        // Bigger lakes
        OverworldBiomes.addBiomeVariant(Biomes.MOUNTAINS, MOUNTAIN_LAKE, 0.05)
        OverworldBiomes.addBiomeVariant(Biomes.FOREST, WARM_LAKE, 0.05)
        OverworldBiomes.addBiomeVariant(Biomes.PLAINS, WARM_LAKE, 0.03)
        OverworldBiomes.addBiomeVariant(Biomes.TAIGA, COLD_LAKE, 0.05)*/

        // Beaches
        OverworldBiomes.addEdgeBiome(MOUNTAIN_LAKE, Biomes.STONE_SHORE, 1.0)
        //OverworldBiomes.addEdgeBiome(FOREST_ISLAND, Biomes.BEACH, 1.0)
        OverworldBiomes.addShoreBiome(MOUNTAIN_LAKE, Biomes.TAIGA, 1.0)
        OverworldBiomes.addShoreBiome(COLD_LAKE, Biomes.TAIGA, 1.0)
        OverworldBiomes.addShoreBiome(WARM_LAKE, FOREST_ISLAND, 1.0)

        // Islands
        MoreOverworldBiomes.addSmallVariant(COLD_LAKE, TAIGA_ISLAND, 4)
        MoreOverworldBiomes.addSmallVariant(MOUNTAIN_LAKE, TAIGA_ISLAND, 4)
        MoreOverworldBiomes.addSmallVariant(WARM_LAKE, FOREST_ISLAND, 4)
        MoreOverworldBiomes.addIsland(COLD_LAKE, TAIGA_ISLAND, 4)
        MoreOverworldBiomes.addIsland(MOUNTAIN_LAKE, TAIGA_ISLAND, 4)
        MoreOverworldBiomes.addIsland(WARM_LAKE, FOREST_ISLAND, 4)

        Registry.BIOME.visit { _, biome, _ ->
            if (BiomeTracker.hasLakes(biome)) {
                registerLakes(biome)
            }
            if (biome.category == Biome.Category.OCEAN) {
                registerOceanIslands(biome)
            }
        }
    }

    private fun register(name: String, biome: Biome): Biome =
        Registry.register(Registry.BIOME, Lakeside.id(name), biome)

    private fun registerLakes(biome: Biome) {
        val temperature = biome.temperatureGroup
        val category = biome.category

        if (category == Biome.Category.RIVER || category == Biome.Category.OCEAN) {
            // Ignore water biomes
            return
        }

        when {
            category == Biome.Category.EXTREME_HILLS -> {
                MoreOverworldBiomes.addSmallVariant(biome, MOUNTAIN_LAKE, 45)
                OverworldBiomes.addBiomeVariant(biome, MOUNTAIN_LAKE, 0.05)
            }

            temperature == TemperatureGroup.COLD || category == Biome.Category.TAIGA -> {
                MoreOverworldBiomes.addSmallVariant(biome, COLD_LAKE, 25)
                OverworldBiomes.addBiomeVariant(biome, COLD_LAKE, 0.05)
            }

            temperature == TemperatureGroup.MEDIUM -> {
                MoreOverworldBiomes.addSmallVariant(biome, WARM_LAKE, 25)
                OverworldBiomes.addBiomeVariant(biome, WARM_LAKE, 0.05)
            }
        } // TODO: Lakes for WARM temperatures?
    }

    private fun registerOceanIslands(biome: Biome) {
        MoreOverworldBiomes.addSmallVariant(biome, TAIGA_ISLAND, 20)
        MoreOverworldBiomes.addSmallVariant(biome, FOREST_ISLAND, 20)
    }
}
