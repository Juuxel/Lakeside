package juuxel.lakeside.biome

import com.mojang.serialization.Lifecycle
import juuxel.lakeside.Lakeside
import juuxel.lakeside.mixin.BuiltinBiomesAccessor
import juuxel.lakeside.mixin.DefaultBiomeCreatorAccessor
import juuxel.lakeside.util.visit
import net.fabricmc.fabric.api.biome.v1.OverworldBiomes
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnGroup
import net.minecraft.sound.BiomeMoodSound
import net.minecraft.util.registry.BuiltinRegistries
import net.minecraft.util.registry.MutableRegistry
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.biome.Biome
import net.minecraft.world.biome.BiomeEffects
import net.minecraft.world.biome.BiomeKeys
import net.minecraft.world.biome.GenerationSettings
import net.minecraft.world.biome.SpawnSettings
import net.minecraft.world.gen.GenerationStep
import net.minecraft.world.gen.feature.ConfiguredFeatures
import net.minecraft.world.gen.feature.DefaultBiomeFeatures
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilders

object LakesideBiomes {
    val WARM_LAKE = key("warm_lake")
    val JUNGLE_LAKE = key("jungle_lake")
    val COLD_LAKE = key("cold_lake")
    val MOUNTAIN_LAKE = key("mountain_lake")
    val FOREST_ISLAND = key("forest_island")
    val TAIGA_ISLAND = key("taiga_island")
    val JUNGLE_ISLAND = key("jungle_island")

    fun init() {
        register(WARM_LAKE, createBiome(Templates.WARM_LAKE, temperature = 0.7f))
        register(JUNGLE_LAKE, createBiome(Templates.JUNGLE_LAKE, temperature = 0.8f))
        register(COLD_LAKE, createBiome(Templates.COLD_LAKE, temperature = 0.2f))
        register(MOUNTAIN_LAKE, createBiome(Templates.MOUNTAIN_LAKE, temperature = 0.2f))
        register(FOREST_ISLAND, createBiome(Templates.FOREST_ISLAND, temperature = 0.7f))
        register(TAIGA_ISLAND, createBiome(Templates.TAIGA_ISLAND, temperature = 0.25f))
        register(JUNGLE_ISLAND, createBiome(Templates.JUNGLE_ISLAND, temperature = 0.95f))

        // Beaches
        OverworldBiomes.addEdgeBiome(MOUNTAIN_LAKE, BiomeKeys.STONE_SHORE, 1.0)
        OverworldBiomes.addEdgeBiome(JUNGLE_LAKE, BiomeKeys.JUNGLE_EDGE, 1.0)
        // OverworldBiomes.addEdgeBiome(FOREST_ISLAND, Biomes.BEACH, 1.0)
        OverworldBiomes.addShoreBiome(MOUNTAIN_LAKE, TAIGA_ISLAND, 1.0)
        OverworldBiomes.addShoreBiome(COLD_LAKE, TAIGA_ISLAND, 1.0)
        OverworldBiomes.addShoreBiome(WARM_LAKE, FOREST_ISLAND, 1.0)
        OverworldBiomes.addShoreBiome(JUNGLE_LAKE, JUNGLE_ISLAND, 1.0)

        // Islands
        MoreOverworldBiomes.addSmallVariant(COLD_LAKE, TAIGA_ISLAND, 4)
        MoreOverworldBiomes.addSmallVariant(MOUNTAIN_LAKE, TAIGA_ISLAND, 4)
        MoreOverworldBiomes.addSmallVariant(WARM_LAKE, FOREST_ISLAND, 4)
        MoreOverworldBiomes.addSmallVariant(JUNGLE_LAKE, JUNGLE_ISLAND, 4)
        MoreOverworldBiomes.addIsland(COLD_LAKE, TAIGA_ISLAND, 4)
        MoreOverworldBiomes.addIsland(MOUNTAIN_LAKE, TAIGA_ISLAND, 4)
        MoreOverworldBiomes.addIsland(WARM_LAKE, FOREST_ISLAND, 4)
        MoreOverworldBiomes.addIsland(JUNGLE_LAKE, JUNGLE_ISLAND, 4)

        BuiltinRegistries.BIOME.visit { _, biome, _ ->
            if (BiomeTracker.hasLakes(biome)) {
                registerLakes(biome)
            }
            if (biome.category == Biome.Category.OCEAN) {
                registerOceanIslands(biome)
            }
        }
    }

    private fun key(id: String): RegistryKey<Biome> = RegistryKey.of(Registry.BIOME_KEY, Lakeside.id(id))

    @Suppress("ThrowableNotThrown")
    private fun getKey(biome: Biome): RegistryKey<Biome> =
        BuiltinRegistries.BIOME.getKey(biome).orElseThrow { RuntimeException("Key not found for biome $biome!") }

    private fun createBiome(template: Template, temperature: Float): Biome =
        Biome.Builder()
            .generationSettings(GenerationSettings.Builder().also(template.generation).build())
            .spawnSettings(SpawnSettings.Builder().also(template.spawn).build())
            .effects(BiomeEffects.Builder().also(template.effects).skyColor(DefaultBiomeCreatorAccessor.callGetSkyColor(temperature)).build())
            .also(template.biome)
            .temperature(temperature)
            .build()

    // Copied verbatim from Woods and Mires.
    private fun register(key: RegistryKey<Biome>, biome: Biome) {
        (BuiltinRegistries.BIOME as MutableRegistry<Biome>).add(key, biome, Lifecycle.stable())

        // Ensures that the biome is stored in the internal raw ID map of BuiltinBiomes.
        // Fabric API usually does this, but some of my biomes don't go through OverworldBiomes at all,
        // which means that won't always get done.
        val byRawId = BuiltinBiomesAccessor.getBY_RAW_ID()
        byRawId[BuiltinRegistries.BIOME.getRawId(biome)] = key
    }

    private fun registerLakes(biome: Biome) {
        val key = getKey(biome)
        val category = biome.category

        if (category == Biome.Category.RIVER || category == Biome.Category.OCEAN) {
            // Ignore water biomes
            return
        }

        when {
            category == Biome.Category.EXTREME_HILLS -> {
                MoreOverworldBiomes.addSmallVariant(key, MOUNTAIN_LAKE, 45)
                OverworldBiomes.addBiomeVariant(key, MOUNTAIN_LAKE, 0.05)
            }

            category == Biome.Category.JUNGLE -> {
                MoreOverworldBiomes.addSmallVariant(key, JUNGLE_LAKE, 25)
                OverworldBiomes.addBiomeVariant(key, JUNGLE_LAKE, 0.05)
            }

            category == Biome.Category.ICY || category == Biome.Category.TAIGA -> {
                MoreOverworldBiomes.addSmallVariant(key, COLD_LAKE, 25)
                OverworldBiomes.addBiomeVariant(key, COLD_LAKE, 0.05)
            }

            else -> {
                MoreOverworldBiomes.addSmallVariant(key, WARM_LAKE, 25)
                OverworldBiomes.addBiomeVariant(key, WARM_LAKE, 0.05)
            }
        }
    }

    private fun registerOceanIslands(biome: Biome) {
        val key = getKey(biome)

        MoreOverworldBiomes.addSmallVariant(key, TAIGA_ISLAND, 20)
        MoreOverworldBiomes.addSmallVariant(key, FOREST_ISLAND, 20)
    }

    private class Template(
        val generation: (GenerationSettings.Builder) -> Unit = {},
        val spawn: (SpawnSettings.Builder) -> Unit = {},
        val effects: (BiomeEffects.Builder) -> Unit = {},
        val biome: (Biome.Builder) -> Unit = {}
    ) {
        fun parent(parent: Template) = Template(
            generation = {
                parent.generation(it)
                generation(it)
            },
            spawn = {
                parent.spawn(it)
                spawn(it)
            },
            effects = {
                parent.effects(it)
                effects(it)
            },
            biome = {
                parent.biome(it)
                biome(it)
            }
        )
    }

    private object Templates {
        private val BASE = Template(
            generation = { generation ->
                generation.surfaceBuilder(ConfiguredSurfaceBuilders.GRASS)
                DefaultBiomeFeatures.addLandCarvers(generation)
                DefaultBiomeFeatures.addDefaultUndergroundStructures(generation)
                DefaultBiomeFeatures.addDungeons(generation)
                DefaultBiomeFeatures.addMineables(generation)
                DefaultBiomeFeatures.addDefaultOres(generation)
                DefaultBiomeFeatures.addDefaultDisks(generation)
                DefaultBiomeFeatures.addSprings(generation)
                DefaultBiomeFeatures.addFrozenTopLayer(generation)
                DefaultBiomeFeatures.addDefaultFlowers(generation)
                DefaultBiomeFeatures.addDefaultMushrooms(generation)
                DefaultBiomeFeatures.addDefaultVegetation(generation)
            },
            spawn = { spawn -> DefaultBiomeFeatures.addBatsAndMonsters(spawn) },
            effects = { effects ->
                effects.waterColor(0x3F76E4).waterFogColor(0x050533).fogColor(0xC0D8FF).moodSound(BiomeMoodSound.CAVE)
            },
            biome = { biome -> biome.precipitation(Biome.Precipitation.RAIN) }
        )

        private val LAKE = Template(
            generation = { generation ->
                DefaultBiomeFeatures.addWaterBiomeOakTrees(generation)
                DefaultBiomeFeatures.addDefaultGrass(generation)
                generation.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.SEAGRASS_RIVER)
            },
            spawn = { spawn ->
                spawn.spawn(SpawnGroup.WATER_CREATURE, SpawnSettings.SpawnEntry(EntityType.SQUID, 2, 1, 4))
                spawn.spawn(SpawnGroup.WATER_CREATURE, SpawnSettings.SpawnEntry(EntityType.SALMON, 5, 1, 5))
            },
            biome = { biome ->
                biome.depth(-0.45f).scale(0f)
                    .category(Biome.Category.RIVER)
                    .downfall(0.8F)
            }
        ).parent(BASE)

        private val ISLAND = Template(
            spawn = { spawn ->
                DefaultBiomeFeatures.addFarmAnimals(spawn)
                spawn.spawn(SpawnGroup.CREATURE, SpawnSettings.SpawnEntry(EntityType.WOLF, 8, 4, 4))
                spawn.spawn(SpawnGroup.CREATURE, SpawnSettings.SpawnEntry(EntityType.RABBIT, 4, 2, 3))
            },
            biome = { biome ->
                biome.depth(0.125f).scale(0.05f)
            }
        ).parent(BASE)

        val WARM_LAKE = Template(
            generation = { generation ->
                DefaultBiomeFeatures.addKelp(generation)
                generation.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_WATERLILLY)
            }
        ).parent(LAKE)

        val JUNGLE_LAKE = Template(
            generation = { generation ->
                DefaultBiomeFeatures.addKelp(generation)
                generation.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_WATERLILLY)
            },
            spawn = { spawn ->
                spawn.spawn(SpawnGroup.WATER_CREATURE, SpawnSettings.SpawnEntry(EntityType.PUFFERFISH, 15, 1, 3))
                spawn.spawn(SpawnGroup.WATER_CREATURE, SpawnSettings.SpawnEntry(EntityType.TROPICAL_FISH, 25, 8, 8))
            }
        ).parent(LAKE)

        val COLD_LAKE = Template(
            biome = { biome ->
                biome.downfall(0.3f)
            }
        ).parent(LAKE)

        val MOUNTAIN_LAKE = Template(
            biome = { biome ->
                biome.downfall(0.3f)
            }
        ).parent(LAKE)

        val FOREST_ISLAND = Template(
            generation = { generation ->
                DefaultBiomeFeatures.addForestFlowers(generation)
                DefaultBiomeFeatures.addDungeons(generation)
                DefaultBiomeFeatures.addForestTrees(generation)
                DefaultBiomeFeatures.addForestGrass(generation)
            },
            biome = { biome ->
                biome.downfall(0.8f).category(Biome.Category.FOREST)
            }
        ).parent(ISLAND)

        val TAIGA_ISLAND = Template(
            generation = { generation ->
                DefaultBiomeFeatures.addLargeFerns(generation)
                DefaultBiomeFeatures.addDungeons(generation)
                DefaultBiomeFeatures.addTaigaTrees(generation)
                DefaultBiomeFeatures.addTaigaGrass(generation)
                DefaultBiomeFeatures.addSweetBerryBushes(generation)
                DefaultBiomeFeatures.addExtraMountainTrees(generation)
            },
            biome = { biome ->
                biome.depth(0.2f).scale(0.2f).downfall(0.8f).category(Biome.Category.TAIGA)
            }
        ).parent(ISLAND)

        val JUNGLE_ISLAND = Template(
            generation = { generation ->
                DefaultBiomeFeatures.addBamboo(generation)
                DefaultBiomeFeatures.addJungleEdgeTrees(generation)
                DefaultBiomeFeatures.addJungleGrass(generation)
                DefaultBiomeFeatures.addJungleVegetation(generation)
            },
            spawn = { spawn ->
                spawn.spawn(SpawnGroup.CREATURE, SpawnSettings.SpawnEntry(EntityType.PARROT, 40, 1, 2))
                spawn.spawn(SpawnGroup.CREATURE, SpawnSettings.SpawnEntry(EntityType.PANDA, 1, 1, 2))
            },
            biome = { biome ->
                biome.depth(0.1f).scale(0.3f).downfall(0.9f).category(Biome.Category.JUNGLE)
            }
        ).parent(ISLAND)
    }
}
