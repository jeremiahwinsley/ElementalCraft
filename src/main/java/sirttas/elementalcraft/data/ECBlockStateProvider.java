package sirttas.elementalcraft.data;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.registries.ForgeRegistries;
import sirttas.elementalcraft.ElementalCraft;
import sirttas.elementalcraft.block.pipe.BlockElementPipe;
import sirttas.elementalcraft.block.shrine.overload.BlockOverloadShrine;

public class ECBlockStateProvider extends BlockStateProvider {

	private ExistingFileHelper existingFileHelper;

	public ECBlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
		super(gen, ElementalCraft.MODID, exFileHelper);
		existingFileHelper = exFileHelper;
	}

	@Override
	protected void registerStatesAndModels() {
		for (Block block : ForgeRegistries.BLOCKS) {
			if (ElementalCraft.MODID.equals(block.getRegistryName().getNamespace()) && !exists(block)) {
				save(block);
			}
		}
	}

	private boolean exists(Block block) {
		return existingFileHelper.exists(block.getRegistryName(), ResourcePackType.CLIENT_RESOURCES, ".json", "blockstates");
	}

	private ResourceLocation prefix(String name) {
		return new ResourceLocation(ElementalCraft.MODID, "block/" + name);
	}

	private void save(Block block) {
		String name = block.getRegistryName().getPath();

		if (block instanceof SlabBlock) { // From botania
			ModelFile file = models().getExistingFile(prefix(name));
			ModelFile fullFile = models().getExistingFile(prefix(name.substring(0, name.length() - 5)));

			getVariantBuilder(block)
				.partialState().with(SlabBlock.TYPE, SlabType.BOTTOM).setModels(new ConfiguredModel(file))
				.partialState().with(SlabBlock.TYPE, SlabType.TOP).setModels(new ConfiguredModel(file, 180, 0, true))
				.partialState().with(SlabBlock.TYPE, SlabType.DOUBLE).setModels(new ConfiguredModel(fullFile));

		} else if (block instanceof StairsBlock) { // From botania
			ModelFile stair = models().getExistingFile(prefix(name));
			ModelFile inner = models().getExistingFile(prefix(name + "_inner"));
			ModelFile outer = models().getExistingFile(prefix(name + "_outer"));

			stairsBlock((StairsBlock) block, stair, inner, outer);
		} else if (block instanceof WallBlock) { // From botania
			ModelFile post = models().getExistingFile(prefix(name + "_post"));
			ModelFile side = models().getExistingFile(prefix(name + "_side"));

			wallBlock((WallBlock) block, post, side);
		} else if (block.getDefaultState().has(BlockStateProperties.DOUBLE_BLOCK_HALF)) {
			ModelFile upper = models().getExistingFile(prefix(name + "_upper"));
			ModelFile lower = models().getExistingFile(prefix(name + "_lower"));
			
			getVariantBuilder(block)
				.partialState().with(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER).setModels(new ConfiguredModel(upper))
				.partialState().with(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER).setModels(new ConfiguredModel(lower));
		} else if (block instanceof BlockOverloadShrine) {
			ModelFile base = models().getExistingFile(prefix(name + "_base"));
			ModelFile top = models().getExistingFile(prefix(name + "_top"));
			ModelFile side = models().getExistingFile(prefix(name + "_side"));

			getMultipartBuilder(block).part().modelFile(base).addModel().end()
				.part().modelFile(top).addModel().condition(BlockOverloadShrine.FACING, Direction.UP).end()
				.part().modelFile(side).addModel().condition(BlockOverloadShrine.FACING, Direction.NORTH).end()
				.part().modelFile(side).rotationY(90).addModel().condition(BlockOverloadShrine.FACING, Direction.EAST).end()
				.part().modelFile(side).rotationY(180).addModel().condition(BlockOverloadShrine.FACING, Direction.SOUTH).end()
				.part().modelFile(side).rotationY(270).addModel().condition(BlockOverloadShrine.FACING, Direction.WEST).end();
		} else if (block instanceof BlockElementPipe) {
			ModelFile core = models().getExistingFile(prefix(name + "_core"));
			ModelFile side = models().getExistingFile(prefix(name + "_side"));
			ModelFile extract = models().getExistingFile(prefix(name + "_extract"));

			getMultipartBuilder(block).part().modelFile(core).addModel().end()
				.part().modelFile(side).uvLock(true).addModel().condition(BlockElementPipe.NORTH, true).end()
				.part().modelFile(side).rotationY(90).uvLock(true).addModel().condition(BlockElementPipe.EAST, true).end()
				.part().modelFile(side).rotationY(180).uvLock(true).addModel().condition(BlockElementPipe.SOUTH, true).end()
				.part().modelFile(side).rotationY(270).uvLock(true).addModel().condition(BlockElementPipe.WEST, true).end()
				.part().modelFile(side).rotationX(270).uvLock(true).addModel().condition(BlockElementPipe.UP, true).end()
				.part().modelFile(side).rotationX(90).uvLock(true).addModel().condition(BlockElementPipe.DOWN, true).end()
				.part().modelFile(extract).uvLock(true).addModel().condition(BlockElementPipe.NORTH_EXTRACT, true).end()
				.part().modelFile(extract).rotationY(90).uvLock(true).addModel().condition(BlockElementPipe.EAST_EXTRACT, true).end()
				.part().modelFile(extract).rotationY(180).uvLock(true).addModel().condition(BlockElementPipe.SOUTH_EXTRACT, true).end()
				.part().modelFile(extract).rotationY(270).uvLock(true).addModel().condition(BlockElementPipe.WEST_EXTRACT, true).end()
				.part().modelFile(extract).rotationX(270).uvLock(true).addModel().condition(BlockElementPipe.UP_EXTRACT, true).end()
				.part().modelFile(extract).rotationX(90).uvLock(true).addModel().condition(BlockElementPipe.DOWN_EXTRACT, true).end();
		} else {
			simpleBlock(block, models().getExistingFile(prefix(name)));
		}
	}

	@Nonnull
	@Override
	public String getName() {
		return "ElementalCraft Blockstates";
	}

}
