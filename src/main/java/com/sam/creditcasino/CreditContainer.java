package com.sam.creditcasino;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.StringTextComponent;

public class CreditContainer extends Container {
    private final PlayerEntity player;
    public int selectedLoan = 0;

    public CreditContainer(int windowId, PlayerInventory playerInv, PacketBuffer data) {
        super(ModContainers.CREDIT_CONTAINER.get(), windowId);
        this.player = playerInv.player;
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 140 + i * 18));
            }
        }
        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInv, k, 8 + k * 18, 198));
        }
    }

    public void takeLoan(int amount) {
        if (EventHandler.registered.contains(player.getUUID())) {
            EventHandler.debts.put(player.getUUID(), EventHandler.debts.getOrDefault(player.getUUID(), 0) + amount);
            player.inventory.add(new ItemStack(net.minecraft.item.Items.DIAMOND, amount));
            player.sendMessage(new StringTextComponent("§aВы взяли кредит: " + amount + " алмазов!"), player.getUUID());
        } else {
            player.sendMessage(new StringTextComponent("§cСначала зарегистрируйтесь!"), player.getUUID());
        }
    }

    @Override
    public boolean stillValid(PlayerEntity player) { return true; }
    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) { return ItemStack.EMPTY; }
}
