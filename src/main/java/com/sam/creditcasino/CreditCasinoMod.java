package com.sam.creditcasino;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("creditcasino")
public class CreditCasinoMod {
    public static final String MOD_ID = "creditcasino";
    private static final Logger LOGGER = LogManager.getLogger();

    public CreditCasinoMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        ModItems.ITEMS.register(modEventBus);
        ModContainers.CONTAINERS.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(new EventHandler());
        MinecraftForge.EVENT_BUS.register(new HudOverlay());
        
        LOGGER.info("Credit & Casino mod loaded.");
    }
}
