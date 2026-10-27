package myewphi.treedetails.block;

import myewphi.treedetails.TreeDetails;
import myewphi.treedetails.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(TreeDetails.MOD_ID);

    //OAK
    public static final DeferredBlock<Block> OAK_BRANCH = registerBlock("oak_branch",
            () -> new BranchBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).ignitedByLava()));
    //MOSSY OAK
    public static final DeferredBlock<RotatedPillarBlock> MOSSY_OAK_WOOD= registerBlock("mossy_oak_wood",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WOOD).ignitedByLava()));
    public static final DeferredBlock<RotatedPillarBlock> MOSSY_OAK_LOG = registerBlock("mossy_oak_log",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG).ignitedByLava()));
    public static final DeferredBlock<Block> MOSSY_OAK_BRANCH = registerBlock("mossy_oak_branch",
            () -> new BranchBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).ignitedByLava()));
    //BIRCH
    public static final DeferredBlock<Block> BIRCH_BRANCH = registerBlock("birch_branch",
            () -> new BranchBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BIRCH_PLANKS).ignitedByLava()));
    //MOSSY BIRCH
    public static final DeferredBlock<RotatedPillarBlock> MOSSY_BIRCH_WOOD= registerBlock("mossy_birch_wood",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BIRCH_WOOD).ignitedByLava()));
    public static final DeferredBlock<RotatedPillarBlock> MOSSY_BIRCH_LOG = registerBlock("mossy_birch_log",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BIRCH_LOG).ignitedByLava()));
    public static final DeferredBlock<Block> MOSSY_BIRCH_BRANCH = registerBlock("mossy_birch_branch",
            () -> new BranchBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BIRCH_PLANKS).ignitedByLava()));

    //SPRUCE
    public static final DeferredBlock<Block> SPRUCE_BRANCH = registerBlock("spruce_branch",
            () -> new BranchBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_PLANKS).ignitedByLava()));
    //MOSSY SPRUCE
    public static final DeferredBlock<RotatedPillarBlock> MOSSY_SPRUCE_WOOD= registerBlock("mossy_spruce_wood",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_WOOD).ignitedByLava()));
    public static final DeferredBlock<RotatedPillarBlock> MOSSY_SPRUCE_LOG = registerBlock("mossy_spruce_log",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_LOG).ignitedByLava()));
    public static final DeferredBlock<Block> MOSSY_SPRUCE_BRANCH = registerBlock("mossy_spruce_branch",
            () -> new BranchBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_PLANKS).ignitedByLava()));

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }
    private static <T extends Block> DeferredBlock<T> registerBlockWithoutItem(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        return toReturn;
    }
    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block){
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }
}
