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

        this.addButton(new Button(x + 100, y + 90, 80, 20, new StringTextComponent("СТАВКА"), b -> {
            if (!this.menu.isSpinning) {
                this.menu.spin();
            }
        }));

        this.addButton(new Button(x + 120, y + 190, 60, 20, new StringTextComponent("Уйти"), b -> {
            this.minecraft.player.closeContainer();
        }));
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        
        this.fillGradient(matrixStack, x, y, x + this.imageWidth, y + this.imageHeight, 0xFF2A1A15, 0xFF1A0F0A);
        
        this.fill(matrixStack, x + 80, y + 115, x + 180, y + 175, 0xFF1E3A8A);
        this.fill(matrixStack, x + 80, y + 115, x + 180, y + 117, 0xFFD4AF37);
        this.fill(matrixStack, x + 80, y + 173, x + 180, y + 175, 0xFFD4AF37);
        this.fill(matrixStack, x + 80, y + 115, x + 82, y + 175, 0xFFD4AF37);
        this.fill(matrixStack, x + 178, y + 115, x + 180, y + 175, 0xFFD4AF37);
        
        this.fill(matrixStack, x + 200, y + 115, x + 240, y + 175, 0xFF555555);
        
        this.fill(matrixStack, x + 20, y + 115, x + 70, y + 175, 0xFF444444);
        this.fill(matrixStack, x + 250, y + 115, x + 300, y + 175, 0xFF444444);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        
        this.font.drawShadow(matrixStack, "ПОСЛЕДНИЙ ШАНС", x + 120, y + 10, 0xFFFFFF);
        this.font.drawShadow(matrixStack, "X0 · X2 · X3 · X4 · X5 · X10 · X50 · X100 · X1000", x + 50, y + 25, 0xFFAA00);
        
        this.fill(matrixStack, x + 90, y + 60, x + 190, y + 80, 0xFF555555);
        this.font.drawShadow(matrixStack, "Ставка: " + this.menu.selectedBet + " алм.", x + 105, y + 66, 0xFFFFFF);
        
        if (this.menu.isSpinning) {
            float diff = this.menu.spinTarget - this.menu.currentAngle;
            this.menu.currentAngle += diff * 0.1f;
            
            if (Math.abs(diff) < 0.1f) {
                this.menu.currentAngle = this.menu.spinTarget;
                this.menu.isSpinning = false;
                
                if (this.menu.lastWinAmount > 0) {
                    ItemStack reward = new ItemStack(Items.DIAMOND, this.menu.lastWinAmount);
                    if (!this.minecraft.player.inventory.add(reward)) {
                        this.minecraft.player.drop(reward, false);
                    }
                    this.minecraft.player.sendMessage(new StringTextComponent("§aВы выиграли X" + this.menu.lastMultiplier + "! " + this.menu.lastWinAmount + " алмазов!"), this.minecraft.player.getUUID());
                } else {
                    this.minecraft.player.sendMessage(new StringTextComponent("§cВы проиграли!"), this.minecraft.player.getUUID());
                }
            }
        }
        
        String centerText = this.menu.isSpinning ? "..." : (this.menu.lastMultiplier >= 0 ? "x" + this.menu.lastMultiplier : "x?");
        this.font.drawShadow(matrixStack, centerText, x + 120, y + 140, 0xFFFFFF);
        
        this.font.drawShadow(matrixStack, "?", x + 215, y + 140, 0xFFFFFF);
        
        this.fill(matrixStack, x + 50, y + 180, x + 130, y + 200, 0xFF444444);
        this.font.drawShadow(matrixStack, "Выбрать синюю", x + 60, y + 186, 0xFFFFFF);
        
        this.fill(matrixStack, x + 190, y + 180, x + 270, y + 200, 0xFF444444);
        this.font.drawShadow(matrixStack, "Выбрать зелёную", x + 200, y + 186, 0xFFFFFF);
        
        this.font.drawShadow(matrixStack, "ВЫИГРЫШ: " + this.menu.lastWinAmount + " алм. x" + Math.max(0, this.menu.lastMultiplier), x + 100, y + 205, 0xFFFFFF);
    }
}
