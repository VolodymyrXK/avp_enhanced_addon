package com.enhanced.avp;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntityMob;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.alien.common.entity.living.SpeciesAlien;
import org.alien.common.entity.living.xenomorph.EntityMatriarch;
import org.predator.common.entity.living.yautja.EntityYautja;

/**
 * Обработчик всех событий Minecraft для аддона.
 */
public class EntityEventHandler {

    @SubscribeEvent
    public void onEntityJoin(EntityJoinWorldEvent event) {
        if (event.getWorld().isRemote) return;
        
        Entity entity = event.getEntity();
        String id = entity.getClass().getName(); // В реальности лучше использовать EntityRegistry
        
        // Попытка найти настройки по ID (упрощенно)
        ConfigHandler.EntityConfig cfg = findConfig(entity);
        
        if (cfg != null) {
            // Контроль спавна в обычном мире
            if (event.getWorld().field_73011_i.getDimension() == 0 && !cfg.allowInOverworld) {
                event.setCanceled(true);
                return;
            }

            if (entity instanceof EntityLiving) {
                EntityLiving living = (EntityLiving)entity;
                
                // Применение характеристик
                applyAttributes(living, cfg);
                
                // Настройка атаки Хищников на Чужих
                if (ConfigHandler.predatorsAttackAliens && living instanceof EntityYautja) {
                    living.field_70714_bg.func_75776_a(2, new EntityAINearestAttackableTarget(living, SpeciesAlien.class, true));
                }
            }
        }
    }

    @SubscribeEvent
    public void onEntityUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving().field_70170_p.field_72995_K) return;
        
        EntityLiving living = (EntityLiving)event.getEntityLiving();
        ConfigHandler.EntityConfig cfg = findConfig(living);
        
        if (cfg != null) {
            // 1. Способности AI
            if (cfg.canClimb) Utils.doClimbing(living);
            if (cfg.canBreakBlocks) Utils.doBreakBlocks(living);
            if (cfg.jumpPower > 0) Utils.doLeap(living, cfg.jumpPower);
            
            // 2. Специфическая логика королевы
            if (living instanceof EntityMatriarch) {
                BuildingLogic.updateQueen((EntityMatriarch)living);
            }
        }
    }

    private void applyAttributes(EntityLiving entity, ConfigHandler.EntityConfig cfg) {
        if (cfg.maxHealth > 0) {
            entity.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(cfg.maxHealth);
            entity.func_70606_j((float)cfg.maxHealth); // setHealth
        }
        if (cfg.attackDamage > 0 && entity.func_110148_a(SharedMonsterAttributes.field_111265_b) != null) {
            entity.func_110148_a(SharedMonsterAttributes.field_111265_b).func_111128_a(cfg.attackDamage);
        }
        if (cfg.moveSpeed > 0) {
            entity.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(cfg.moveSpeed);
        }
        if (cfg.visionRange > 0) {
            entity.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111128_a(cfg.visionRange);
        }
    }

    private ConfigHandler.EntityConfig findConfig(Entity entity) {
        // Здесь должна быть логика поиска по ID из EntityList
        // Для примера поищем по части имени класса
        for (Map.Entry<String, ConfigHandler.EntityConfig> entry : ConfigHandler.entitySettings.entrySet()) {
            if (entity.getClass().getSimpleName().toLowerCase().contains(entry.getKey().split(":")[1])) {
                return entry.getValue();
            }
        }
        return null;
    }
}
