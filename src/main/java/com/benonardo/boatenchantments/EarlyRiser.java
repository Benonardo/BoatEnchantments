package com.benonardo.boatenchantments;

import com.chocohead.mm.api.ClassTinkerers;
import net.fabricmc.loader.api.FabricLoader;

public class EarlyRiser implements Runnable {

    @Override
    public void run() {
        String enchantmentTarget = FabricLoader.getInstance().getMappingResolver().mapClassName("intermediary", "net.minecraft.class_1886");
        ClassTinkerers.enumBuilder(enchantmentTarget).addEnumSubclass("BOAT", this.getClass().getPackageName() + ".BoatEnchantmentTarget").build();
    }

}
