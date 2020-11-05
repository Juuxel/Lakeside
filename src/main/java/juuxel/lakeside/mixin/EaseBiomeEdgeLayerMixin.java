package juuxel.lakeside.mixin;

import juuxel.lakeside.layer.LayerHelper;
import net.minecraft.world.biome.layer.EaseBiomeEdgeLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EaseBiomeEdgeLayer.class)
abstract class EaseBiomeEdgeLayerMixin {
    @Inject(method = "sample", at = @At("HEAD"), cancellable = true)
    private void onSample(LayerRandomnessSource context, int n, int e, int s, int w, int center, CallbackInfoReturnable<Integer> info) {
        int smallVariants = LayerHelper.INSTANCE.transformSmallVariant(context, n, e, s, w, center);
        if (smallVariants != center) {
            info.setReturnValue(smallVariants);
        }
    }
}
