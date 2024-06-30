package mod.chiselsandbits.data.recipe;

import com.ldtteam.datagenerators.recipes.RecipeIngredientJson;
import com.ldtteam.datagenerators.recipes.RecipeIngredientKeyJson;
import java.io.IOException;
import mod.chiselsandbits.core.ChiselsAndBits;
import mod.chiselsandbits.registry.ModItems;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.Tags;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = ChiselsAndBits.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class NetheriteBitSawRecipeGenerator extends AbstractRecipeGenerator {
    @SubscribeEvent
    public static void dataGeneratorSetup(final GatherDataEvent event) {
        event.getGenerator().addProvider(new NetheriteBitSawRecipeGenerator(event.getGenerator()));
    }

    private NetheriteBitSawRecipeGenerator(final DataGenerator generator) {
        super(generator, ModItems.ITEM_BIT_SAW_NETHERITE.get());
    }

    @Override
    protected void generate() throws IOException {
        addShapedRecipe(
                "sss",
                "stt",
                "   ",
                "s",
                new RecipeIngredientKeyJson(
                        new RecipeIngredientJson(Tags.Items.RODS_BLAZE.getName().toString(), true)),
                "t",
                new RecipeIngredientKeyJson(new RecipeIngredientJson(
                        Tags.Items.INGOTS_NETHERITE.getName().toString(), true)));
    }
}
