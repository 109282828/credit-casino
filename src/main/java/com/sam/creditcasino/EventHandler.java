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
import net.minecraft.world.gen.Heightmap;
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
    public static final Set<UUID> registered = new HashSet<>();
    public static final Map<UUID, Integer> debts = new HashMap<>();
    public static final List<BlockPos> protectedBlocks = new ArrayList<>();
    private static int tickCounter = 0;

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerEntity player = event.getPlayer();
        if (player.level.isClientSide()) return;

        player.inventory.add(new ItemStack(Items.OAK_SIGN, 1));
        player.inventory.add(new ItemStack(ModItems.UPGRADER_ITEM.get(), 1));

        if (!hasSpawned.contains(player.getUUID())) {
            hasSpawned.add(player.getUUID());
            spawnBuildings(player);
        }
    }

    public static void spawnBuildings(PlayerEntity player) {
        BlockPos basePos = player.level.getHeightmapPos(Heightmap.Type.WORLD_SURFACE, player.blockPosition().offset(10, 0, 0));
        
        for (int x = -20; x <= 20; x++) {
            for (int z = -20; z <= 20; z++) {
                for (int y = 0; y <= 15; y++) {
                    BlockPos p = basePos.offset(x, y, z);
                    player.level.setBlockAndUpdate(p, Blocks.AIR.defaultBlockState());
                    protectedBlocks.add(p);
                }
                BlockPos floor = basePos.offset(x, -1, z);
                player.level.setBlockAndUpdate(floor, Blocks.GRASS_BLOCK.defaultBlockState());
                protectedBlocks.add(floor);
            }
        }

        // БАНК
        for (int x = -4; x <= 4; x++) {
            for (int z = -4; z <= 4; z++) {
                setProtBlock(player, basePos.offset(x, 0, z), Blocks.GOLD_BLOCK.defaultBlockState());
                setProtBlock(player, basePos.offset(x, 1, z), Blocks.SMOOTH_QUARTZ.defaultBlockState());
            }
        }
        for (int y = 2; y <= 6; y++) {
            for (int x = -4; x <= 4; x++) {
                for (int z = -4; z <= 4; z++) {
                    if (Math.abs(x) == 4 || Math.abs(z) == 4) {
                        if (y == 6) {
                            setProtBlock(player, basePos.offset(x, y, z), Blocks.DARK_PRISMARINE.defaultBlockState());
                        } else if (y % 2 == 1 && (Math.abs(x) == 4 && Math.abs(z) < 3 || Math.abs(z) == 4 && Math.abs(x) < 3)) {
                            setProtBlock(player, basePos.offset(x, y, z), Blocks.LIGHT_BLUE_STAINED_GLASS.defaultBlockState());
                        } else {
                            setProtBlock(player, basePos.offset(x, y, z), Blocks.SMOOTH_QUARTZ.defaultBlockState());
                        }
                    }
                }
            }
        }
        for (int x = -4; x <= 4; x++) {
            for (int z = -4; z <= 4; z++) {
                setProtBlock(player, basePos.offset(x, 7, z), Blocks.GOLD_BLOCK.defaultBlockState());
                if (Math.abs(x) < 4 && Math.abs(z) < 4) setProtBlock(player, basePos.offset(x, 8, z), Blocks.DIAMOND_BLOCK.defaultBlockState());
            }
        }
        setProtBlock(player, basePos.offset(0, 2, -4), Blocks.AIR.defaultBlockState());
        setProtBlock(player, basePos.offset(0, 3, -4), Blocks.AIR.defaultBlockState());
        setProtBlock(player, basePos.offset(-2, 6, 0), Blocks.SEA_LANTERN.defaultBlockState());
        setProtBlock(player, basePos.offset(2, 6, 0), Blocks.SEA_LANTERN.defaultBlockState());

        VillagerEntity villager = EntityType.VILLAGER.create(player.level);
        if (villager != null) {
            villager.setPos(basePos.getX() + 0.5, basePos.getY() + 2, basePos.getZ() + 0.5);
            villager.setCustomName(new StringTextComponent("§6Роман Кредитор"));
            villager.setCustomNameVisible(true);
            villager.setInvulnerable(true);
            villager.setNoAi(true);
            player.level.addFreshEntity(villager);
        }

        // КАЗИНО
        BlockPos casinoPos = basePos.offset(15, 0, 0);
        for (int x = -5; x <= 5; x++) {
            for (int z = -5; z <= 5; z++) {
                setProtBlock(player, casinoPos.offset(x, 0, z), Blocks.DARK_OAK_PLANKS.defaultBlockState());
                setProtBlock(player, casinoPos.offset(x, 1, z), Blocks.RED_CARPET.defaultBlockState());
            }
        }
        for (int y = 1; y <= 5; y++) {
            for (int x = -5; x <= 5; x++) {
                for (int z = -5; z <= 5; z++) {
                    if (Math.abs(x) == 5 || Math.abs(z) == 5) {
                        if (y == 5) {
                            setProtBlock(player, casinoPos.offset(x, y, z), Blocks.DARK_OAK_STAIRS.defaultBlockState());
                        } else if ((Math.abs(x) == 5 && Math.abs(z) < 3) || (Math.abs(z) == 5 && Math.abs(x) < 3)) {
                            setProtBlock(player, casinoPos.offset(x, y, z), Blocks.RED_STAINED_GLASS.defaultBlockState());
                        } else {
                            setProtBlock(player, casinoPos.offset(x, y, z), Blocks.STRIPPED_DARK_OAK_LOG.defaultBlockState());
                        }
                    }
                }
            }
        }
        for (int x = -5; x <= 5; x++) {
            for (int z = -5; z <= 5; z++) {
                setProtBlock(player, casinoPos.offset(x, 6, z), Blocks.RED_CONCRETE.defaultBlockState());
            }
        }
        setProtBlock(player, casinoPos.offset(0, 1, -5), Blocks.AIR.defaultBlockState());
        setProtBlock(player, casinoPos.offset(0, 2, -5), Blocks.AIR.defaultBlockState());
        setProtBlock(player, casinoPos.offset(0, 3, -5), Blocks.AIR.defaultBlockState());
        
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

    private static void setProtBlock(PlayerEntity player, BlockPos pos, net.minecraft.block.BlockState state) {
        player.level.setBlockAndUpdate(pos, state);
        protectedBlocks.add(pos);
    }

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
                    NetworkHooks.openGui((ServerPlayerEntity) event.getPlayer(),
                            new SimpleNamedContainerProvider((id, inv, p) -> new CreditContainer(id, inv, null), new StringTextComponent("Кредит")));
                }
            }
        }
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        BlockPos pos = event.getPos();
        PlayerEntity player = event.getPlayer();
        if (player == null) return;
        if (protectedBlocks.contains(pos)) {
            event.setCanceled(true);
            player.sendMessage(new StringTextComponent("§cЭто здание защищено!"), player.getUUID());
        }
    }

    @SubscribeEvent
    public void onExplosion(ExplosionEvent.Detonate event) {
        event.getAffectedBlocks().removeIf(pos -> protectedBlocks.contains(pos));
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
                                        BlockPos breakPos = pPos.offset(x, y, z);
                                        if (!protectedBlocks.contains(breakPos)) {
                                            player.level.destroyBlock(breakPos, false);
                                        }
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
}
