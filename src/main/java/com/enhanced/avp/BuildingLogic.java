package com.enhanced.avp;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.alien.common.block.init.AlienBlocks;
import org.alien.common.entity.living.SpeciesXenomorph;
import org.alien.common.entity.living.xenomorph.EntityMatriarch;
import org.alien.common.entity.living.xenomorph.ovamorph.EntityOvamorph;

import java.util.List;

/**
 * Логика строительства улья и защиты королевы.
 */
public class BuildingLogic {

    /**
     * Основной цикл логики для королевы.
     */
    public static void updateQueen(EntityMatriarch queen) {
        World world = queen.field_70170_p;
        if (world.field_72995_K) return;

        NBTTagCompound data = queen.getEntityData();
        int timer = data.func_74762_e("EnhancedHiveTimer");
        boolean constructed = data.func_74767_n("EnhancedHiveDone");

        // 1. Процесс строительства (первые X минут)
        if (!constructed) {
            timer++;
            if (timer % 20 == 0) { // Каждую секунду
                buildStep(queen);
            }
            
            // Если время вышло (7 минут по умолчанию)
            if (timer >= ConfigHandler.hiveConstructionMinutes * 1200) {
                spawnInitialEggs(queen);
                data.func_74757_a("EnhancedHiveDone", true);
                timer = 0;
            }
            data.func_74768_a("EnhancedHiveTimer", timer);
            
            // Защита: пока строится, все вокруг охраняют
            coordinateGuards(queen);
        } else {
            // 2. Постоянный спавн яиц
            timer++;
            if (timer >= ConfigHandler.eggSpawnIntervalSeconds * 20) {
                spawnSingleEgg(queen);
                timer = 0;
            }
            data.func_74768_a("EnhancedHiveTimer", timer);
        }
    }

    private static void buildStep(EntityMatriarch queen) {
        World world = queen.field_70170_p;
        BlockPos center = queen.func_180425_c();
        int r = 6;
        
        for (int i = 0; i < 3; i++) { // 3 блока за шаг
            BlockPos target = center.func_177982_a(queen.field_70146_Z.nextInt(r*2)-r, 
                                                queen.field_70146_Z.nextInt(3)-1, 
                                                queen.field_70146_Z.nextInt(r*2)-r);
            
            IBlockState state = world.func_180495_p(target);
            if (state.func_177230_c() != Blocks.field_150357_h && state.func_177230_c() != AlienBlocks.NATURAL_RESIN) {
                world.func_175656_a(target, AlienBlocks.NATURAL_RESIN.func_176223_P());
            }
        }
    }

    private static void coordinateGuards(EntityMatriarch queen) {
        List<Entity> xenos = queen.field_70170_p.func_72839_b(queen, queen.func_174813_aQ().func_72314_b(32, 10, 32));
        for (Entity e : xenos) {
            if (e instanceof SpeciesXenomorph && !(e instanceof EntityMatriarch)) {
                // Заставляем патрулировать вокруг королевы
                SpeciesXenomorph xeno = (SpeciesXenomorph)e;
                if (xeno.func_70638_az() == null) { // Если нет цели
                     xeno.func_70661_as().func_75497_a(queen.field_70165_t, queen.field_70163_u, queen.field_70161_v, 1.0);
                }
            }
        }
    }

    private static void spawnInitialEggs(EntityMatriarch queen) {
        for (int i = 0; i < ConfigHandler.initialEggCount; i++) {
            spawnSingleEgg(queen);
        }
    }

    private static void spawnSingleEgg(EntityMatriarch queen) {
        World world = queen.field_70170_p;
        EntityOvamorph egg = new EntityOvamorph(world);
        double rx = queen.field_70165_t + queen.field_70146_Z.nextDouble() * 6 - 3;
        double rz = queen.field_70161_v + queen.field_70146_Z.nextDouble() * 6 - 3;
        egg.func_70012_b(rx, queen.field_70163_u, rz, 0, 0);
        world.func_72838_d(egg);
    }
}
