package com.sam.creditcasino;

import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.merchant.villager.VillagerEntity;
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
import net.minecraftforge.event.entity.player.AttackEntityEvent;
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
    
    public static final Map<UUID, Integer> prisonTimeLeft = new HashMap<>();
    public static final Map<UUID, BlockPos> returnPositions = new HashMap<>();
    private static final BlockPos PRISON_CENTER = new BlockPos(0, 100, 0);
    
    private static int tickCounter = 0;

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerEntity player = event.getPlayer();
        if (player.level.isClientSide()) return;
        player.inventory.add(new ItemStack(Items.OAK_SIGN, 1));
        if (!hasSpawned.contains(player.getUUID())) {
            hasSpawned.add(player.getUUID());
            spawnBuildings(player);
        }
    }

    public static void spawnBuildings(PlayerEntity player) {
        BlockPos basePos = player.level.getHeightmapPos(Heightmap.Type.WORLD_SURFACE, player.blockPosition().offset(10, 0, 0));
        
        for (int x = -20; x <= 20; x++) {
            for (int z = -20; z <= 20; z++) {
                for (int y = 0; y <= 30; y++) {
                    BlockPos p = basePos.offset(x, y, z);
                    player.level.setBlockAndUpdate(p, Blocks.AIR.defaultBlockState());
                    protectedBlocks.remove(p);
                }
                BlockPos floor = basePos.offset(x, -1, z);
                player.level.setBlockAndUpdate(floor, Blocks.GRASS_BLOCK.defaultBlockState());
                protectedBlocks.add(floor);
            }
        }

        for (int y = 0; y <= 25; y++) {
            for (int x = -2; x <= 2; x++) {
                for (int z = -2; z <= 2; z++) {
                    if (Math.abs(x) == 2 && Math.abs(z) == 2) continue;
                    setProtBlock(player, basePos.offset(x, y, z), Blocks.DARK_OAK_LOG.defaultBlockState());
                }
            }
        }
        for (int x = -10; x <= 10; x++) {
            for (int z = -10; z <= 10; z++) {
                for (int y = 15; y <= 30; y++) {
                    if (Math.sqrt(x*x + z*z + (y-22)*(y-22)) < 9) {
                        setProtBlock(player, basePos.offset(x, y, z), Blocks.DARK_OAK_LEAVES.defaultBlockState());
                    }
                }
            }
        }
        for (int y = 1; y <= 5; y++) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    player.level.setBlockAndUpdate(basePos.offset(x, y, z), Blocks.AIR.defaultBlockState());
                    protectedBlocks.remove(basePos.offset(x, y, z));
                }
            }
        }
        BankerEntity banker = ModEntities.BANKER.get().create(player.level);
        if (banker != null) {
            banker.setPos(basePos.getX() + 0.5, basePos.getY() + 1, basePos.getZ() + 0.5);
            banker.setCustomName(new StringTextComponent("§6Роман Кредитор"));
            banker.setCustomNameVisible(true);
            banker.setInvulnerable(false);
            banker.setNoAi(true);
            player.level.addFreshEntity(banker);
        }
        setProtBlock(player, basePos.offset(-2, 2, 0), Blocks.GLASS_PANE.defaultBlockState());
        setProtBlock(player, basePos.offset(-3, 3, 0), Blocks.OAK_SIGN.defaultBlockState());
        
        for (int y = 8; y <= 12; y++) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    player.level.setBlockAndUpdate(basePos.offset(x, y, z), Blocks.AIR.defaultBlockState());
                    protectedBlocks.remove(basePos.offset(x, y, z));
                }
            }
        }
        VillagerEntity dealer = net.minecraft.entity.EntityType.VILLAGER.create(player.level);
        if (dealer != null) {
            dealer.setPos(basePos.getX() + 0.5, basePos.getY() + 8, basePos.getZ() + 0.5);
            dealer.setCustomName(new StringTextComponent("§cДилер"));
            dealer.setCustomNameVisible(true);
            dealer.setInvulnerable(false);
            dealer.setNoAi(true);
            player.level.addFreshEntity(dealer);
        }
        setProtBlock(player, basePos.offset(-2, 9, 0), Blocks.GLASS_PANE.defaultBlockState());
        setProtBlock(player, basePos.offset(-3, 10, 0), Blocks.OAK_SIGN.defaultBlockState());
    }

    @SubscribeEvent
    public void onEntityAttack(AttackEntityEvent event) {
        if (event.getTarget().hasCustomName()) {
            String name = event.getTarget().getCustomName().getString();
            if (name.contains("Дилер") || name.contains("Роман Кредитор")) {
                sendToPrison(event.getPlayer());
            }
        }
    }

    private void sendToPrison(PlayerEntity player) {
        if (prisonTimeLeft.containsKey(player.getUUID())) return;
        returnPositions.put(player.getUUID(), player.blockPosition());
        prisonTimeLeft.put(player.getUUID(), 120);

        for (int i = 0; i < 5; i++) {
            OmonEntity omon = ModEntities.OMON.get().create(player.level);
            if (omon != null) {
                omon.setPos(player.getX() + (i - 2), player.getY(), player.getZ());
                omon.setCustomName(new StringTextComponent("§0ОМОН"));
                omon.setCustomNameVisible(true);
                omon.setItemSlot(EquipmentSlotType.MAINHAND, new ItemStack(Items.IRON_AXE));
                omon.setTarget(player);
                player.level.addFreshEntity(omon);
            }
        }
        player.sendMessage(new StringTextComponent("§cВы ударили сотрудника! ОМОН уже едет!"), player.getUUID());
        
        new Thread(() -> {
            try { Thread.sleep(2000); } catch (Exception ignored) {}
            if (ServerLifecycleHooks.getCurrentServer() == null) return;
            ServerLifecycleHooks.getCurrentServer().execute(() -> {
                if (player instanceof ServerPlayerEntity) {
                    ServerPlayerEntity sp = (ServerPlayerEntity) player;
                    buildPrison(player.level);
                    sp.teleportTo(PRISON_CENTER.getX() + 0.5, PRISON_CENTER.getY() + 1, PRISON_CENTER.getZ() + 0.5);
                    sp.inventory.add(new ItemStack(Items.DIAMOND_PICKAXE, 1));
                    sp.sendMessage(new StringTextComponent("§cСуд приговорил вас к 120 секундам исправительных работ. Ломайте булыжник!"), sp.getUUID());
                }
            });
        }).start();
    }

    private void buildPrison(net.minecraft.world.World level) {
        for (int x = -3; x <= 3; x++) {
            for (int y = 0; y <= 5; y++) {
                for (int z = -3; z <= 3; z++) {
                    BlockPos p = PRISON_CENTER.offset(x, y, z);
                    if (x == -3 || x == 3 || z == -3 || z == 3 || y == 0 || y == 5) {
                        level.setBlockAndUpdate(p, Blocks.BEDROCK.defaultBlockState());
                        protectedBlocks.add(p);
                    } else {
                        level.setBlockAndUpdate(p, Blocks.AIR.defaultBlockState());
                        protectedBlocks.remove(p);
                    }
                }
            }
        }
        level.setBlockAndUpdate(PRISON_CENTER, Blocks.COBBLESTONE.defaultBlockState());
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        PlayerEntity player = event.getPlayer();
        if (player == null) return;
        BlockPos pos = event.getPos();

        if (prisonTimeLeft.containsKey(player.getUUID())) {
            if (event.getState().getBlock() == Blocks.COBBLESTONE && pos.distSqr(PRISON_CENTER) < 10) {
                prisonTimeLeft.put(player.getUUID(), prisonTimeLeft.get(player.getUUID()) - 1);
                player.inventory.add(new ItemStack(Items.DIAMOND, 1));
                player.sendMessage(new StringTextComponent("§a+1 алмаз. Осталось сидеть: " + prisonTimeLeft.get(player.getUUID()) + " сек."), player.getUUID());
                if (ServerLifecycleHooks.getCurrentServer() != null) {
                    ServerLifecycleHooks.getCurrentServer().execute(() -> player.level.setBlockAndUpdate(pos, Blocks.COBBLESTONE.defaultBlockState()));
                }
                event.setCanceled(true);
                return;
            } else {
                event.setCanceled(true);
                return;
            }
        }

        if (protectedBlocks.contains(pos)) {
            event.setCanceled(true);
            player.sendMessage(new StringTextComponent("§cЭто здание защищено!"), player.getUUID());
        }
    }

    @SubscribeEvent
    public void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof PlayerEntity)) return;
        PlayerEntity player = (PlayerEntity) event.getEntity();
        if (prisonTimeLeft.containsKey(player.getUUID())) { event.setCanceled(true); return; }

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
                    player.level.sendBlockUpdated(pos, event.getPlacedBlock(), event.getPlacedBlock(), 3);
                }
            }
        }
    }

    @SubscribeEvent
    public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getTarget().hasCustomName()) {
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
    public void onExplosion(ExplosionEvent.Detonate event) {
        event.getAffectedBlocks().removeIf(pos -> protectedBlocks.contains(pos));
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        tickCounter++;
        
        if (tickCounter % 20 == 0) {
            if (ServerLifecycleHooks.getCurrentServer() == null) return;
            for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                UUID uuid = player.getUUID();
                if (prisonTimeLeft.containsKey(uuid)) {
                    int time = prisonTimeLeft.get(uuid);
                    if (time <= 0) {
                        prisonTimeLeft.remove(uuid);
                        BlockPos returnPos = returnPositions.remove(uuid);
                        if (returnPos != null) {
                            player.teleportTo(returnPos.getX() + 0.5, returnPos.getY(), returnPos.getZ() + 0.5);
                        }
                        for (int i = 0; i < player.inventory.getContainerSize(); i++) {
                            ItemStack stack = player.inventory.getItem(i);
                            if (stack.getItem() == Items.DIAMOND_PICKAXE) { stack.shrink(1); }
                        }
                        player.sendMessage(new StringTextComponent("§aВы отсидели свой срок. Кирка изъята, вы свободны."), uuid);
                    } else if (tickCounter % 1200 == 0) {
                        player.sendMessage(new StringTextComponent("§cОсталось сидеть: " + time + " сек. Ломайте булыжник!"), uuid);
                    }
                }

                if (tickCounter % 24000 == 0) {
                    if (registered.contains(uuid) && debts.getOrDefault(uuid, 0) > 0) {
                        CollectorEntity debtorKiller = ModEntities.COLLECTOR.get().create(player.level);
                        if (debtorKiller != null) {
                            debtorKiller.setPos(player.getX(), player.getY(), player.getZ());
                            debtorKiller.setCustomName(new StringTextComponent("§0Человек в черном"));
                            debtorKiller.setCustomNameVisible(true);
                            debtorKiller.getAttribute(Attributes.MAX_HEALTH).setBaseValue(100.0D);
                            debtorKiller.setHealth(100.0F);
                            debtorKiller.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.3D);
                            debtorKiller.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(10.0D);
                            player.level.addFreshEntity(debtorKiller);
                            
                            player.sendMessage(new StringTextComponent("§cОТДАВАЙ ДОЛГ!"), uuid);
                            
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
                            debts.put(uuid, debts.get(uuid) * 2);
                        }
                    }
                }
            }
        }
    }

    private static void setProtBlock(PlayerEntity player, BlockPos pos, net.minecraft.block.BlockState state) {
        player.level.setBlockAndUpdate(pos, state);
        protectedBlocks.add(pos);
    }
}
