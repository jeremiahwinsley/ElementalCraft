package sirttas.elementalcraft.world.feature.structure;

import java.util.Random;

import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.structure.TemplateStructurePiece;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.TemplateManager;
import sirttas.elementalcraft.ElementType;
import sirttas.elementalcraft.ElementalCraft;
import sirttas.elementalcraft.block.ECBlocks;
import sirttas.elementalcraft.property.ECProperties;

public class SourceAltarStructure extends Structure<NoFeatureConfig> {

	public static final String NAME = "source_altar";

	private static final ResourceLocation SMALL = ElementalCraft.createRL("altar/small");
	private static final ResourceLocation MEDIUM = ElementalCraft.createRL("altar/medium");

	public SourceAltarStructure() {
		super(NoFeatureConfig.field_236558_a_);
	}

	@Override
	public GenerationStage.Decoration getDecorationStage() {
		return GenerationStage.Decoration.SURFACE_STRUCTURES;
	}

	@Override
	public IStartFactory<NoFeatureConfig> getStartFactory() {
		return Start::new;
	}

	@Override
	public String getStructureName() {
		return ElementalCraft.MODID + ":" + NAME;
	}

	public static class Start extends StructureStart<NoFeatureConfig> {

		public Start(Structure<NoFeatureConfig> structure, int x, int y, MutableBoundingBox mutableBoundingBox, int k, long l) {
			super(structure, x, y, mutableBoundingBox, k, l);
		}

		@Override
		public void func_230364_a_/* init */(DynamicRegistries dynamicRegistries, ChunkGenerator generator, TemplateManager templateManagerIn, int chunkX, int chunkZ, Biome biomeIn,
				NoFeatureConfig config) {
			this.components.add(new Piece(templateManagerIn, this.rand.nextInt(8) < 6 ? SMALL : MEDIUM, ElementType.random(rand), new BlockPos(chunkX * 16, 0, chunkZ * 16)));
			this.recalculateStructureSize();

		}
	}

	public static class Piece extends TemplateStructurePiece {

		private ResourceLocation templateName;
		private ElementType elementType;

		public Piece(TemplateManager templateManager, ResourceLocation templateName, ElementType elementType, BlockPos pos) {
			super(ECStructures.SOURCE_ALTAR_PIECE_TYPE, 0);
			this.templatePosition = pos;
			this.templateName = templateName;
			this.elementType = elementType;
			initTemplate(templateManager);
		}

		public Piece(TemplateManager templateManager, CompoundNBT nbt) {
			super(ECStructures.SOURCE_ALTAR_PIECE_TYPE, nbt);
			this.templateName = new ResourceLocation(nbt.getString("Template"));
			this.elementType = ElementType.byName(nbt.getString("ElementType"));
			initTemplate(templateManager);
		}

		private void initTemplate(TemplateManager templateManager) {
			this.setup(templateManager.getTemplateDefaulted(templateName), this.templatePosition,
					new PlacementSettings().setMirror(Mirror.NONE).setCenterOffset(new BlockPos(1, 0, 1)).addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_BLOCK));
		}

		@Override
		protected void readAdditional(CompoundNBT tagCompound) {
			super.readAdditional(tagCompound);
			tagCompound.putString("Template", this.templateName.toString());
			tagCompound.putString("ElementType", this.elementType.getString());
		}


		@Override
		public boolean func_230383_a_/* create */(ISeedReader worldIn, StructureManager structureManager, ChunkGenerator chunkGeneratorIn, Random randomIn, MutableBoundingBox mutableBoundingBoxIn,
				ChunkPos chunkPosIn, BlockPos pos) {
			this.templatePosition = this.templatePosition.add(0, worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, this.templatePosition.getX(), this.templatePosition.getZ()) - 1, 0);
			return super.func_230383_a_/* create */(worldIn, structureManager, chunkGeneratorIn, randomIn, mutableBoundingBoxIn, chunkPosIn, pos);

		}

		@Override
		protected void handleDataMarker(String function, BlockPos pos, IServerWorld worldIn, Random rand, MutableBoundingBox sbb) {
			if ("chest".equals(function)) {
				this.generateChest(worldIn, sbb, rand, pos, ElementalCraft.createRL("chests/altar/small_" + elementType.getString()), null);
				worldIn.func_230547_a_/* notifyNeighbors */(pos, Blocks.CHEST);
			} else if ("source".equals(function)) {
				worldIn.setBlockState(pos, ECBlocks.source.getDefaultState().with(ECProperties.ELEMENT_TYPE, elementType), 3);
			}

		}
	}

}
