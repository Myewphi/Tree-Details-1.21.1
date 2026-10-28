package myewphi.treedetails;

import myewphi.treedetails.block.ModBlocks;
import myewphi.treedetails.item.ModItems;
import myewphi.treedetails.treeplacer.ModFoliagePlacers;
import myewphi.treedetails.treeplacer.ModTreeDecorators;
import myewphi.treedetails.treeplacer.ModTrunkPlacers;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;

@Mod(TreeDetails.MOD_ID)
public class TreeDetails {
    public static final String MOD_ID = "treedetails";
    public static final Logger LOGGER = LogUtils.getLogger();

    public TreeDetails(IEventBus modEventBus, ModContainer modContainer) {
        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModTrunkPlacers.register(modEventBus);
        ModFoliagePlacers.register(modEventBus);
        ModTreeDecorators.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
    }

    //CREATIVE TAB
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CREATIVE_TAB = CREATIVE_MODE_TABS.register("creative_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.tab"))
            .icon(Items.OAK_SAPLING::getDefaultInstance)
            .displayItems((parameters, output) -> {

                output.accept(Blocks.OAK_WOOD);
                output.accept(Blocks.OAK_LOG);
                output.accept(ModBlocks.OAK_BRANCH);
                output.accept(ModBlocks.MOSSY_OAK_WOOD);
                output.accept(ModBlocks.MOSSY_OAK_LOG);
                output.accept(ModBlocks.MOSSY_OAK_BRANCH);

                output.accept(Blocks.BIRCH_WOOD);
                output.accept(Blocks.BIRCH_LOG);
                output.accept(ModBlocks.BIRCH_BRANCH);
                output.accept(ModBlocks.MOSSY_BIRCH_WOOD);
                output.accept(ModBlocks.MOSSY_BIRCH_LOG);
                output.accept(ModBlocks.MOSSY_BIRCH_BRANCH);

                output.accept(Blocks.SPRUCE_WOOD);
                output.accept(Blocks.SPRUCE_LOG);
                output.accept(ModBlocks.SPRUCE_BRANCH);
                output.accept(ModBlocks.MOSSY_SPRUCE_WOOD);
                output.accept(ModBlocks.MOSSY_SPRUCE_LOG);
                output.accept(ModBlocks.MOSSY_SPRUCE_BRANCH);

                output.accept(Blocks.JUNGLE_WOOD);
                output.accept(Blocks.JUNGLE_LOG);
                output.accept(ModBlocks.JUNGLE_BRANCH);
                output.accept(ModBlocks.MOSSY_JUNGLE_WOOD);
                output.accept(ModBlocks.MOSSY_JUNGLE_LOG);
                output.accept(ModBlocks.MOSSY_JUNGLE_BRANCH);

                output.accept(Blocks.ACACIA_WOOD);
                output.accept(Blocks.ACACIA_LOG);
                output.accept(ModBlocks.ACACIA_BRANCH);
                output.accept(ModBlocks.MOSSY_ACACIA_WOOD);
                output.accept(ModBlocks.MOSSY_ACACIA_LOG);
                output.accept(ModBlocks.MOSSY_ACACIA_BRANCH);

                output.accept(Blocks.DARK_OAK_WOOD);
                output.accept(Blocks.DARK_OAK_LOG);
                output.accept(ModBlocks.DARK_OAK_BRANCH);
                output.accept(ModBlocks.MOSSY_DARK_OAK_WOOD);
                output.accept(ModBlocks.MOSSY_DARK_OAK_LOG);
                output.accept(ModBlocks.MOSSY_DARK_OAK_BRANCH);

                output.accept(Blocks.CHERRY_WOOD);
                output.accept(Blocks.CHERRY_LOG);
                output.accept(ModBlocks.CHERRY_BRANCH);
                output.accept(ModBlocks.MOSSY_CHERRY_WOOD);
                output.accept(ModBlocks.MOSSY_CHERRY_LOG);
                output.accept(ModBlocks.MOSSY_CHERRY_BRANCH);

                output.accept(Blocks.MANGROVE_WOOD);
                output.accept(Blocks.MANGROVE_LOG);
                output.accept(ModBlocks.MANGROVE_BRANCH);
                output.accept(ModBlocks.MOSSY_MANGROVE_WOOD);
                output.accept(ModBlocks.MOSSY_MANGROVE_LOG);
                output.accept(ModBlocks.MOSSY_MANGROVE_BRANCH);
            }).build());

    //Util
    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty WEST = BooleanProperty.create("west");
    public static final BooleanProperty UP = BooleanProperty.create("up");
    public static final BooleanProperty DOWN = BooleanProperty.create("down");

    public static BooleanProperty dirToBooleanProperty(Direction dir){
        switch (dir){
            case NORTH -> { return NORTH; }
            case EAST -> { return EAST; }
            case SOUTH -> { return SOUTH; }
            case WEST -> { return WEST; }
            case UP -> { return UP; }
            case DOWN -> { return DOWN; }
            default -> { throw new RuntimeException("dirToBooleanProperty did not recieve a valid direction somehow!"); }
        }
    }
}
