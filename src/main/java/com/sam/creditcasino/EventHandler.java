package com.sam.creditcasino;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.NetworkHooks;
import java.util.*;

public class EventHandler {
    private static final Set<UUID> hasSpawned = new HashSet<>();
    public static final Map<UUID, Integer> debts = new HashMap<>();
    private static int tickCounter = 0;

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerEntity player = event.getPlayer();
        if (player.level.isClientSide() || hasSpawned.contains(player.getUUID())) return;

        hasSpawned.add(player.getUUID());
        
        // Выдаем книгу апгрейда
        player.inventory.add(new ItemStack(ModItems.UPGRADE_BOOK.get()));

        BlockPos pos = player.blockPosition().offset(5, 0, 0);
        
        // Здание Кредит
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                for (int y = 0; y <= 4; y++) {
                    if (x == -2 || x == 2 || z == -2 || z == 2 || y == 4) {
                        player.level.setBlockAndUpdate(pos.offset(x, y, z), Blocks.IRON_BLOCK.defaultBlockState());
                    } else if (y == 0) {
                        player.level.setBlockAndUpdate(pos.offset(x, y, z), Blocks.SMOOTH_QUARTZ.defaultBlockState());
                    }
                }
            }
        }
        // Роман
        VillagerEntity villager = EntityType.VILLAGER.create(player.level);
        if (villager != null) {
            villager.setPos(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
            villager.setCustomName(new StringTextComponent("Роман Кредитор"));
            villager.setCustomNameVisible(true);
            villager.setInvulnerable(true);
            player.level.addFreshEntity(villager);
        }

        // Здание Казино
        BlockPos casinoPos = pos.offset(10, 0, 0);
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                for (int y = 0; y <= 3; y++) {
                    if (x == -2 || x == 2 || z == -2 || z == 2 || y == 3) {
                        player.level.setBlockAndUpdate(casinoPos.offset(x, y, z), Blocks.OAK_PLANKS.defaultBlockState());
                    } else if (y == 0) {
                        player.level.setBlockAndUpdate(casinoPos.offset(x, y, z), Blocks.DARK_OAK_PLANKS.defaultBlockState());
                    }
                }
            }
        }
        // Дилер
        VillagerEntity dealer = EntityType.VILLAGER.create(player.level);
        if (dealer != null) {
            dealer.setPos(casinoPos.getX() + 0.5, casinoPos.getY() + 1, casinoPos.getZ() + 0.5);
            dealer.setCustomName(new StringTextComponent("Дилер"));
            dealer.setCustomNameVisible(true);
            dealer.setInvulnerable(true);
            player.level.addFreshEntity(dealer);
        }
    }

    @SubscribeEvent
    public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getTarget() instanceof VillagerEntity && event.getTarget().hasCustomName()) {
            String name = event.getTarget().getCustomName().getString();
            if (name.equals("Дилер")) {
                event.setCanceled(true);
                if (!event.getWorld().isClientSide()) {
                    NetworkHooks.openGui((net.minecraft.server.network.ServerPlayerEntity) event.getPlayer(),
                            new SimpleNamedContainerProvider((id, inv, p) -> new CasinoContainer(id, inv, null), new StringTextComponent("Казино")));
                }
            }
        }
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        BlockPos pos = event.getPos();
        if (pos.getX() > 0 && pos.getX() < 20 && pos.getZ() > -5 && pos.getZ() < 5) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onExplosion(ExplosionEvent.Detonate event) {
        event.getAffectedBlocks().removeIf(pos -> pos.getX() > 0 && pos.getX() < 20 && pos.getZ() > -5 && pos.getZ() < 5);
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        tickCounter++;
        if (tickCounter % 12000 == 0) {
            for (Map.Entry<UUID, Integer> entry : debts.entrySet()) {
                if (entry.getValue() > 0) entry.setValue(entry.getValue() * 2);
            }
        }
    }
}
