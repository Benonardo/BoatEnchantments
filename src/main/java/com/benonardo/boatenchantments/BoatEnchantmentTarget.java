package com.benonardo.boatenchantments;

import com.benonardo.boatenchantments.mixin.EnchantmentTargetMixin;
import net.minecraft.item.BoatItem;
import net.minecraft.item.Item;

public class BoatEnchantmentTarget extends EnchantmentTargetMixin {

    @Override
    public boolean isAcceptableItem(Item item) {
        return item instanceof BoatItem;
    }

}
