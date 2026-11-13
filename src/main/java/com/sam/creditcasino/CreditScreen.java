package com.sam.creditcasino;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class CreditScreen extends ContainerScreen<CreditContainer> {

    public CreditScreen(CreditContainer container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);
        this.imageWidth = 320;
        this.imageHeight = 200;
    }

    @Override
    protected void init() {
        super.init();
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        int[] loans = {10, 50, 100, 200};
        for (int i = 0; i < loans.length; i++) {
            int amount = loans[i];
            int bx = x + 40 + i * 60;
            int by = y + 60;
            this.addButton(new Button(bx, by, 50, 20, new StringTextComponent(amount + " алмазов"), b -> {
                this.menu.takeLoan(amount);
            }));
        }

        this.addButton(new Button(x + 130, y + 160, 60, 20, new StringTextComponent("Уйти"), b -> {
            this.minecraft.player.closeContainer();
        }));
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        
        this.fillGradient(matrixStack, x, y, x + this.imageWidth, y + this.imageHeight, 0xFF2A1A15, 0xFF1A0F0A);
        this.fill(matrixStack, x, y, x + this.imageWidth, y + 2, 0xFFD4AF37);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        
        int debt = EventHandler.debts.getOrDefault(this.minecraft.player.getUUID(), 0);
        
        this.font.drawShadow(matrixStack, "Долг: " + debt + " алм. (" + debt + ".0 value)", x + 20, y + 15, 0xFFFFFF);
        this.font.drawShadow(matrixStack, "Лимит занятого: " + debt + " / 100 алмазов", x + 20, y + 30, 0xAAAAAA);
        this.font.drawShadow(matrixStack, "Выберите сумму займа", x + 20, y + 45, 0xFF5555);
        this.font.drawShadow(matrixStack, "Отдаёте: 0 x Воздух", x + 20, y + 100, 0xAAAAAA);
        this.font.drawShadow(matrixStack, "Погасится: 0.0 алм. -> остаток: 0.0", x + 20, y + 115, 0xAAAAAA);
        
        this.fill(matrixStack, x + 20, y + 130, x + 150, y + 150, 0xFF555555);
        this.font.drawShadow(matrixStack, "За кого: NorthKenza", x + 30, y + 135, 0xFFFFFF);
        
        this.fill(matrixStack, x + 170, y + 130, x + 300, y + 150, 0xFF555555);
        this.font.drawShadow(matrixStack, "Передать предметы", x + 180, y + 135, 0xFFFFFF);
    }
}
