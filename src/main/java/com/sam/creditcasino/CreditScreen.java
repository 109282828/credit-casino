package com.sam.creditcasino;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class CreditScreen extends ContainerScreen<CreditContainer> {

    private static final ResourceLocation BG = new ResourceLocation("textures/gui/container/generic_54.png");

    public CreditScreen(CreditContainer container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);
        this.imageHeight = 222;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        int[] loans = {5, 10, 20, 30, 50, 100, 200};
        for (int i = 0; i < loans.length; i++) {
            int amount = loans[i];
            int bx = x + 10 + (i % 4) * 45;
            int by = y + 40 + (i / 4) * 20;
            this.addButton(new Button(bx, by, 40, 18, new StringTextComponent(amount + " алм."), b -> {
                this.menu.takeLoan(amount);
            }));
        }

        this.addButton(new Button(x + 100, y + 120, 60, 20, new StringTextComponent("Уйти"), b -> {
            this.minecraft.player.closeContainer();
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
        
        int debt = EventHandler.debts.getOrDefault(this.minecraft.player.getUUID(), 0);
        
        this.font.drawShadow(matrixStack, "БАНК: РОМАН КРЕДИТОР", x + 40, y + 10, 0xFFD700);
        this.font.drawShadow(matrixStack, "Текущий долг: " + debt + " алмазов", x + 10, y + 25, 0xFFFFFF);
        this.font.drawShadow(matrixStack, "Выберите сумму займа:", x + 10, y + 30, 0xFFFFFF);
    }
}
