package juuxel.lakeside.mixin;

import juuxel.lakeside.biome.GenerationSettingsDuck;
import juuxel.lakeside.config.Config;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DefaultBiomeFeatures.class)
abstract class DefaultBiomeFeaturesMixin {
    @Redirect(
        method = "addDefaultLakes",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/biome/GenerationSettings$Builder;feature(Lnet/minecraft/world/gen/GenerationStep$Feature;Lnet/minecraft/world/gen/feature/ConfiguredFeature;)Lnet/minecraft/world/biome/GenerationSettings$Builder;",
            ordinal = 0
        )
    )
    private static GenerationSettings.Builder lakeside_redirectDefaultLakes(GenerationSettings.Builder builder, GenerationStep.Feature step, ConfiguredFeature<?, ?> feature) {
        // If I used OverworldBiomes here, it would NPE because not all biomes are initialized
        // in the vanilla biome array
        ((GenerationSettingsDuck) builder).setLakeside_hasLake(true);

        if (!Config.get().getDisableVanillaLakes()) {
            return builder.feature(step, feature);
        }

        return builder;
    }
}
