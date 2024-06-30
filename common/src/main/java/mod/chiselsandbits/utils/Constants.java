package mod.chiselsandbits.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import mod.chiselsandbits.core.ChiselsAndBits;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.versions.forge.ForgeVersion;

public final class Constants {

    private Constants() {
        throw new IllegalStateException("Tried to initialize: Constants but this is a Utility class.");
    }

    public static final String MOD_ID = "chiselsandbits";
    public static final String MOD_NAME = "Chisels & Bits";
    public static final String MOD_VERSION = "%VERSION%";

    public static class DataGenerator {

        public static final Gson GSON =
                new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        public static final String EN_US_LANG = "assets/" + Constants.MOD_ID + "/lang/en_us.json";
        public static final String ITEM_MODEL_DIR = "assets/" + Constants.MOD_ID + "/models/item/";
        private static final String DATAPACK_DIR = "data/" + MOD_ID + "/";
        private static final String FORGE_DATAPACK_DIR = "data/" + ForgeVersion.MOD_ID + "/";
        public static final String RECIPES_DIR = DATAPACK_DIR + "recipes/";
        public static final String TAGS_DIR = DATAPACK_DIR + "tags/";
        public static final String BLOCK_TAGS_DIR = TAGS_DIR + "blocks/";
        public static final String ITEM_TAGS_DIR = TAGS_DIR + "items/";
        public static final String FORGE_TAGS_DIR = FORGE_DATAPACK_DIR + "tags/";
        public static final String FORGE_ITEM_TAGS_DIR = FORGE_TAGS_DIR + "items/";
        public static final String LOOT_TABLES_DIR = DATAPACK_DIR + "loot_tables/blocks";
        private static final String RESOURCEPACK_DIR = "assets/" + MOD_ID + "/";
        public static final String BLOCKSTATE_DIR = RESOURCEPACK_DIR + "blockstates/";
        public static final String CONFIG_LANG_DIR = RESOURCEPACK_DIR + "lang/config/";
        public static final ResourceLocation CHISELED_BLOCK_MODEL =
                new ResourceLocation(ChiselsAndBits.MODID, "block/chiseled");
        public static final ResourceLocation CHISEL_PRINTER_MODEL =
                new ResourceLocation(ChiselsAndBits.MODID, "block/chisel_printer");
    }
}
