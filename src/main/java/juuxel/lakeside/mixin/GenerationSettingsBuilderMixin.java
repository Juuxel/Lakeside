package juuxel.lakeside.mixin;

import juuxel.lakeside.biome.GenerationSettingsDuck;
import net.minecraft.world.biome.GenerationSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GenerationSettings.Builder.class)
abstract class GenerationSettingsBuilderMixin implements GenerationSettingsDuck {
    private boolean lakeside_hasLake = false;

    @Override
    public boolean getLakeside_hasLake() {
        return lakeside_hasLake;
    }

    @Override
    public void setLakeside_hasLake(boolean hasLake) {
        this.lakeside_hasLake = hasLake;
    }

    @Inject(method = "build", at = @At("RETURN"))
    private void lakeside_onBuild(CallbackInfoReturnable<GenerationSettings> info) {
        ((GenerationSettingsDuck) info.getReturnValue()).setLakeside_hasLake(lakeside_hasLake);
    }
}
