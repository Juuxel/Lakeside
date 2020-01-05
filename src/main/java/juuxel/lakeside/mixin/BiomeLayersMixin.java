package juuxel.lakeside.mixin;

import juuxel.lakeside.layer.AddMediumVariantsLayer;
import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.biome.layer.ScaleLayer;
import net.minecraft.world.biome.layer.util.LayerFactory;
import net.minecraft.world.biome.layer.util.LayerSampleContext;
import net.minecraft.world.biome.layer.util.LayerSampler;
import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;
import net.minecraft.world.level.LevelGeneratorType;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.function.LongFunction;

@Mixin(BiomeLayers.class)
public class BiomeLayersMixin {
    @ModifyVariable(
            method = "build(Lnet/minecraft/world/level/LevelGeneratorType;Lnet/minecraft/world/gen/chunk/OverworldChunkGeneratorConfig;Ljava/util/function/LongFunction;)Lnet/minecraft/world/biome/layer/util/LayerFactory;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/layer/EaseBiomeEdgeLayer;create(Lnet/minecraft/world/biome/layer/util/LayerSampleContext;Lnet/minecraft/world/biome/layer/util/LayerFactory;)Lnet/minecraft/world/biome/layer/util/LayerFactory;"),
            ordinal = 3
    )
    private static <T extends LayerSampler, C extends LayerSampleContext<T>> LayerFactory<T> addMediumVariants(LayerFactory<T> layer, LevelGeneratorType generatorType, OverworldChunkGeneratorConfig settings, LongFunction<C> contextProvider) {
        LayerFactory<T> variants = AddMediumVariantsLayer.INSTANCE.create(contextProvider.apply(2000L), layer);
        return ScaleLayer.NORMAL.create(contextProvider.apply(1001L), variants);
    }

    @ModifyArg(
            method = "build(Lnet/minecraft/world/level/LevelGeneratorType;Lnet/minecraft/world/gen/chunk/OverworldChunkGeneratorConfig;Ljava/util/function/LongFunction;)Lnet/minecraft/world/biome/layer/util/LayerFactory;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/layer/BiomeLayers;stack(JLnet/minecraft/world/biome/layer/type/ParentedLayer;Lnet/minecraft/world/biome/layer/util/LayerFactory;ILjava/util/function/LongFunction;)Lnet/minecraft/world/biome/layer/util/LayerFactory;"),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/layer/AddBambooJungleLayer;create(Lnet/minecraft/world/biome/layer/util/LayerSampleContext;Lnet/minecraft/world/biome/layer/util/LayerFactory;)Lnet/minecraft/world/biome/layer/util/LayerFactory;"),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/layer/EaseBiomeEdgeLayer;create(Lnet/minecraft/world/biome/layer/util/LayerSampleContext;Lnet/minecraft/world/biome/layer/util/LayerFactory;)Lnet/minecraft/world/biome/layer/util/LayerFactory;")
            ),
            index = 3
    )
    private static int modifyStack(int original) {
        return 1;
    }
}
