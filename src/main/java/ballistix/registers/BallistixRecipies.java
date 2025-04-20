package ballistix.registers;

import ballistix.Ballistix;
import ballistix.common.recipe.recipeutils.ChargedItemIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class BallistixRecipies {

    public static final DeferredRegister<IngredientType<?>> INGREDIENT_TYPES = DeferredRegister.create(NeoForgeRegistries.INGREDIENT_TYPES, Ballistix.ID);

    public static final DeferredHolder<IngredientType<?>, IngredientType<ChargedItemIngredient>> CHARGEDITEM_INGREDIENT_TYPE = INGREDIENT_TYPES.register("chargeditemingredient", () -> new IngredientType<>(ChargedItemIngredient.CODEC));


}
