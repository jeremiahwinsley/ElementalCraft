package sirttas.elementalcraft.block.tank;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;
import sirttas.elementalcraft.ElementalCraft;
import sirttas.elementalcraft.api.element.ElementType;
import sirttas.elementalcraft.block.tile.TileEC;
import sirttas.elementalcraft.config.ECConfig;
import sirttas.elementalcraft.nbt.ECNames;

public class TileTank extends TileEC implements ITank {

	@ObjectHolder(ElementalCraft.MODID + ":" + BlockTank.NAME) public static TileEntityType<TileTank> TYPE;

	private int elementAmount = 0;
	private int elementCapacity = 100000;
	private ElementType elementType = ElementType.NONE;
	private boolean small = false;

	public TileTank(int elementAmountMax) {
		super(TYPE);
		this.elementCapacity = elementAmountMax;
	}

	public TileTank(boolean small) {
		this(small ? ECConfig.COMMON.tankSmallCapacity.get() : ECConfig.COMMON.tankCapacity.get());
		this.setSmall(small);
	}

	public TileTank() {
		this(false);
	}

	@Override
	public int inserElement(int count, ElementType type, boolean simulate) {
		if (type != this.elementType && this.elementType != ElementType.NONE) {
			return count - this.extractElement(count, this.elementType, simulate);
		} else {
			int newCount = Math.min(elementAmount + count, elementCapacity);
			int ret = count - newCount + elementAmount;

			if (!simulate) {
				elementAmount = newCount;
				if (this.elementType == ElementType.NONE) {
					this.elementType = type;
				}
			}
			this.markDirty();
			return ret;
		}
	}

	@Override
	public int extractElement(int count, ElementType type, boolean simulate) {
		if (type != this.elementType) {
			return 0;
		}
		int newCount = Math.max(elementAmount - count, 0);
		int ret = elementAmount - newCount;

		if (!simulate) {
			elementAmount = newCount;
			if (this.elementAmount <= 0) {
				this.elementType = ElementType.NONE;
			}
		}
		this.markDirty();
		return ret;
	}

	@Override
	public ElementType getElementType() {
		return elementType;
	}

	@Override
	public int getElementAmount() {
		return elementAmount;
	}

	@Override
	public int getElementCapacity() {
		return elementCapacity;
	}

	public boolean isSmall() {
		return small;
	}

	public void setSmall(boolean small) {
		this.small = small;
	}

	@Override
	public boolean doesRenderGauge() {
		return true;
	}

	@Override
	public void read(BlockState state, CompoundNBT compound) {
		super.read(state, compound);
		elementType = ElementType.byName(compound.getString(ECNames.ELEMENT_TYPE));
		elementAmount = compound.getInt(ECNames.ELEMENT_AMOUNT);
		if (compound.contains(ECNames.ELEMENT_CAPACITY)) { // TODO 1.17 remove if
			elementCapacity = compound.getInt(ECNames.ELEMENT_CAPACITY);
		}
		small = compound.getBoolean(ECNames.SMALL);
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		super.write(compound);
		compound.putString(ECNames.ELEMENT_TYPE, elementType.getString());
		compound.putInt(ECNames.ELEMENT_AMOUNT, elementAmount);
		if (elementCapacity > 0) { // TODO 1.17 remove if
			compound.putInt(ECNames.ELEMENT_CAPACITY, elementCapacity);
		}
		compound.putBoolean(ECNames.SMALL, small);
		return compound;
	}

}
