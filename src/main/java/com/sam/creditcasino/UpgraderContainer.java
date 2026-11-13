package com.sam.creditcasino;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.StringTextComponent;

public class UpgraderContainer extends Container {
    private final PlayerEntity player;
    public int currentChance = 22;

    public UpgraderContainer(int windowId, PlayerInventory playerInv, PacketBuffer data) {
        super(ModContainers.UPGRADER_CONTAINER.get(), windowId);
        this.player = playerInv.player;
        this.addSlot(new Slot(playerInv, 0, 80, 20));
        this.addSlot(new Slot(playerInv, 1, 80, 60) {
            @Override public boolean mayPlace(ItemStack stack) { return false; }
        });
        
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 140 + i * 18));
            }
        }
        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInv, k, 8 + k * 18, 198));
        }
    }

    public void tryUpgrade(int chance) {
        ItemStack input = this.getSlot(0).getItem();
        if (input.isEmpty()) return;
        
        if (player.level.random.nextInt(100) < chance) {
            ItemStack output = input.copy();
            output.setCount(output.getCount() * 2);
            input.shrink(input.getCount());
            player.inventory.add(output);
            player.sendMessage(new StringTextComponent("§aУспех! Предмет улучшен."), player.getUUID());
        } else {
            input.shrink(1);
            player.sendMessage(new StringTextComponent("§cПровал! Предмет уничтожен."), player.getUUID());
        }
    }

    @Override
    public boolean stillValid(PlayerEntity player) { return true; }
    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) { return ItemStack.EMPTY; }
}
