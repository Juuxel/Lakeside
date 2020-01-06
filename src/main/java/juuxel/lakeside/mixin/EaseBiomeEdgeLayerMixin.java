package juuxel.lakeside.mixin;

import juuxel.lakeside.layer.AddSmallVariantsLayer;
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
        // Second round of small variants
        int smallVariants = AddSmallVariantsLayer.INSTANCE.sample(context, n, e, s, w, center);
        if (smallVariants != center) {
            info.setReturnValue(smallVariants);
        }
    }
}
