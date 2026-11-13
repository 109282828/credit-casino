package com.sam.creditcasino;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class SpawnCommand {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSource> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("casino")
            .then(Commands.literal("spawn")
                .executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayerOrException();
                    EventHandler.spawnBuildings(player);
                    context.getSource().sendSuccess(new StringTextComponent("§aЗдания успешно заспавнены!"), false);
                    return 1;
                })
            )
        );
    }
}
