package com.sam.creditcasino;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class CasinoScreen extends ContainerScreen<CasinoContainer> {

    private static final ResourceLocation BG = new ResourceLocation("textures/gui/container/generic_54.png");

    public CasinoScreen(CasinoContainer container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);
        this.imageHeight = 222;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        int[] bets = {1, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60};
        for (int i = 0; i < bets.length; i++) {
            int bet = bets[i];
            int bx = x + 10 + (i % 7) * 20;
            int by = y + 15 + (i / 7) * 20;
            this.addButton(new Button(bx, by, 18, 18, new StringTextComponent("" + bet), b -> {
                this.menu.setBet(bet);
            }));
        }

        this.addButton(new Button(x + 50, y + 85, 60, 20, new StringTextComponent("СТАВКА"), b -> {
            if (!this.menu.isSpinning) {
                this.menu.spin();
            }
        }));
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        this.minecraft.getTextureManager().bind(BG);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        this.blit(matrixStack, x, y, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        
        int cx = x + 80;
        int cy = y + 50;
        
        RenderSystem.pushMatrix();
        RenderSystem.translatef(cx, cy, 0);
        
        if (this.menu.isSpinning) {
            float diff = this.menu.spinTarget - this.menu.currentAngle;
            this.menu.currentAngle += diff * 0.05f;
            
            if (Math.abs(diff) < 0.1f) {
                this.menu.currentAngle = this.menu.spinTarget;
                this.menu.isSpinning = false;
                
                int winAmount = this.menu.selectedBet * this.menu.lastMultiplier;
                if (winAmount > 0) {
                    ItemStack reward = new ItemStack(Items.DIAMOND, winAmount);
                    if (!this.minecraft.player.inventory.add(reward)) {
                        this.minecraft.player.drop(reward, false);
                    }
                    this.minecraft.player.sendMessage(new StringTextComponent("§aВы выиграли X" + this.menu.lastMultiplier + "! " + winAmount + " алмазов!"), this.minecraft.player.getUUID());
                } else {
                    this.minecraft.player.sendMessage(new StringTextComponent("§cВы проиграли!"), this.minecraft.player.getUUID());
                }
            }
        }
        
        RenderSystem.rotatef(this.menu.currentAngle, 0, 0, 1);
        this.fill(matrixStack, -1, -30, 1, 0, 0xFFFF0000);
        RenderSystem.popMatrix();
        
        this.font.drawShadow(matrixStack, "Ставка: " + this.menu.selectedBet, x + 60, y + 75, 0xFFFFFF);
        this.font.drawShadow(matrixStack, "Алмазы:", x + 10, y + 95, 0xFFFFFF);
    }
}
