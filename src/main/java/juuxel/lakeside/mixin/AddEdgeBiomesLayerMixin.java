package juuxel.lakeside.mixin;

import juuxel.lakeside.layer.LayerHelper;
import net.minecraft.world.biome.layer.AddEdgeBiomesLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AddEdgeBiomesLayer.class)
abstract class AddEdgeBiomesLayerMixin {
    @Inject(method = "sample", at = @At("RETURN"), cancellable = true)
    private void onSample(LayerRandomnessSource context, int n, int e, int s, int w, int center, CallbackInfoReturnable<Integer> info) {
        int smallVariants = LayerHelper.transformIsland(context, n, e, s, w, center);
        if (smallVariants != center) {
            info.setReturnValue(smallVariants);
        }
    }
}
