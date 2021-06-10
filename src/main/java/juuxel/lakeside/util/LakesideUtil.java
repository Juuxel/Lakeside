package juuxel.lakeside.util;

import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.minecraft.util.registry.Registry;

import java.util.function.Consumer;

public final class LakesideUtil {
    public static <T> void visit(Registry<T> registry, Consumer<T> callback) {
        registry.forEach(callback);
        RegistryEntryAddedCallback.event(registry).register((rawId, id, entry) -> callback.accept(entry));
    }
}
