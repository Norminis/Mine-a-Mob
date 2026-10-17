
package com.norm.mineamob;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod(MineAMob.MOD_ID)
public final class MineAMob {
    public static final String MOD_ID = "mineamob";
    private static final List<EntityType<?>> VANILLA_MOBS = new ArrayList<>();

    public MineAMob() {
        for (EntityType<?> type : BuiltInRegistries.ENTITY_TYPE) {
            ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(type);
            if (id != null
                    && "minecraft".equals(id.getNamespace())
                    && type.getCategory() != MobCategory.MISC) {
                VANILLA_MOBS.add(type);
            }
        }

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onBlockBroken(BlockEvent.BreakEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        // Only spawn a mob 20% of the time.
        if (level.getRandom().nextFloat() >= 0.20F) {
            return;
        }

        if (VANILLA_MOBS.isEmpty()) {
            return;
        }

        for (int attempt = 0; attempt < 8; attempt++) {
            EntityType<?> type = VANILLA_MOBS.get(
                    level.getRandom().nextInt(VANILLA_MOBS.size()));
            Entity entity = type.create(level);

            if (!(entity instanceof Mob mob)) {
                continue;
            }

            BlockPos pos = event.getPos();
            double x = pos.getX() + 0.5D
                    + (level.getRandom().nextDouble() - 0.5D) * 2.0D;
            double y = pos.getY() + 1.0D;
            double z = pos.getZ() + 0.5D
                    + (level.getRandom().nextDouble() - 0.5D) * 2.0D;

            mob.moveTo(x, y, z,
                    level.getRandom().nextFloat() * 360.0F, 0.0F);

            if (level.addFreshEntity(mob)) {
                return;
            }
        }
    }
}
