package com.sam.creditcasino;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.server.ServerLifecycleHooks; // <--- ИСПРАВЛЕНО ТУТ
import java.util.*;

public class EventHandler {
    private static final Set<UUID> hasSpawned = new HashSet<>();
    private static final Set<UUID> registered = new HashSet<>();
    public static final Map<UUID, Integer> debts = new HashMap<>();
    private static int tickCounter = 0;

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerEntity player = event.getPlayer();
        if (player.level.isClientSide() || hasSpawned.contains(player.getUUID())) return;

        hasSpawned.add(player.getUUID());
        
        // Выдаем табличку для регистрации
        player.inventory.add(new ItemStack(Items.OAK_SIGN, 1));

        BlockPos pos = player.blockPosition().offset(10, 0, 10);
        
        // === ЗДАНИЕ КРЕДИТ ===
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                player.level.setBlockAndUpdate(pos.offset(x, -1, z), Blocks.QUARTZ_BLOCK.defaultBlockState());
                player.level.setBlockAndUpdate(pos.offset(x, 0, z), Blocks.POLISHED_DIORITE.defaultBlockState());
            }
        }
        for (int y = 1; y <= 4; y++) {
            for (int x = -3; x <= 3; x++) {
                for (int z = -3; z <= 3; z++) {
                    if (x == -3 || x == 3 || z == -3 || z == 3) {
                        if (y == 4) {
                            player.level.setBlockAndUpdate(pos.offset(x, y, z), Blocks.SMOOTH_QUARTZ_SLAB.defaultBlockState());
                        } else {
                            if ((x == -3 || x == 3) && (z == -1 || z == 0 || z == 1)) {
                                player.level.setBlockAndUpdate(pos.offset(x, y, z), Blocks.GLASS_PANE.defaultBlockState());
                            } else if ((z == -3 || z == 3) && (x == -1 || x == 0 || x == 1)) {
                                player.level.setBlockAndUpdate(pos.offset(x, y, z), Blocks.GLASS_PANE.defaultBlockState());
                            } else {
                                player.level.setBlockAndUpdate(pos.offset(x, y, z), Blocks.SMOOTH_QUARTZ.defaultBlockState());
                            }
                        }
                    }
                }
            }
        }
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                player.level.setBlockAndUpdate(pos.offset(x, 5, z), Blocks.DARK_PRISMARINE.defaultBlockState());
            }
        }
        player.level.setBlockAndUpdate(pos.offset(0, 1, -3), Blocks.AIR.defaultBlockState());
        player.level.setBlockAndUpdate(pos.offset(0, 2, -3), Blocks.AIR.defaultBlockState());
        player.level.setBlockAndUpdate(pos.offset(-2, 3, -2), Blocks.SEA_LANTERN.defaultBlockState());
        player.level.setBlockAndUpdate(pos.offset(2, 3, 2), Blocks.SEA_LANTERN.defaultBlockState());

        VillagerEntity villager = EntityType.VILLAGER.create(player.level);
        if (villager != null) {
            villager.setPos(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
            villager.setCustomName(new StringTextComponent("§6Роман Кредитор"));
            villager.setCustomNameVisible(true);
            villager.setInvulnerable(true);
            villager.setNoAi(true);
            player.level.addFreshEntity(villager);
        }

        // === ЗДАНИЕ КАЗИНО ===
        BlockPos casinoPos = pos.offset(15, 0, 0);
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                player.level.setBlockAndUpdate(casinoPos.offset(x, -1, z), Blocks.DARK_OAK_PLANKS.defaultBlockState());
                player.level.setBlockAndUpdate(casinoPos.offset(x, 0, z), Blocks.RED_CARPET.defaultBlockState());
            }
        }
        for (int y = 1; y <= 4; y++) {
            for (int x = -3; x <= 3; x++) {
                for (int z = -3; z <= 3; z++) {
                    if (x == -3 || x == 3 || z == -3 || z == 3) {
                        if (y == 4) {
                            player.level.setBlockAndUpdate(casinoPos.offset(x, y, z), Blocks.OAK_SLAB.defaultBlockState());
                        } else {
                            if ((x == -3 || x == 3) && (z == -1 || z == 0 || z == 1)) {
                                player.level.setBlockAndUpdate(casinoPos.offset(x, y, z), Blocks.RED_STAINED_GLASS_PANE.defaultBlockState());
                            } else if ((z == -3 || z == 3) && (x == -1 || x == 0 || x == 1)) {
                                player.level.setBlockAndUpdate(casinoPos.offset(x, y, z), Blocks.RED_STAINED_GLASS_PANE.defaultBlockState());
                            } else {
                                player.level.setBlockAndUpdate(casinoPos.offset(x, y, z), Blocks.OAK_LOG.defaultBlockState());
                            }
                        }
                    }
                }
            }
        }
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                player.level.setBlockAndUpdate(casinoPos.offset(x, 5, z), Blocks.DARK_OAK_STAIRS.defaultBlockState());
            }
        }
        player.level.setBlockAndUpdate(casinoPos.offset(0, 1, -3), Blocks.AIR.defaultBlockState());
        player.level.setBlockAndUpdate(casinoPos.offset(0, 2, -3), Blocks.AIR.defaultBlockState());
        
        VillagerEntity dealer = EntityType.VILLAGER.create(player.level);
        if (dealer != null) {
            dealer.setPos(casinoPos.getX() + 0.5, casinoPos.getY() + 1, casinoPos.getZ() + 0.5);
            dealer.setCustomName(new StringTextComponent("§cДилер"));
            dealer.setCustomNameVisible(true);
            dealer.setInvulnerable(true);
            dealer.setNoAi(true);
            player.level.addFreshEntity(dealer);
        }
    }

    // --- РЕГИСТРАЦИЯ ЧЕРЕЗ ТАБЛИЧКУ ---
    @SubscribeEvent
    public void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof PlayerEntity)) return;
        PlayerEntity player = (PlayerEntity) event.getEntity();
        
        if (event.getPlacedBlock().getBlock() == Blocks.OAK_SIGN) {
            BlockPos pos = event.getPos();
            boolean hasBlocksAround = false;
            for (int x = -3; x <= 3; x++) {
                for (int y = -3; y <= 3; y++) {
                    for (int z = -3; z <= 3; z++) {
                        if (x == 0 && y == 0 && z == 0) continue;
                        if (!player.level.isEmptyBlock(pos.offset(x, y, z)) && player.level.getBlockState(pos.offset(x, y, z)).getBlock() != Blocks.GRASS && player.level.getBlockState(pos.offset(x, y, z)).getBlock() != Blocks.DIRT) {
                            hasBlocksAround = true;
                            break;
                        }
                    }
                }
            }
            
            if (!hasBlocksAround) {
                event.setCanceled(true);
                player.inventory.add(new ItemStack(Items.OAK_SIGN, 1));
                player.sendMessage(new StringTextComponent("§cВы не можете поставить табличку в пустом месте!"), player.getUUID());
            } else {
                registered.add(player.getUUID());
                player.sendMessage(new StringTextComponent("§aВы успешно зарегистрированы!"), player.getUUID());
                net.minecraft.tileentity.SignTileEntity sign = (net.minecraft.tileentity.SignTileEntity) player.level.getBlockEntity(pos);
                if (sign != null) {
                    sign.setMessage(0, new StringTextComponent("§aРегистрация"));
                    sign.setMessage(1, new StringTextComponent(player.getName().getString()));
                    sign.setChanged();
                }
            }
        }
    }

    @SubscribeEvent
    public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getTarget() instanceof VillagerEntity && event.getTarget().hasCustomName()) {
            String name = event.getTarget().getCustomName().getString();
            if (name.contains("Дилер")) {
                event.setCanceled(true);
                if (!event.getWorld().isClientSide()) {
                    NetworkHooks.openGui((ServerPlayerEntity) event.getPlayer(),
                            new SimpleNamedContainerProvider((id, inv, p) -> new CasinoContainer(id, inv, null), new StringTextComponent("Казино")));
                }
            }
            if (name.contains("Роман Кредитор")) {
                event.setCanceled(true);
                if (!event.getWorld().isClientSide()) {
                    event.getPlayer().sendMessage(new StringTextComponent("§eКредитный отдел временно закрыт."), event.getPlayer().getUUID());
                }
            }
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        tickCounter++;
        
        if (tickCounter % 24000 == 0) {
            if (ServerLifecycleHooks.getCurrentServer() == null) return;
            
            for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                if (registered.contains(player.getUUID()) && debts.getOrDefault(player.getUUID(), 0) > 0) {
                    ZombieEntity debtorKiller = EntityType.ZOMBIE.create(player.level);
                    if (debtorKiller != null) {
                        debtorKiller.setPos(player.getX(), player.getY(), player.getZ());
                        debtorKiller.setCustomName(new StringTextComponent("§0Человек в черном"));
                        debtorKiller.setCustomNameVisible(true);
                        debtorKiller.setItemSlot(EquipmentSlotType.HEAD, new ItemStack(Items.LEATHER_HELMET));
                        debtorKiller.setItemSlot(EquipmentSlotType.CHEST, new ItemStack(Items.LEATHER_CHESTPLATE));
                        debtorKiller.setItemSlot(EquipmentSlotType.LEGS, new ItemStack(Items.LEATHER_LEGGINGS));
                        debtorKiller.setItemSlot(EquipmentSlotType.FEET, new ItemStack(Items.LEATHER_BOOTS));
                        debtorKiller.getAttribute(Attributes.MAX_HEALTH).setBaseValue(100.0D);
                        debtorKiller.setHealth(100.0F);
                        debtorKiller.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.3D);
                        debtorKiller.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(10.0D);
                        player.level.addFreshEntity(debtorKiller);
                        
                        player.sendMessage(new StringTextComponent("§cОТДАВАЙ ДОЛГ!"), player.getUUID());
                        
                        BlockPos pPos = player.blockPosition();
                        for (int x = -5; x <= 5; x++) {
                            for (int y = 0; y <= 5; y++) {
                                for (int z = -5; z <= 5; z++) {
                                    if (Math.random() < 0.3) {
                                        player.level.destroyBlock(pPos.offset(x, y, z), false);
                                    }
                                }
                            }
                        }
                        debts.put(player.getUUID(), debts.get(player.getUUID()) * 2);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        BlockPos pos = event.getPos();
        PlayerEntity player = event.getPlayer();
        if (player != null && !player.isCreative() && pos.distSqr(player.blockPosition()) < 900) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onExplosion(ExplosionEvent.Detonate event) {
        event.getAffectedBlocks().removeIf(pos -> pos.getY() < 6 && Math.abs(pos.getX()) < 30 && Math.abs(pos.getZ()) < 30);
    }
}
