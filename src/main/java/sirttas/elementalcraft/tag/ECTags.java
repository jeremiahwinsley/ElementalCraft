package sirttas.elementalcraft.tag;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import sirttas.elementalcraft.ElementalCraft;

public class ECTags {
	public static class Items {
		public static final Tag<Item> INFUSABLE_SWORDS = createTag("infusable/swords");
		public static final Tag<Item> INFUSABLE_PICKAXES = createTag("infusable/pickaxes");
		public static final Tag<Item> INFUSABLE_AXES = createTag("infusable/axes");
		public static final Tag<Item> INFUSABLE_SHOVELS = createTag("infusable/shovels");
		public static final Tag<Item> INFUSABLE_HOES = createTag("infusable/hoes");
		public static final Tag<Item> INFUSABLE_SHILDS = createTag("infusable/shields");
		public static final Tag<Item> INFUSABLE_BOWS = createTag("infusable/bows");
		public static final Tag<Item> INFUSABLE_CROSSBOWS = createTag("infusable/crossbows");
		public static final Tag<Item> INFUSABLE_HELMETS = createTag("infusable/helmets");
		public static final Tag<Item> INFUSABLE_CHESTPLATES = createTag("infusable/chestplates");
		public static final Tag<Item> INFUSABLE_LEGGINGS = createTag("infusable/leggings");
		public static final Tag<Item> INFUSABLE_BOOTS = createTag("infusable/boots");

		public static final Tag<Item> PUREROCKS = createTag("purerocks");

		private static Tag<Item> createTag(String name) {
			return new ItemTags.Wrapper(new ResourceLocation(ElementalCraft.MODID, name));
		}
	}

	public static class Blocks {
		public static final Tag<Block> LAVASHRINE_LIQUIFIABLES = createTag("lavashrine_liquifiables");
		public static final Tag<Block> PUREROCKS = createTag("purerocks");

		private static Tag<Block> createTag(String name) {
			return new BlockTags.Wrapper(new ResourceLocation(ElementalCraft.MODID, name));
		}
	}
}
