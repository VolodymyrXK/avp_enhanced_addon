package com.enhanced.avp;

import net.minecraftforge.common.config.Configuration;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс для управления настройками аддона.
 * Позволяет настраивать каждую сущность по её ID.
 */
public class ConfigHandler {
    public static Configuration config;
    
    // Категории конфига
    public static final String CAT_GENERAL = "general";
    public static final String CAT_ENTITIES = "entities";
    public static final String CAT_HIVE = "hive_logic";

    // Общие настройки
    public static boolean predatorsAttackAliens;
    
    // Настройки улья
    public static int hiveConstructionMinutes;
    public static int initialEggCount;
    public static int eggSpawnIntervalSeconds;

    // Хранилище настроек для каждой сущности
    public static class EntityConfig {
        public double maxHealth;
        public double attackDamage;
        public double moveSpeed;
        public double jumpPower;
        public double visionRange;
        public boolean canClimb;
        public boolean canBreakBlocks;
        public int spawnWeight;
        public boolean allowInOverworld;
    }

    public static Map<String, EntityConfig> entitySettings = new HashMap<>();

    public static void init(File configFile) {
        config = new Configuration(configFile);
        load();
    }

    public static void load() {
        config.load();

        // Общие
        predatorsAttackAliens = config.getBoolean("PredatorsAttackAliens", CAT_GENERAL, true, "Должны ли хищники атаковать чужих по умолчанию");

        // Улей
        hiveConstructionMinutes = config.getInt("HiveConstructionMinutes", CAT_HIVE, 7, 1, 60, "Сколько минут королева строит улей");
        initialEggCount = config.getInt("InitialEggCount", CAT_HIVE, 5, 0, 20, "Сколько яиц спавнится сразу после постройки");
        eggSpawnIntervalSeconds = config.getInt("EggSpawnIntervalSeconds", CAT_HIVE, 30, 5, 300, "Интервал появления яиц в секундах");

        // Список всех сущностей (базовый набор для генерации конфига)
        String[] defaultIds = {
            "avp:drone", "avp:warrior", "avp:praetorian", "avp:queen", 
            "avp:yautja", "avp:yautja_berserker", "avp:marine"
        };

        for (String id : defaultIds) {
            EntityConfig ec = new EntityConfig();
            String cat = CAT_ENTITIES + "." + id.replace(":", "_");
            
            ec.maxHealth = config.get(cat, "MaxHealth", -1.0, "Максимальное здоровье (-1 - оставить как в моде)").getDouble();
            ec.attackDamage = config.get(cat, "AttackDamage", -1.0, "Урон (-1 - оставить как в моде)").getDouble();
            ec.moveSpeed = config.get(cat, "MovementSpeed", -1.0, "Скорость бега (-1 - оставить как в моде)").getDouble();
            ec.jumpPower = config.get(cat, "JumpPower", 0.42, "Сила прыжка (базовая 0.42)").getDouble();
            ec.visionRange = config.get(cat, "VisionRange", -1.0, "Дальность зрения (-1 - оставить как в моде)").getDouble();
            ec.canClimb = config.getBoolean("CanClimbWalls", cat, id.contains("drone") || id.contains("queen"), "Может ли ползать по стенам");
            ec.canBreakBlocks = config.getBoolean("CanBreakBlocks", cat, id.contains("queen"), "Может ли ломать блоки");
            ec.spawnWeight = config.getInt("SpawnWeight", cat, 20, 0, 100, "Вес спавна");
            ec.allowInOverworld = config.getBoolean("AllowInOverworld", cat, true, "Разрешить спавн в обычном мире");
            
            entitySettings.put(id, ec);
        }

        if (config.hasChanged()) {
            config.save();
        }
    }
}
