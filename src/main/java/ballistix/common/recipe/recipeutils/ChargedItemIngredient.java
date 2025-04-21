package ballistix.common.recipe.recipeutils;

import java.util.Arrays;
import java.util.stream.Stream;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import ballistix.registers.BallistixRecipies;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import voltaic.api.item.IItemElectric;
import voltaic.prefab.utilities.object.TransferPack;

public class ChargedItemIngredient implements ICustomIngredient {

    public static final MapCodec<ChargedItemIngredient> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
                    //
                    Ingredient.CODEC.fieldOf("ingredient").forGetter(instance -> instance.ingredient),
                    //
                    TransferPack.CODEC.fieldOf("charge").forGetter(instance -> instance.charge),
                    //
                    Codec.BOOL.fieldOf("isStrict").forGetter(instance -> instance.isStrict)
//

            ).apply(builder, ChargedItemIngredient::new)


    );

    private final Ingredient ingredient;
    private final TransferPack charge;
    private final boolean isStrict;

    public ChargedItemIngredient(Ingredient ingredient, TransferPack charge, boolean isStrict) {
        this.ingredient = ingredient;
        this.charge = charge;
        this.isStrict = isStrict;
    }

    @Override
    public boolean test(ItemStack stack) {

        if (!ingredient.test(stack) || !(stack.getItem() instanceof IItemElectric)) {
            return false;
        }

        IItemElectric electric = (IItemElectric) stack.getItem();

        TransferPack stored = electric.extractPower(stack, Double.MAX_VALUE, true);

        if (isStrict) {

            return stored.equals(charge);

        }
	return stored.getVoltage() >= charge.getVoltage() && stored.getJoules() >= charge.getJoules();

    }

    @Override
    public Stream<ItemStack> getItems() {
        ItemStack[] items = ingredient.getItems();
        for(ItemStack stack : items) {
            IItemElectric.setEnergyStored(stack, charge.getJoules());
            IItemElectric.setMaximumCapacity(stack, charge.getJoules());
        }
        return Arrays.stream(items);
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public IngredientType<?> getType() {
        return BallistixRecipies.CHARGEDITEM_INGREDIENT_TYPE.get();
    }

    @Override
    public String toString() {
        return "items: " + Arrays.toString(ingredient.getItems()) + ", charge: " + charge.toString() + " is strict: " + isStrict;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ChargedItemIngredient other) {
            return other.isStrict == isStrict && other.ingredient.equals(ingredient) && other.charge.equals(charge);
        }
        return false;
    }
}
