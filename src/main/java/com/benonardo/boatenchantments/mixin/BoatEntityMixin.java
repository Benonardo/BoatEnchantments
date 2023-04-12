package com.benonardo.boatenchantments.mixin;

import com.benonardo.boatenchantments.duck.EnchantableBoatEntity;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.block.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.ThornsEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(BoatEntity.class)
public abstract class BoatEntityMixin extends Entity implements EnchantableBoatEntity {

    @Shadow public abstract Item asItem();

    @Shadow public abstract @Nullable Entity getPrimaryPassenger();

    @SuppressWarnings("WrongEntityDataParameterClass")
    @Unique
    private static final TrackedData<Boolean> ENCHANTED = DataTracker.registerData(BoatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    @Unique
    private Map<Enchantment, Integer> enchantments = new HashMap<>();
    @Unique
    private final Random random = Random.create();

    public BoatEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "initDataTracker", at  = @At("TAIL"))
    private void initEnchantedTracker(CallbackInfo ci) {
        this.dataTracker.startTracking(ENCHANTED, false);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readEnchantmentsFromNbt(NbtCompound nbt, CallbackInfo ci) {
        this.setEnchantments(EnchantmentHelper.fromNbt(nbt.getList(ItemStack.ENCHANTMENTS_KEY, NbtElement.COMPOUND_TYPE)));
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeEnchantmentsToNbt(NbtCompound nbt, CallbackInfo ci) {
        var enchantmentList = new NbtList();
        this.enchantments.forEach((enchantment, lvl) -> enchantmentList.add(EnchantmentHelper.createNbt(Registry.ENCHANTMENT.getId(enchantment), lvl)));
        nbt.put(ItemStack.ENCHANTMENTS_KEY, enchantmentList);
    }

    // frost walker
    @Inject(method = "tick", at = @At("TAIL"))
    private void freezeWaterWhenFrostWalkerEnchanted(CallbackInfo ci) {
        if (this.enchantments.containsKey(Enchantments.FROST_WALKER)) {
            var level = this.enchantments.get(Enchantments.FROST_WALKER);
            var blockState = Blocks.FROSTED_ICE.getDefaultState();
            float f = (float)Math.min(16, 2 + level);
            BlockPos.Mutable mutable = new BlockPos.Mutable();

            for (var pos : BlockPos.iterate(this.getBlockPos().add(-f, -1.0, -f), this.getBlockPos().add(f, -1.0, f))) {
                if (pos.isWithinDistance(this.getPos(), (double) f)) {
                    mutable.set(pos.getX(), pos.getY() + 1, pos.getZ());
                    BlockState blockState2 = world.getBlockState(mutable);
                    if (blockState2.isAir()) {
                        BlockState blockState3 = world.getBlockState(pos);
                        if (blockState3.getMaterial() == Material.WATER && blockState3.get(FluidBlock.LEVEL) == 0 && blockState.canPlaceAt(world, pos) && world.canPlace(blockState, pos, ShapeContext.absent())) {
                            world.setBlockState(pos, blockState);
                            world.createAndScheduleBlockTick(pos, Blocks.FROSTED_ICE, MathHelper.nextInt(this.random, 60, 120));
                        }
                    }
                }
            }
        }
    }

    // thorns
    @Inject(method = "collidesWith", at = @At("TAIL"))
    private void damageEntitiesOnCollisionWhenThornsEnchanted(Entity other, CallbackInfoReturnable<Boolean> cir) {
        if (enchantments.containsKey(Enchantments.THORNS) && cir.getReturnValue() && !this.world.isClient && ThornsEnchantment.shouldDamageAttacker(this.enchantments.get(Enchantments.THORNS), this.random)) {
            world.getServer().execute(() -> other.damage(DamageSource.thorns(this), ThornsEnchantment.getDamageAmount(this.enchantments.get(Enchantments.THORNS), this.random)));
        }
    }

    // efficiency
    /*@ModifyConstant(method = "updatePaddles", constant = @Constant(floatValue = 0.04F))
    private float accelerateWhenEfficiencyEnchanted(float original) {
        if (enchantments.containsKey(Enchantments.EFFICIENCY)) {
            return original + 1f * enchantments.get(Enchantments.EFFICIENCY);
        }
        return original;
    }*/

    // unbreaking
    @ModifyExpressionValue(method = "fall", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/vehicle/BoatEntity;fallDistance:F", opcode = Opcodes.GETFIELD, ordinal = 0))
    private float dontBreakOnFallWhenUnbreakingEnchanted(float original) {
        if (enchantments.containsKey(Enchantments.UNBREAKING)) {
            return 0.0f;
        }
        return original;
    }

    // curse of vanishing
    @Inject(method = "dropItems", at = @At("HEAD"), cancellable = true)
    private void dontDropWhenVanishingCursed(DamageSource source, CallbackInfo ci) {
        if (this.enchantments.isEmpty()) return;
        if (!this.enchantments.containsKey(Enchantments.VANISHING_CURSE)) {
            var stack = this.asItem().getDefaultStack();
            EnchantmentHelper.set(this.enchantments, stack);
            this.dropStack(stack);
        }
        ci.cancel();
    }

    @Unique
    @Override
    public Map<Enchantment, Integer> getEnchantments() {
        return this.enchantments;
    }

    @Unique
    @Override
    public void setEnchantments(Map<Enchantment, Integer> value) {
        this.enchantments = value;
        this.dataTracker.set(ENCHANTED, !value.isEmpty());
    }

    @Unique
    @Override
    public boolean isEnchanted() {
        return this.dataTracker.get(ENCHANTED);
    }

}
