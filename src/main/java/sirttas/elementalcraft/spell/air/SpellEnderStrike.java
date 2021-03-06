package sirttas.elementalcraft.spell.air;

import java.util.Comparator;

import com.google.common.collect.Multimap;

import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import sirttas.elementalcraft.entity.EntityHelper;
import sirttas.elementalcraft.spell.Spell;

public class SpellEnderStrike extends Spell {

	public static final String NAME = "ender_strike";

	@Override
	public ActionResultType castOnEntity(Entity sender, Entity target) {
		if (sender instanceof LivingEntity) {
			LivingEntity livingSender = (LivingEntity) sender;
			Vector3d newPos = target.getPositionVec().add(target.getLookVec().inverse().normalize());

			if (MinecraftForge.EVENT_BUS.post(new EnderTeleportEvent(livingSender, newPos.x, newPos.y + 0.5F, newPos.z, 0))) {
				return ActionResultType.SUCCESS;
			}
			if (livingSender.attemptTeleport(newPos.x, newPos.y + 0.5F, newPos.z, true)) {
				livingSender.lookAt(EntityAnchorArgument.Type.EYES, target.getPositionVec());
				livingSender.getEntityWorld().playSound(null, livingSender.prevPosX, livingSender.prevPosY, livingSender.prevPosZ, SoundEvents.ENTITY_ENDERMAN_TELEPORT,
						livingSender.getSoundCategory(), 1.0F, 1.0F);
				livingSender.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
				if (livingSender instanceof PlayerEntity) {
					PlayerEntity playerSender = (PlayerEntity) livingSender;

					playerSender.attackTargetEntityWithCurrentItem(target);
					playerSender.resetCooldown();
					EntityHelper.swingArm(playerSender);
				} else {
					livingSender.attackEntityAsMob(target);
				}
				return ActionResultType.SUCCESS;
			}
		}
		return ActionResultType.PASS;
	}

	@Override
	public ActionResultType castOnSelf(Entity sender) {
		Vector3d pos = sender.getPositionVec();

		return sender.getEntityWorld().getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(pos, pos.add(1, 1, 1)).grow(getRange())).stream()
				.filter(IMob.class::isInstance).sorted(Comparator.comparingDouble(e -> pos.distanceTo(e.getPositionVec()))).findFirst().map(e -> castOnEntity(sender, e)).orElse(ActionResultType.PASS);
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getOnUseAttributeModifiers() {
		Multimap<Attribute, AttributeModifier> multimap = super.getOnUseAttributeModifiers();

		multimap.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", 1.0D, AttributeModifier.Operation.MULTIPLY_TOTAL));
		multimap.put(ForgeMod.REACH_DISTANCE.get(), new AttributeModifier(REACH_DISTANCE_MODIFIER, "Reach distance modifier", 5.0D, AttributeModifier.Operation.ADDITION));
		return multimap;
	}
}
