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

    public CasinoContainer(int windowId, PlayerInventory playerInv, PacketBuffer data) {
        super(ModContainers.CASINO_CONTAINER.get(), windowId);
        this.player = playerInv.player;
        
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 110 + i * 18));
            }
        }
        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInv, k, 8 + k * 18, 168));
        }
    }

    public void setBet(int amount) { this.selectedBet = amount; }

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
        
        if (roll < 30) multiplier = 0;
        else if (roll < 45) multiplier = 2;
        else if (roll < 60) multiplier = 3;
        else if (roll < 70) multiplier = 4;
        else if (roll < 80) multiplier = 5;
        else if (roll < 89) multiplier = 10;
        else if (roll < 94) multiplier = 100;
        else multiplier = 0;

        this.lastMultiplier = multiplier;
        float[] angles = {0f, 45f, 90f, 135f, 180f, 225f, 270f, 315f};
        int index = 0;
        switch (multiplier) {
            case 0: index = 0; break;
            case 2: index = 1; break;
            case 3: index = 2; break;
            case 4: index = 3; break;
            case 5: index = 4; break;
            case 10: index = 5; break;
            case 100: index = 6; break;
        }
        this.spinTarget = currentAngle + 720 + angles[index];
        this.isSpinning = true;
    }

    @Override
    public boolean stillValid(PlayerEntity player) { return true; }
    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) { return ItemStack.EMPTY; }
}
