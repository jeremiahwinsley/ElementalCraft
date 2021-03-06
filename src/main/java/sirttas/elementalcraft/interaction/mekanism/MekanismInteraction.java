package sirttas.elementalcraft.interaction.mekanism;

import mekanism.api.chemical.gas.GasStack;
import mekanism.api.recipes.ChemicalDissolutionRecipe;
import mekanism.api.recipes.ItemStackGasToItemStackRecipe;
import mekanism.api.recipes.ItemStackToItemStackRecipe;
import mekanism.api.recipes.inputs.ItemStackIngredient;
import mekanism.api.recipes.inputs.chemical.GasStackIngredient;
import mekanism.common.recipe.MekanismRecipeType;
import mekanism.common.recipe.impl.ChemicalDissolutionIRecipe;
import mekanism.common.recipe.impl.CrushingIRecipe;
import mekanism.common.recipe.impl.EnrichingIRecipe;
import mekanism.common.recipe.impl.InjectingIRecipe;
import mekanism.common.recipe.impl.PurifyingIRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModList;
import sirttas.elementalcraft.ElementalCraft;
import sirttas.elementalcraft.api.pureore.PureOreInjectorIMCMessage;
import sirttas.elementalcraft.config.ECConfig;
import sirttas.elementalcraft.item.pureore.PureOreRecipeInjector;

public class MekanismInteraction {

	public static void setup() {
		if (isActive()) {
			if (Boolean.TRUE.equals(ECConfig.COMMON.mekanismPureOreDissolutionRecipe.get())) {
				ElementalCraft.PURE_ORE_MANAGER.addInjector(PureOreRecipeInjector.create(MekanismRecipeType.DISSOLUTION, MekanismInteraction::buildDissolutionRecipe)
						.filter((recipe, stack) -> recipe.getItemInput().test(stack)));
			}
			if (Boolean.TRUE.equals(ECConfig.COMMON.mekanismPureOreInjectingRecipe.get())) {
				addInjector(MekanismRecipeType.INJECTING, InjectingIRecipe::new);
			}
			if (Boolean.TRUE.equals(ECConfig.COMMON.mekanismPureOrePurifyingRecipe.get())) {
				addInjector(MekanismRecipeType.PURIFYING, PurifyingIRecipe::new);
			}
			if (Boolean.TRUE.equals(ECConfig.COMMON.mekanismPureOreEnrichingRecipe.get())) {
				addInjector(MekanismRecipeType.ENRICHING, EnrichingIRecipe::new);
			}
			if (Boolean.TRUE.equals(ECConfig.COMMON.mekanismPureOreCrushingRecipe.get())) {
				addInjector(MekanismRecipeType.CRUSHING, CrushingIRecipe::new);
			}
		}
	}
	
	private static ChemicalDissolutionRecipe buildDissolutionRecipe(ChemicalDissolutionRecipe sourceRecipe, Ingredient ingredient) {
		return new ChemicalDissolutionIRecipe(ElementalCraft.createRL(PureOreInjectorIMCMessage.buildRecipeId(sourceRecipe.getId())), ItemStackIngredient.from(ingredient),
				sourceRecipe.getGasInput(), sourceRecipe.getOutput(ItemStack.EMPTY, GasStack.EMPTY).getChemicalStack());
	}

	private static void addInjector(MekanismRecipeType<ItemStackGasToItemStackRecipe> recipeType, ItemStackGasToItemStackRecipeFactory factory) {
		ElementalCraft.PURE_ORE_MANAGER.addInjector(PureOreRecipeInjector.create(recipeType,
				(sourceRecipe, ingredient) -> factory.create(ElementalCraft.createRL(PureOreInjectorIMCMessage.buildRecipeId(sourceRecipe.getId())),
						ItemStackIngredient.from(ingredient), sourceRecipe.getChemicalInput(), sourceRecipe.getOutput(ItemStack.EMPTY, GasStack.EMPTY)))
				.filter((recipe, stack) -> recipe.getItemInput().test(stack)));
	}
	
	private static void addInjector(MekanismRecipeType<ItemStackToItemStackRecipe> recipeType, ItemStackToItemStackRecipeFactory factory) {
		ElementalCraft.PURE_ORE_MANAGER.addInjector(PureOreRecipeInjector.create(recipeType, 
				(sourceRecipe, ingredient) -> factory.create(ElementalCraft.createRL(PureOreInjectorIMCMessage.buildRecipeId(sourceRecipe.getId())), ItemStackIngredient.from(ingredient),
						sourceRecipe.getOutput(ItemStack.EMPTY))).filter((recipe, stack) -> recipe.test(stack)));
	}

	private static boolean isActive() {
		return ModList.get().isLoaded("mekanism") && Boolean.TRUE.equals(ECConfig.COMMON.mekanismInteracionEnabled.get());
	}

	private static interface ItemStackToItemStackRecipeFactory {
		ItemStackToItemStackRecipe create(ResourceLocation id, ItemStackIngredient input, ItemStack output);
	}
	
	private static interface ItemStackGasToItemStackRecipeFactory {
		ItemStackGasToItemStackRecipe create(ResourceLocation id, ItemStackIngredient itemInput, GasStackIngredient gasInput, ItemStack output);
	}
	
}
