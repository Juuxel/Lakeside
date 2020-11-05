package juuxel.lakeside.mixin;

import juuxel.lakeside.biome.GenerationSettingsDuck;
import net.minecraft.world.biome.GenerationSettings;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(GenerationSettings.class)
abstract class GenerationSettingsMixin implements GenerationSettingsDuck {
    private boolean lakeside_hasLake;

    @Override
    public boolean getLakeside_hasLake() {
        return lakeside_hasLake;
    }

    @Override
    public void setLakeside_hasLake(boolean hasLake) {
        this.lakeside_hasLake = hasLake;
    }
}
