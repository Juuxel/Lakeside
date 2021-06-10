package juuxel.lakeside.layer;

import juuxel.lakeside.biome.MoreOverworldBiomes;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

import java.util.function.BiFunction;

public final class LayerHelper {
    public static int transformSmallVariant(LayerRandomnessSource context, int n, int e, int s, int w, int center) {
        return transform(context, n, e, s, w, center, MoreOverworldBiomes::transformSmallVariant);
    }

    public static int transformIsland(LayerRandomnessSource context, int n, int e, int s, int w, int center) {
        return transform(context, n, e, s, w, center, MoreOverworldBiomes::transformIsland);
    }

    private static int transform(
        LayerRandomnessSource context,
        int n, int e, int s, int w,
        int center, BiFunction<Integer, LayerRandomnessSource, Integer> transformer
    ) {
        var neighborCount = 0;
        if (n == center) neighborCount++;
        if (e == center) neighborCount++;
        if (s == center) neighborCount++;
        if (w == center) neighborCount++;

        if (neighborCount >= 2) {
            int transformed = transformer.apply(center, context);
            if (transformed != center) {
                return transformed;
            }
        }

        return center;
    }
}
