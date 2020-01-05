package juuxel.lakeside.mixin;

import juuxel.lakeside.api.MoreOverworldBiomes;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.layer.EaseBiomeEdgeLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EaseBiomeEdgeLayer.class)
public class EaseBiomeEdgeLayerMixin {
    @Inject(method = "sample", at = @At("HEAD"), cancellable = true)
    private void onSample(LayerRandomnessSource context, int n, int e, int s, int w, int center, CallbackInfoReturnable<Integer> info) {
        if (n == center && e == center && s == center && w == center) {
            Biome base = Registry.BIOME.get(center);
            Biome transformed = MoreOverworldBiomes.INSTANCE.transformSubBiome(base, context);
            if (transformed != null) {
                info.setReturnValue(Registry.BIOME.getRawId(transformed));
            }
        }
    }
}
