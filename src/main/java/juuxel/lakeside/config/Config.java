package juuxel.lakeside.config;

import com.google.common.base.Suppliers;
import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.Properties;
import java.util.function.Supplier;

public final class Config {
    private static final String CONFIG_DESCRIPTION =
        """
        Lakeside options:
        disable_vanilla_lakes - Disables vanilla lakes (default: true)
        """;

    private static final Supplier<Config> LAZY_INSTANCE = Suppliers.memoize(Config::load);

    public static Config get() {
        return LAZY_INSTANCE.get();
    }

    public boolean disableVanillaLakes = true;

    private void save(Properties properties) {
        properties.setProperty("disable_vanilla_lakes", Boolean.toString(disableVanillaLakes));
    }

    private void load(Properties properties) {
        disableVanillaLakes = Boolean.parseBoolean(properties.getProperty("disable_vanilla_lakes"));
    }

    private static Config load() {
        var path = FabricLoader.getInstance().getConfigDir().resolve("Lakeside.properties");
        var properties = new Properties();
        var config = new Config();

        if (Files.notExists(path)) {
            config.save(properties);

            try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                properties.store(writer, CONFIG_DESCRIPTION);
            } catch (IOException e) {
                throw new UncheckedIOException("Could not save Lakeside config", e);
            }
        } else {
            try (BufferedReader reader = Files.newBufferedReader(path)) {
                properties.load(reader);
            } catch (IOException e) {
                throw new UncheckedIOException("Could not load Lakeside config", e);
            }

            config.load(properties);
        }

        return config;
    }
}
