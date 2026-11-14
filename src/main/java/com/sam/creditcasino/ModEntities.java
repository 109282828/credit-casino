package com.sam.creditcasino;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, CreditCasinoMod.MOD_ID);

    public static final RegistryObject<EntityType<CollectorEntity>> COLLECTOR = ENTITIES.register("collector",
            () -> EntityType.Builder.of(CollectorEntity::new, EntityClassification.MONSTER).sized(0.6F, 1.95F).build(new ResourceLocation(CreditCasinoMod.MOD_ID, "collector").toString()));

    public static final RegistryObject<EntityType<BankerEntity>> BANKER = ENTITIES.register("banker",
            () -> EntityType.Builder.of(BankerEntity::new, EntityClassification.MONSTER).sized(0.6F, 1.95F).build(new ResourceLocation(CreditCasinoMod.MOD_ID, "banker").toString()));

    public static final RegistryObject<EntityType<OmonEntity>> OMON = ENTITIES.register("omon",
            () -> EntityType.Builder.of(OmonEntity::new, EntityClassification.MONSTER).sized(0.6F, 1.95F).build(new ResourceLocation(CreditCasinoMod.MOD_ID, "omon").toString()));
}
