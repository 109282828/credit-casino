package com.sam.creditcasino;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CreditCasinoMod.MOD_ID, value = Dist.CLIENT)
public class HudOverlay {
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onRenderHud(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;
        Minecraft mc = Minecraft.getInstance();
        ClientPlayerEntity player = mc.player;
        if (player == null) return;

        int day = (int) (player.level.getDayTime() / 24000L);
        int debt = EventHandler.debts.getOrDefault(player.getUUID(), 0);
        
        mc.fill(event.getMatrixStack(), 5, 5, 160, 55, 0x80000000);
        
        mc.font.drawShadow(event.getMatrixStack(), "Игрок: " + player.getName().getString(), 10, 10, 0xFFFFFF);
        mc.font.drawShadow(event.getMatrixStack(), "День: " + day, 10, 20, 0xFFFFFF);
        mc.font.drawShadow(event.getMatrixStack(), "Долг: " + debt, 10, 30, 0xFF5555);
        mc.font.drawShadow(event.getMatrixStack(), "Просрочка: " + (debt > 0 ? "ДА" : "НЕТ"), 10, 40, 0xFFFF55);
    }
}
