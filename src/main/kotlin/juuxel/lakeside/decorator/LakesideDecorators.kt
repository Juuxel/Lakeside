package juuxel.lakeside.decorator

import juuxel.lakeside.Lakeside
import net.minecraft.util.registry.Registry
import net.minecraft.world.gen.decorator.CountChanceDecoratorConfig
import net.minecraft.world.gen.decorator.Decorator
import net.minecraft.world.gen.decorator.DecoratorConfig

object LakesideDecorators {
    val COUNT_CHANCE_TOP_SOLID = CountChanceTopSolidDecorator(CountChanceDecoratorConfig::deserialize)

    fun init() {
        register("count_chance_top_solid", COUNT_CHANCE_TOP_SOLID)
    }

    private fun <DC : DecoratorConfig, D : Decorator<DC>> register(name: String, decorator: D): D =
        Registry.register(Registry.DECORATOR, Lakeside.id(name), decorator)
}
