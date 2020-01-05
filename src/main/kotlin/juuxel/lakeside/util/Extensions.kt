package juuxel.lakeside.util

import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.minecraft.world.biome.layer.type.ParentedLayer
import net.minecraft.world.biome.layer.util.LayerFactory
import net.minecraft.world.biome.layer.util.LayerSampleContext
import net.minecraft.world.biome.layer.util.LayerSampler
import java.util.*
import java.util.function.LongFunction

inline fun <T> Registry<T>.visit(crossinline visitor: (Identifier, T, Int) -> Unit) {
    for (t in this) {
        visitor(getId(t) ?: throw IllegalStateException("Could not find ID for supposedly present registry entry: $t"), t, getRawId(t))
    }

    RegistryEntryAddedCallback.event(this).register(RegistryEntryAddedCallback { rawId, id, value -> visitor(id, value, rawId) })
}

fun <R : LayerSampler, T : LayerSampleContext<R>> ParentedLayer.stack(times: Int, contextProvider: LongFunction<T>, initialSalt: Long, parent: LayerFactory<R>): LayerFactory<R> =
    (0 until times).fold(parent) { acc, i ->
        create(contextProvider.apply(initialSalt + i), acc)
    }
