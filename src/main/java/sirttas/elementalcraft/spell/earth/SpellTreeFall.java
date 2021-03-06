package sirttas.elementalcraft.spell.earth;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.stream.Stream;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import sirttas.elementalcraft.spell.Spell;

public class SpellTreeFall extends Spell {

	public static final String NAME = "tree_fall";

	private boolean isValidBlock(Block block) {
		return BlockTags.LOGS.contains(block) || BlockTags.LEAVES.contains(block);
	}

	private void cutTree(World world, BlockPos target) {
		Queue<BlockPos> queue = new ArrayDeque<>();
		float rangeSq = getRange();

		rangeSq *= rangeSq;
		queue.offer(target);
		while (!queue.isEmpty()) {
			BlockPos pos = queue.poll();
			Block block = world.getBlockState(pos).getBlock();
			
			if (isValidBlock(block) && pos.distanceSq(target) <= rangeSq) {
				world.destroyBlock(pos, true);
				Stream.of(Direction.values()).filter(d -> d != Direction.DOWN).forEach(d -> queue.offer(pos.offset(d)));
			}
		}
	}

	@Override
	public ActionResultType castOnBlock(Entity sender, BlockPos target) {
		World world = sender.getEntityWorld();

		if (!world.isRemote && isValidBlock(world.getBlockState(target).getBlock())) {
			cutTree(world, target);
			return ActionResultType.SUCCESS;
		}

		return ActionResultType.PASS;
	}


}
