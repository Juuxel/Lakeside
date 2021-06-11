package juuxel.lakeside.biome;

import com.mojang.serialization.Lifecycle;
import juuxel.lakeside.Lakeside;
import juuxel.lakeside.mixin.BuiltinBiomesAccessor;
import juuxel.lakeside.mixin.DefaultBiomeCreatorAccessor;
import juuxel.lakeside.util.LakesideUtil;
import net.fabricmc.fabric.api.biome.v1.OverworldBiomes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.util.Util;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.ConfiguredFeatures;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilders;

import java.util.function.Consumer;

public final class LakesideBiomes {
    public static final RegistryKey<Biome> WARM_LAKE = key("warm_lake");
    public static final RegistryKey<Biome> JUNGLE_LAKE = key("jungle_lake");
    public static final RegistryKey<Biome> COLD_LAKE = key("cold_lake");
    public static final RegistryKey<Biome> MOUNTAIN_LAKE = key("mountain_lake");
    public static final RegistryKey<Biome> FOREST_ISLAND = key("forest_island");
    public static final RegistryKey<Biome> TAIGA_ISLAND = key("taiga_island");
    public static final RegistryKey<Biome> JUNGLE_ISLAND = key("jungle_island");

    public static void init() {
        register(WARM_LAKE, createBiome(Templates.WARM_LAKE, 0.7f));
        register(JUNGLE_LAKE, createBiome(Templates.JUNGLE_LAKE, 0.8f));
        register(COLD_LAKE, createBiome(Templates.COLD_LAKE, 0.2f));
        register(MOUNTAIN_LAKE, createBiome(Templates.MOUNTAIN_LAKE, 0.2f));
        register(FOREST_ISLAND, createBiome(Templates.FOREST_ISLAND, 0.7f));
        register(TAIGA_ISLAND, createBiome(Templates.TAIGA_ISLAND, 0.25f));
        register(JUNGLE_ISLAND, createBiome(Templates.JUNGLE_ISLAND, 0.95f));

        // Beaches
        OverworldBiomes.addEdgeBiome(MOUNTAIN_LAKE, BiomeKeys.STONE_SHORE, 1.0);
        OverworldBiomes.addEdgeBiome(JUNGLE_LAKE, BiomeKeys.JUNGLE_EDGE, 1.0);
        // OverworldBiomes.addEdgeBiome(FOREST_ISLAND, Biomes.BEACH, 1.0)
        OverworldBiomes.addShoreBiome(MOUNTAIN_LAKE, TAIGA_ISLAND, 1.0);
        OverworldBiomes.addShoreBiome(COLD_LAKE, TAIGA_ISLAND, 1.0);
        OverworldBiomes.addShoreBiome(WARM_LAKE, FOREST_ISLAND, 1.0);
        OverworldBiomes.addShoreBiome(JUNGLE_LAKE, JUNGLE_ISLAND, 1.0);

        // Islands
        MoreOverworldBiomes.addSmallVariant(COLD_LAKE, TAIGA_ISLAND, 4);
        MoreOverworldBiomes.addSmallVariant(MOUNTAIN_LAKE, TAIGA_ISLAND, 4);
        MoreOverworldBiomes.addSmallVariant(WARM_LAKE, FOREST_ISLAND, 4);
        MoreOverworldBiomes.addSmallVariant(JUNGLE_LAKE, JUNGLE_ISLAND, 4);
        MoreOverworldBiomes.addIsland(COLD_LAKE, TAIGA_ISLAND, 4);
        MoreOverworldBiomes.addIsland(MOUNTAIN_LAKE, TAIGA_ISLAND, 4);
        MoreOverworldBiomes.addIsland(WARM_LAKE, FOREST_ISLAND, 4);
        MoreOverworldBiomes.addIsland(JUNGLE_LAKE, JUNGLE_ISLAND, 4);

        LakesideUtil.visit(BuiltinRegistries.BIOME, biome -> {
            if (BiomeTracker.hasLakes(biome)) {
                registerLakes(biome);
            }

            if (biome.getCategory() == Biome.Category.OCEAN) {
                registerOceanIslands(biome);
            }
        });
    }

    private static RegistryKey<Biome> key(String id) {
        return RegistryKey.of(Registry.BIOME_KEY, Lakeside.id(id));
    }

    private static RegistryKey<Biome> getKey(Biome biome) {
        return BuiltinRegistries.BIOME.getKey(biome)
            .orElseThrow(() -> new RuntimeException("Key not found for biome $biome!"));
    }

    private static Biome createBiome(Template template, float temperature) {
        return Util.make(
            new Biome.Builder()
                .generationSettings(Util.make(new GenerationSettings.Builder(), template.generation).build())
                .spawnSettings(Util.make(new SpawnSettings.Builder(), template.spawn).build())
                .effects(Util.make(new BiomeEffects.Builder(), template.effects).skyColor(DefaultBiomeCreatorAccessor.callGetSkyColor(temperature)).build()),
            template.biome
        )
            .temperature(temperature)
            .build();
    }

    // Copied verbatim from Woods and Mires.
    private static void register(RegistryKey<Biome> key, Biome biome) {
        ((MutableRegistry<Biome>) BuiltinRegistries.BIOME).add(key, biome, Lifecycle.stable());

        // Ensures that the biome is stored in the internal raw ID map of BuiltinBiomes.
        // Fabric API usually does this, but some of my biomes don't go through OverworldBiomes at all,
        // which means that won't always get done.
        BuiltinBiomesAccessor.getBY_RAW_ID().put(BuiltinRegistries.BIOME.getRawId(biome), key);
    }

    private static void registerLakes(Biome biome) {
        var key = getKey(biome);
        var category = biome.getCategory();

        if (category == Biome.Category.RIVER || category == Biome.Category.OCEAN) {
            // Ignore water biomes
            return;
        }

        switch (category) {
            case EXTREME_HILLS:
                MoreOverworldBiomes.addSmallVariant(key, MOUNTAIN_LAKE, 45);
                OverworldBiomes.addBiomeVariant(key, MOUNTAIN_LAKE, 0.05);
                break;

            case JUNGLE:
                MoreOverworldBiomes.addSmallVariant(key, JUNGLE_LAKE, 25);
                OverworldBiomes.addBiomeVariant(key, JUNGLE_LAKE, 0.05);
                break;

            case ICY:
            case TAIGA:
                MoreOverworldBiomes.addSmallVariant(key, COLD_LAKE, 25);
                OverworldBiomes.addBiomeVariant(key, COLD_LAKE, 0.05);
                break;

            default:
                MoreOverworldBiomes.addSmallVariant(key, WARM_LAKE, 25);
                OverworldBiomes.addBiomeVariant(key, WARM_LAKE, 0.05);
                break;
        }
    }

    private static void registerOceanIslands(Biome biome) {
        var key = getKey(biome);

        MoreOverworldBiomes.addSmallVariant(key, TAIGA_ISLAND, 20);
        MoreOverworldBiomes.addSmallVariant(key, FOREST_ISLAND, 20);
    }

    private record Template(
        Consumer<GenerationSettings.Builder> generation,
        Consumer<SpawnSettings.Builder> spawn,
        Consumer<BiomeEffects.Builder> effects,
        Consumer<Biome.Builder> biome
    ) {
        Template parent(Template parent) {
            return new Template(
                generation -> {
                    parent.generation.accept(generation);
                    this.generation.accept(generation);
                },
                spawn -> {
                    parent.spawn.accept(spawn);
                    this.spawn.accept(spawn);
                },
                effects -> {
                    parent.effects.accept(effects);
                    this.effects.accept(effects);
                },
                biome -> {
                    parent.biome.accept(biome);
                    this.biome.accept(biome);
                }
            );
        }
    }

    private static final class Templates {
        static final Template BASE = new Template(
            generation -> {
                generation.surfaceBuilder(ConfiguredSurfaceBuilders.GRASS);
                DefaultBiomeFeatures.addLandCarvers(generation);
                DefaultBiomeFeatures.addDefaultUndergroundStructures(generation);
                DefaultBiomeFeatures.addDungeons(generation);
                DefaultBiomeFeatures.addMineables(generation);
                DefaultBiomeFeatures.addDefaultOres(generation);
                DefaultBiomeFeatures.addDefaultDisks(generation);
                DefaultBiomeFeatures.addSprings(generation);
                DefaultBiomeFeatures.addFrozenTopLayer(generation);
                DefaultBiomeFeatures.addDefaultFlowers(generation);
                DefaultBiomeFeatures.addDefaultMushrooms(generation);
                DefaultBiomeFeatures.addDefaultVegetation(generation);
            },
            spawn -> DefaultBiomeFeatures.addBatsAndMonsters(spawn),
            effects ->  effects.waterColor(0x3F76E4).waterFogColor(0x050533).fogColor(0xC0D8FF).moodSound(BiomeMoodSound.CAVE),
            biome -> biome.precipitation(Biome.Precipitation.RAIN)
        );

        static final Template LAKE = new Template(
            generation -> {
                DefaultBiomeFeatures.addWaterBiomeOakTrees(generation);
                DefaultBiomeFeatures.addDefaultGrass(generation);
                generation.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.SEAGRASS_RIVER);
            },
            spawn -> {
                spawn.spawn(SpawnGroup.WATER_CREATURE, new SpawnSettings.SpawnEntry(EntityType.SQUID, 2, 1, 4));
                spawn.spawn(SpawnGroup.WATER_CREATURE, new SpawnSettings.SpawnEntry(EntityType.SALMON, 5, 1, 5));
            },
            effects -> {},
            biome -> biome.depth(-0.45f).scale(0f)
                .category(Biome.Category.RIVER)
                .downfall(0.8F)
        ).parent(BASE);

        static final Template ISLAND = new Template(
            generation -> {},
            spawn -> {
                DefaultBiomeFeatures.addFarmAnimals(spawn);
                spawn.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.WOLF, 8, 4, 4));
                spawn.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.RABBIT, 4, 2, 3));
            },
            effects -> {},
            biome -> biome.depth(0.125f).scale(0.05f)
        ).parent(BASE);

        static final Template WARM_LAKE = new Template(
            generation -> {
                DefaultBiomeFeatures.addKelp(generation);
                generation.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_WATERLILLY);
            },
            spawn -> {},
            effects -> {},
            biome -> {}
        ).parent(LAKE);

        static final Template JUNGLE_LAKE = new Template(
            generation -> {
                DefaultBiomeFeatures.addKelp(generation);
                generation.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_WATERLILLY);
            },
            spawn -> {},
            effects -> {},
            biome -> {}
        ).parent(LAKE);

        static final Template COLD_LAKE = new Template(
            generation -> {},
            spawn -> {},
            effects -> {},
            biome -> biome.downfall(0.3f)
        ).parent(LAKE);

        static final Template MOUNTAIN_LAKE = new Template(
            generation -> {},
            spawn -> {},
            effects -> {},
            biome -> biome.downfall(0.3f)
        ).parent(LAKE);

        static final Template FOREST_ISLAND = new Template(
            generation -> {
                DefaultBiomeFeatures.addForestFlowers(generation);
                DefaultBiomeFeatures.addDungeons(generation);
                DefaultBiomeFeatures.addForestTrees(generation);
                DefaultBiomeFeatures.addForestGrass(generation);
            },
            spawn -> {},
            effects -> {},
            biome -> biome.downfall(0.8f).category(Biome.Category.FOREST)
        ).parent(ISLAND);

        static final Template TAIGA_ISLAND = new Template(
            generation -> {
                DefaultBiomeFeatures.addLargeFerns(generation);
                DefaultBiomeFeatures.addDungeons(generation);
                DefaultBiomeFeatures.addTaigaTrees(generation);
                DefaultBiomeFeatures.addTaigaGrass(generation);
                DefaultBiomeFeatures.addSweetBerryBushes(generation);
                DefaultBiomeFeatures.addExtraMountainTrees(generation);
            },
            spawn -> {},
            effects -> {},
            biome -> biome.depth(0.2f).scale(0.2f).downfall(0.8f).category(Biome.Category.TAIGA)
        ).parent(ISLAND);

        static final Template JUNGLE_ISLAND = new Template(
            generation -> {
                DefaultBiomeFeatures.addBamboo(generation);
                DefaultBiomeFeatures.addJungleEdgeTrees(generation);
                DefaultBiomeFeatures.addJungleGrass(generation);
                DefaultBiomeFeatures.addJungleVegetation(generation);
            },
            spawn -> {
                spawn.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.PARROT, 40, 1, 2));
                spawn.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.PANDA, 1, 1, 2));
            },
            effects -> {},
            biome -> biome.depth(0.1f).scale(0.3f).downfall(0.9f).category(Biome.Category.JUNGLE)
        ).parent(ISLAND);
    }
}
