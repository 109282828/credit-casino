package com.sam.creditcasino;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import java.util.UUID;

public class UpgradeItem extends Item {
    public UpgradeItem(Properties properties) { super(properties); }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (!world.isClientSide) {
            player.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(new AttributeModifier(UUID.randomUUID(), "upgrade_health", 100.0D, AttributeModifier.Operation.ADDITION));
            player.setHealth(120.0F);
            player.getAttribute(Attributes.ATTACK_DAMAGE).addPermanentModifier(new AttributeModifier(UUID.randomUUID(), "upgrade_damage", 50.0D, AttributeModifier.Operation.ADDITION));
            player.getAttribute(Attributes.MOVEMENT_SPEED).addPermanentModifier(new AttributeModifier(UUID.randomUUID(), "upgrade_speed", 0.5D, AttributeModifier.Operation.ADDITION));
            player.getAttribute(Attributes.KNOCKBACK_RESISTANCE).addPermanentModifier(new AttributeModifier(UUID.randomUUID(), "upgrade_kb", 1.0D, AttributeModifier.Operation.ADDITION));
            
            player.sendMessage(new StringTextComponent("§aВы активировали 100% Апгрейд!"), player.getUUID());
            player.playSound(SoundEvents.PLAYER_LEVELUP, 1.0F, 1.0F);
            itemstack.shrink(1);
        }
        return ActionResult.success(itemstack);
    }
}
