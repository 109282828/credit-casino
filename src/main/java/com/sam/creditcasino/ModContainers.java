package com.sam.creditcasino;

import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModContainers {
    public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, CreditCasinoMod.MOD_ID);

    public static final RegistryObject<ContainerType<CasinoContainer>> CASINO_CONTAINER = CONTAINERS.register("casino",
            () -> IForgeContainerType.create((windowId, inv, data) -> new CasinoContainer(windowId, inv, data)));
}
