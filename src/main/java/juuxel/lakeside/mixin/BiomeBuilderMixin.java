package juuxel.lakeside.mixin;

import juuxel.lakeside.biome.BiomeTracker;
import juuxel.lakeside.biome.GenerationSettingsDuck;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.GenerationSettings;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Biome.Builder.class)
abstract class BiomeBuilderMixin {
    @Shadow
    private @Nullable GenerationSettings generationSettings;

    @Inject(method = "build", at = @At("RETURN"))
    private void lakeside_onBuild(CallbackInfoReturnable<Biome> info) {
        GenerationSettingsDuck duck = (GenerationSettingsDuck) generationSettings;
        if (duck != null && duck.getLakeside_hasLake()) {
            BiomeTracker.INSTANCE.addBiomeWithLakes(info.getReturnValue());
        }
    }
}
