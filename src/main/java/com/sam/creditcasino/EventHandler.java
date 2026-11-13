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
import net.minecraftforge.fml.server.ServerLifecycleHooks;
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
        player.inventory.add(new ItemStack(Items.OAK_SIGN, 1));

        // Выбираем ровную точку рядом с игроком
        BlockPos pos = player.blockPosition().offset(12, 0, 0);
        
        // 1. ОЧИЩАЕМ ТЕРРИТОРИЮ (40x15x40)
        for (int x = -20; x <= 20; x++) {
            for (int z = -20; z <= 20; z++) {
                for (int y = -2; y <= 15; y++) {
                    player.level.setBlockAndUpdate(pos.offset(x, y, z), Blocks.AIR.defaultBlockState());
                }
                // Ровняем пол
                player.level.setBlockAndUpdate(pos.offset(x, -3, z), Blocks.GRASS_BLOCK.defaultBlockState());
            }
        }

        // 2. СТРОИМ БОГАТЫЙ КРЕДИТ (Золото, кварц, витражи)
        BlockPos creditPos = pos.offset(0, 0, 0);
        
        // Фундамент
        for (int x = -4; x <= 4; x++) {
            for (int z = -4; z <= 4; z++) {
                player.level.setBlockAndUpdate(creditPos.offset(x, 0, z), Blocks.GOLD_BLOCK.defaultBlockState());
                player.level.setBlockAndUpdate(creditPos.offset(x, 1, z), Blocks.SMOOTH_QUARTZ.defaultBlockState());
            }
        }
        // Стены и окна
        for (int y = 2; y <= 6; y++) {
            for (int x = -4; x <= 4; x++) {
                for (int z = -4; z <= 4; z++) {
                    if (Math.abs(x) == 4 || Math.abs(z) == 4) {
                        if (y == 6) {
                            player.level.setBlockAndUpdate(creditPos.offset(x, y, z), Blocks.DARK_PRISMARINE.defaultBlockState());
                        } else if (y % 2 == 1 && (Math.abs(x) == 4 && Math.abs(z) < 3 || Math.abs(z) == 4 && Math.abs(x) < 3)) {
                            player.level.setBlockAndUpdate(creditPos.offset(x, y, z), Blocks.LIGHT_BLUE_STAINED_GLASS.defaultBlockState());
                        } else {
                            player.level.setBlockAndUpdate(creditPos.offset(x, y, z), Blocks.SMOOTH_QUARTZ.defaultBlockState());
                        }
                    }
                }
            }
        }
        // Колонны
        for (int y = 2; y <= 6; y++) {
            player.level.setBlockAndUpdate(creditPos.offset(-4, y, -4), Blocks.GOLD_BLOCK.defaultBlockState());
            player.level.setBlockAndUpdate(creditPos.offset(4, y, -4), Blocks.GOLD_BLOCK.defaultBlockState());
            player.level.setBlockAndUpdate(creditPos.offset(-4, y, 4), Blocks.GOLD_BLOCK.defaultBlockState());
            player.level.setBlockAndUpdate(creditPos.offset(4, y, 4), Blocks.GOLD_BLOCK.defaultBlockState());
        }
        // Крыша-купол
        for (int x = -4; x <= 4; x++) {
            for (int z = -4; z <= 4; z++) {
                player.level.setBlockAndUpdate(creditPos.offset(x, 7, z), Blocks.GOLD_BLOCK.defaultBlockState());
                if (Math.abs(x) < 4 && Math.abs(z) < 4) {
                    player.level.setBlockAndUpdate(creditPos.offset(x, 8, z), Blocks.DIAMOND_BLOCK.defaultBlockState());
                }
            }
        }
        // Вход и свет
        player.level.setBlockAndUpdate(creditPos.offset(0, 2, -4), Blocks.AIR.defaultBlockState());
        player.level.setBlockAndUpdate(creditPos.offset(0, 3, -4), Blocks.AIR.defaultBlockState());
        player.level.setBlockAndUpdate(creditPos.offset(-2, 6, 0), Blocks.SEA_LANTERN.defaultBlockState());
        player.level.setBlockAndUpdate(creditPos.offset(2, 6, 0), Blocks.SEA_LANTERN.defaultBlockState());
        
        VillagerEntity villager = EntityType.VILLAGER.create(player.level);
        if (villager != null) {
            villager.setPos(creditPos.getX() + 0.5, creditPos.getY() + 2, creditPos.getZ() + 0.5);
            villager.setCustomName(new StringTextComponent("§6Роман Кредитор"));
            villager.setCustomNameVisible(true);
            villager.setInvulnerable(true);
            villager.setNoAi(true);
            player.level.addFreshEntity(villager);
        }

        // 3. СТРОИМ БОГАТОЕ КАЗИНО (Дерево, красный ковер, неон)
        BlockPos casinoPos = pos.offset(15, 0, 0);
        
        for (int x = -5; x <= 5; x++) {
            for (int z = -5; z <= 5; z++) {
                player.level.setBlockAndUpdate(casinoPos.offset(x, 0, z), Blocks.DARK_OAK_PLANKS.defaultBlockState());
                player.level.setBlockAndUpdate(casinoPos.offset(x, 1, z), Blocks.RED_CARPET.defaultBlockState());
            }
        }
        for (int y = 1; y <= 5; y++) {
            for (int x = -5; x <= 5; x++) {
                for (int z = -5; z <= 5; z++) {
                    if (Math.abs(x) == 5 || Math.abs(z) == 5) {
                        if (y == 5) {
                            player.level.setBlockAndUpdate(casinoPos.offset(x, y, z), Blocks.DARK_OAK_STAIRS.defaultBlockState());
                        } else if ((Math.abs(x) == 5 && Math.abs(z) < 3) || (Math.abs(z) == 5 && Math.abs(x) < 3)) {
                            player.level.setBlockAndUpdate(casinoPos.offset(x, y, z), Blocks.RED_STAINED_GLASS.defaultBlockState());
                        } else {
                            player.level.setBlockAndUpdate(casinoPos.offset(x, y, z), Blocks.STRIPPED_DARK_OAK_LOG.defaultBlockState());
                        }
                    }
                }
            }
        }
        // Крыша
        for (int x = -5; x <= 5; x++) {
            for (int z = -5; z <= 5; z++) {
                player.level.setBlockAndUpdate(casinoPos.offset(x, 6, z), Blocks.RED_CONCRETE.defaultBlockState());
            }
        }
        // Вход
        player.level.setBlockAndUpdate(casinoPos.offset(0, 1, -5), Blocks.AIR.defaultBlockState());
        player.level.setBlockAndUpdate(casinoPos.offset(0, 2, -5), Blocks.AIR.defaultBlockState());
        player.level.setBlockAndUpdate(casinoPos.offset(0, 3, -5), Blocks.AIR.defaultBlockState());
        
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
                        if (!player.level.isEmptyBlock(pos.offset(x, y, z)) && player.level.getBlockState(pos.offset(x, y, z)).getBlock() != Blocks.GRASS_BLOCK && player.level.getBlockState(pos.offset(x, y, z)).getBlock() != Blocks.DIRT) {
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
        if (player != null && !player.isCreative() && pos.distSqr(player.blockPosition()) < 1600) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onExplosion(ExplosionEvent.Detonate event) {
        event.getAffectedBlocks().removeIf(pos -> pos.getY() < 10 && Math.abs(pos.getX()) < 50 && Math.abs(pos.getZ()) < 50);
    }
}
