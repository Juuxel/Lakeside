package juuxel.lakeside;

import juuxel.lakeside.biome.LakesideBiomes;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public final class Lakeside implements ModInitializer {
    public static final String ID = "lakeside";

    public static Identifier id(String path) {
        return new Identifier(ID, path);
    }

    @Override
    public void onInitialize() {
        LakesideBiomes.init();
    }
}
