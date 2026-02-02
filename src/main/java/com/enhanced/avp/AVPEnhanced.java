package com.enhanced.avp;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

/**
 * Главный класс аддона AVP-Enhanced.
 * Этот мод расширяет возможности оригинального AVP мода через события Forge.
 */
@Mod(modid = AVPEnhanced.MODID, name = AVPEnhanced.NAME, version = AVPEnhanced.VERSION, dependencies = "required-after:avp")
public class AVPEnhanced {
    public static final String MODID = "avpenhanced";
    public static final String NAME = "AVP Enhanced Addon";
    public static final String VERSION = "1.0";

    public static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        logger.info("[AVP-Enhanced] Загрузка конфигурации...");
        ConfigHandler.init(event.getSuggestedConfigurationFile());
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        logger.info("[AVP-Enhanced] Регистрация обработчиков событий...");
        // Регистрация основного обработчика событий
        MinecraftForge.EVENT_BUS.register(new EntityEventHandler());
        logger.info("[AVP-Enhanced] Аддон успешно инициализирован!");
    }
}
