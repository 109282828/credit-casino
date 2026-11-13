package com.sam.creditcasino;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CreditCasinoMod.MOD_ID);

    public static final RegistryObject<Item> UPGRADE_BOOK = ITEMS.register("upgrade_book",
            () -> new UpgradeItem(new Item.Properties().tab(ItemGroup.TAB_MISC).stacksTo(1)));
}
