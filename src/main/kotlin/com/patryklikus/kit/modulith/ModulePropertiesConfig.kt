package com.patryklikus.kit.modulith

import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.boot.context.properties.ConfigurationPropertiesBean
import org.springframework.boot.context.properties.bind.Binder
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.core.Ordered
import org.springframework.modulith.core.ApplicationModules

/**
 * Prepends the owning module identifier to the prefix of every `@ConfigurationProperties`
 * bean whose class lives in a module's base package, then rebinds it.
 */
internal class ModulePropertiesConfig : BeanPostProcessor, Ordered, ApplicationContextAware {
    private lateinit var context: ApplicationContext
    private val modules by lazy { context.getBean(ApplicationModules::class.java) }
    private val binder by lazy { Binder.get(context.environment) }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.context = applicationContext
    }

    override fun getOrder(): Int = Ordered.HIGHEST_PRECEDENCE + 2

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any {
        val propsBean = ConfigurationPropertiesBean.get(context, bean, beanName) ?: return bean
        val packageName = bean::class.java.`package`?.name ?: return bean
        val module = modules.firstOrNull { packageName.startsWith(it.basePackage.name) } ?: return bean
        val prefix = propsBean.annotation.prefix
        val effective = if (prefix.isEmpty()) module.identifier.toString() else "${module.identifier}.$prefix"
        return binder.bindOrCreate(effective, propsBean.asBindTarget())
    }
}
