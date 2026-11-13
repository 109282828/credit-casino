package com.sam.creditcasino;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class CasinoScreen extends ContainerScreen<CasinoContainer> {

    public CasinoScreen(CasinoContainer container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);
        this.imageWidth = 320;
        this.imageHeight = 220;
    }

    @Override
    protected void init() {
        super.init();
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        int[] bets = {1, 5, 10, 20, 50, 100, 200, 500, 1000};
        for (int i = 0; i < bets.length; i++) {
            int bet = bets[i];
            int bx = x + 40 + (i % 5) * 40;
            int by = y + 40 + (i / 5) * 25;
            this.addButton(new Button(bx, by, 35, 20, new StringTextComponent("" + bet), b -> {
                this.menu.setBet(bet);
            }));
        }

        this.addButton(new Button(x + 240, y + 40, 60, 20, new StringTextComponent("ВО-БАНК"), b -> {
            this.menu.setAllIn();
        }));

        this.addButton(new Button(x + 100, y + 90, 80,
