package com.benonardo.boatenchantments.duck;

import net.minecraft.enchantment.Enchantment;

import java.util.Map;

public interface EnchantableBoatEntity {

    Map<Enchantment, Integer> getEnchantments();
    void setEnchantments(Map<Enchantment, Integer> value);

    boolean isEnchanted();

}
