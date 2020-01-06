package juuxel.lakeside.mixin;

import juuxel.lakeside.biome.BiomeTracker;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DefaultBiomeFeatures.class)
public class DefaultBiomeFeaturesMixin {
    @Inject(method = "addDefaultLakes", at = @At("HEAD"), cancellable = true)
    private static void replaceLakes(Biome biome, CallbackInfo info) {
        // If I used OverworldBiomes here, it would NPE because not all biomes are initialized
        // in the vanilla biome array
        BiomeTracker.INSTANCE.addBiomeWithLakes(biome);

        // TODO: Add config option
        info.cancel();
    }
}
