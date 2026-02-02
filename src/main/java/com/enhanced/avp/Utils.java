package com.enhanced.avp;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.alien.common.block.init.AlienBlocks;

/**
 * Утилиты для продвинутых действий мобов.
 */
public class Utils {

    /**
     * Логика лазания по стенам.
     */
    public static void doClimbing(EntityLiving entity) {
        if (entity.field_70123_F) { // collidedHorizontally
            entity.field_70181_x = 0.22; // motionY
        }
    }

    /**
     * Логика разрушения блоков перед мобом.
     */
    public static void doBreakBlocks(EntityLiving entity) {
        if (entity.field_70170_p.field_72995_K) return;
        
        // Только если есть цель и моб движется
        if (entity.func_70638_az() != null) {
            World world = entity.field_70170_p;
            // Проверяем блоки перед мобом
            for (int i = 0; i < 2; i++) {
                BlockPos pos = new BlockPos(entity.field_70165_t + Math.cos(Math.toRadians(entity.field_70177_z + 90)) * 1.5,
                                            entity.field_70163_u + i,
                                            entity.field_70161_v + Math.sin(Math.toRadians(entity.field_70177_z + 90)) * 1.5);
                
                IBlockState state = world.func_180495_p(pos);
                Block block = state.func_177230_c();
                
                if (block != Blocks.field_150350_a && block != Blocks.field_150357_h && block != AlienBlocks.NATURAL_RESIN) {
                    if (state.func_185887_b(world, pos) <= 40.0f) {
                        world.func_175655_b(pos, true); // Разрушить с дропом
                    }
                }
            }
        }
    }

    /**
     * Логика дальних прыжков (а-ля Parasites/Rupter).
     */
    public static void doLeap(EntityLiving entity, double jumpPower) {
        if (entity.field_70122_E && entity.func_70638_az() != null) {
            double dist = entity.func_70032_d(entity.func_70638_az());
            if (dist > 4.0 && dist < 12.0 && entity.field_70146_Z.nextInt(20) == 0) {
                // Делаем рывок к цели
                double d0 = entity.func_70638_az().field_70165_t - entity.field_70165_t;
                double d1 = entity.func_70638_az().field_70161_v - entity.field_70161_v;
                float f = MathHelper.func_76133_a(d0 * d0 + d1 * d1);
                
                entity.field_70159_w = d0 / f * 0.8 + entity.field_70159_w * 0.2; // motionX
                entity.field_70179_y = d1 / f * 0.8 + entity.field_70179_y * 0.2; // motionZ
                entity.field_70181_x = jumpPower; // motionY
            }
        }
    }
}
