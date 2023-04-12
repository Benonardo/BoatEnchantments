package com.benonardo.boatenchantments.mixin;

import com.benonardo.boatenchantments.duck.EnchantableBoatEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.BoatItem;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(BoatItem.class)
public class BoatItemMixin {

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyVariable(method = "use", at = @At("STORE"))
    private BoatEntity appendEnchantments(BoatEntity original, World world, PlayerEntity user, Hand hand) {
        ((EnchantableBoatEntity)original).setEnchantments(EnchantmentHelper.get(user.getStackInHand(hand)));
        return original;
    }

}
