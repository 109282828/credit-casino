package com.sam.creditcasino;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.StringTextComponent;
import java.util.Random;

public class CasinoContainer extends Container {

    private final PlayerEntity player;
    public int selectedBet = 1;
    public float spinTarget = 0f;
    public float currentAngle = 0f;
    public boolean isSpinning = false;
    public int lastMultiplier = -1;
    public int lastWinAmount = 0;

    public CasinoContainer(int windowId, PlayerInventory playerInv, PacketBuffer data) {
        super(ModContainers.CASINO_CONTAINER.get(), windowId);
        this.player = playerInv.player;
        
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInv, j + i * 9 + 9, -1000, -1000));
            }
        }
        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInv, k, -1000, -1000));
        }
    }

    public void setBet(int amount) { this.selectedBet = amount; }
    
    public void setAllIn() {
        int diamonds = 0;
        for (int i = 0; i < player.inventory.getContainerSize(); i++) {
            ItemStack stack = player.inventory.getItem(i);
            if (stack.getItem() == Items.DIAMOND) diamonds += stack.getCount();
        }
        if (diamonds > 0) this.selectedBet = diamonds;
    }

    public void spin() {
        if (isSpinning) return;
        int diamonds = 0;
        for (int i = 0; i < player.inventory.getContainerSize(); i++) {
            ItemStack stack = player.inventory.getItem(i);
            if (stack.getItem() == Items.DIAMOND) diamonds += stack.getCount();
        }
        if (diamonds < selectedBet) {
            player.sendMessage(new StringTextComponent("§cНедостаточно алмазов!"), player.getUUID());
            return;
        }

        int toRemove = selectedBet;
        for (int i = 0; i < player.inventory.getContainerSize(); i++) {
            ItemStack stack = player.inventory.getItem(i);
            if (stack.getItem() == Items.DIAMOND) {
                int take = Math.min(stack.getCount(), toRemove);
                stack.shrink(take);
                toRemove -= take;
                if (toRemove <= 0) break;
            }
        }

        Random rand = new Random();
        int roll = rand.nextInt(100);
        int multiplier = 0;
        
        if (roll < 50) multiplier = 0;       // 50%
        else if (roll < 70) multiplier = 2;  // 20%
        else if (roll < 85) multiplier = 3;  // 15%
        else if (roll < 95) multiplier = 4;  // 10%
        else if (roll < 98) multiplier = 5;  // 3%
        else if (roll < 99) multiplier = 10; // 1%
        else multiplier = 0;

        this.lastMultiplier = multiplier;
        this.lastWinAmount = selectedBet * multiplier;
        this.spinTarget = currentAngle + 720 + (multiplier * 36); 
        this.isSpinning = true;
    }

    @Override
    public boolean stillValid(PlayerEntity player) { return true; }
    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) { return ItemStack.EMPTY; }
}
