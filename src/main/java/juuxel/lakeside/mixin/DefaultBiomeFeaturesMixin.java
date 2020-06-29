package juuxel.lakeside.mixin;

import juuxel.lakeside.biome.BiomeTracker;
import juuxel.lakeside.config.Config;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DefaultBiomeFeatures.class)
public class DefaultBiomeFeaturesMixin {
    @Redirect(
        method = "addDefaultLakes",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/biome/Biome;addFeature(Lnet/minecraft/world/gen/GenerationStep$Feature;Lnet/minecraft/world/gen/feature/ConfiguredFeature;)V",
            ordinal = 0
        )
    )
    private static void lakeside_redirectDefaultLakes(Biome biome, GenerationStep.Feature step, ConfiguredFeature<?, ?> feature) {
        // If I used OverworldBiomes here, it would NPE because not all biomes are initialized
        // in the vanilla biome array
        BiomeTracker.INSTANCE.addBiomeWithLakes(biome);

        if (!Config.get().getDisableVanillaLakes()) {
            biome.addFeature(step, feature);
        }
    }
}
