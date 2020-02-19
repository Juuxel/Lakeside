package juuxel.lakeside.config

import net.fabricmc.loader.api.FabricLoader
import java.nio.file.Files
import java.util.Properties

data class Config(
    val disableVanillaLakes: Boolean = true
) {
    fun save(properties: Properties) {
        properties.setProperty("disable_vanilla_lakes", disableVanillaLakes.toString())
    }

    companion object {
        val INSTANCE: Config by lazy(::load)
            @JvmStatic @JvmName("get") get

        private val CONFIG_DESCRIPTION: String =
            """
               | Lakeside options:
               | disable_vanilla_lakes - Disables vanilla lakes (default: true) 
            """.trimMargin()

        private fun load(): Config {
            val path = FabricLoader.getInstance().configDirectory.toPath().resolve("Lakeside.properties")
            val properties = Properties()

            if (Files.notExists(path)) {
                val config = Config()
                config.save(properties)

                Files.newBufferedWriter(path).use {
                    properties.store(it, CONFIG_DESCRIPTION)
                }

                return config
            }

            Files.newBufferedReader(path).use { properties.load(it) }
            return load(properties)
        }

        private fun load(properties: Properties): Config =
            Config(
                disableVanillaLakes = properties.getProperty("disable_vanilla_lakes")?.toBoolean() ?: true
            )
    }
}
