
package com.norm.mineamob;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = MineAMob.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public final class HorrorSpawnHandler {

    private static final String NEXT_SPAWN_KEY =
            "mineamob_next_horror_spawn";

    private static final ResourceLocation HORROR_ID =
            new ResourceLocation("man", "managgresive");

    private static final long MIN_DELAY = 20L * 60L * 10L;
    private static final long MAX_DELAY = 20L * 60L * 15L;

    private static final double MIN_DISTANCE = 24.0D;
    private static final double MAX_DISTANCE = 80.0D;

    private HorrorSpawnHandler() {
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Player player = event.player;

        if (player.level().isClientSide
                || !(player.level() instanceof ServerLevel level)) {
            return;
        }

        if (player.isSpectator() || !player.isAlive()) {
            return;
        }

        long now = level.getGameTime();
        long nextSpawn = player.getPersistentData()
                .getLong(NEXT_SPAWN_KEY);

        if (nextSpawn == 0L) {
            scheduleNextSpawn(player, now);
            return;
        }

        if (now < nextSpawn) {
            return;
        }

        // Schedule the next attempt before trying to spawn.
        // This prevents repeated attempts every tick.
        scheduleNextSpawn(player, now);

        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE
                .getOptional(HORROR_ID)
                .orElse(null);

        if (type == null) {
            return;
        }

        for (int attempt = 0; attempt < 12; attempt++) {
            double angle = level.getRandom().nextDouble()
                    * Math.PI * 2.0D;

            double distance = MIN_DISTANCE
                    + level.getRandom().nextDouble()
                    * (MAX_DISTANCE - MIN_DISTANCE);

            int x = (int) Math.floor(
                    player.getX() + Math.cos(angle) * distance);

            int z = (int) Math.floor(
                    player.getZ() + Math.sin(angle) * distance);

            // Don't force-load chunks just to spawn the horror mob.
            if (!level.hasChunkAt(new BlockPos(x, 0, z))) {
                continue;
            }

            BlockPos ground = level.getHeight(
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    x, z);

            if (ground.getY() >= level.getMaxBuildHeight()) {
                continue;
            }

            if (!level.getBlockState(ground).getFluidState().isEmpty()) {
                continue;
            }

            Entity entity = type.create(level);

            if (entity == null) {
                return;
            }

            entity.moveTo(
                    x + 0.5D,
                    ground.getY(),
                    z + 0.5D,
                    level.getRandom().nextFloat() * 360.0F,
                    0.0F);

            if (!level.noCollision(entity)) {
                entity.discard();
                continue;
            }

            if (level.addFreshEntity(entity)) {
                return;
            }

            entity.discard();
        }
    }

    private static void scheduleNextSpawn(
            Player player, long now) {

        long delay = MIN_DELAY
                + player.getRandom().nextLong(
                        MAX_DELAY - MIN_DELAY + 1L);

        player.getPersistentData().putLong(
                NEXT_SPAWN_KEY, now + delay);
    }
}
