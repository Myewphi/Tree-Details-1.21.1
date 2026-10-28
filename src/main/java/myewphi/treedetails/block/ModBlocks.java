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

    //JUNGLE
    public static final DeferredBlock<Block> JUNGLE_BRANCH = registerBlock("jungle_branch",
            () -> new BranchBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.JUNGLE_PLANKS).ignitedByLava()));
    //MOSSY JUNGLE
    public static final DeferredBlock<RotatedPillarBlock> MOSSY_JUNGLE_WOOD= registerBlock("mossy_jungle_wood",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.JUNGLE_WOOD).ignitedByLava()));
    public static final DeferredBlock<RotatedPillarBlock> MOSSY_JUNGLE_LOG = registerBlock("mossy_jungle_log",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.JUNGLE_LOG).ignitedByLava()));
    public static final DeferredBlock<Block> MOSSY_JUNGLE_BRANCH = registerBlock("mossy_jungle_branch",
            () -> new BranchBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.JUNGLE_PLANKS).ignitedByLava()));

    //ACACIA
    public static final DeferredBlock<Block> ACACIA_BRANCH = registerBlock("acacia_branch",
            () -> new BranchBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.ACACIA_PLANKS).ignitedByLava()));
    //MOSSY ACACIA
    public static final DeferredBlock<RotatedPillarBlock> MOSSY_ACACIA_WOOD= registerBlock("mossy_acacia_wood",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.ACACIA_WOOD).ignitedByLava()));
    public static final DeferredBlock<RotatedPillarBlock> MOSSY_ACACIA_LOG = registerBlock("mossy_acacia_log",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.ACACIA_LOG).ignitedByLava()));
    public static final DeferredBlock<Block> MOSSY_ACACIA_BRANCH = registerBlock("mossy_acacia_branch",
            () -> new BranchBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.ACACIA_PLANKS).ignitedByLava()));

    //DARK OAK
    public static final DeferredBlock<Block> DARK_OAK_BRANCH = registerBlock("dark_oak_branch",
            () -> new BranchBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DARK_OAK_PLANKS).ignitedByLava()));
    //MOSSY DARK OAK
    public static final DeferredBlock<RotatedPillarBlock> MOSSY_DARK_OAK_WOOD= registerBlock("mossy_dark_oak_wood",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DARK_OAK_WOOD).ignitedByLava()));
    public static final DeferredBlock<RotatedPillarBlock> MOSSY_DARK_OAK_LOG = registerBlock("mossy_dark_oak_log",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DARK_OAK_LOG).ignitedByLava()));
    public static final DeferredBlock<Block> MOSSY_DARK_OAK_BRANCH = registerBlock("mossy_dark_oak_branch",
            () -> new BranchBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DARK_OAK_PLANKS).ignitedByLava()));

    //CHERRY
    public static final DeferredBlock<Block> CHERRY_BRANCH = registerBlock("cherry_branch",
            () -> new BranchBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CHERRY_PLANKS).ignitedByLava()));
    //MOSSY CHERRY
    public static final DeferredBlock<RotatedPillarBlock> MOSSY_CHERRY_WOOD= registerBlock("mossy_cherry_wood",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CHERRY_WOOD).ignitedByLava()));
    public static final DeferredBlock<RotatedPillarBlock> MOSSY_CHERRY_LOG = registerBlock("mossy_cherry_log",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CHERRY_LOG).ignitedByLava()));
    public static final DeferredBlock<Block> MOSSY_CHERRY_BRANCH = registerBlock("mossy_cherry_branch",
            () -> new BranchBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CHERRY_PLANKS).ignitedByLava()));

    //MANGROVE
    public static final DeferredBlock<Block> MANGROVE_BRANCH = registerBlock("mangrove_branch",
            () -> new BranchBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MANGROVE_PLANKS).ignitedByLava()));
    //MOSSY MANGROVE
    public static final DeferredBlock<RotatedPillarBlock> MOSSY_MANGROVE_WOOD= registerBlock("mossy_mangrove_wood",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MANGROVE_WOOD).ignitedByLava()));
    public static final DeferredBlock<RotatedPillarBlock> MOSSY_MANGROVE_LOG = registerBlock("mossy_mangrove_log",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MANGROVE_LOG).ignitedByLava()));
    public static final DeferredBlock<Block> MOSSY_MANGROVE_BRANCH = registerBlock("mossy_mangrove_branch",
            () -> new BranchBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MANGROVE_PLANKS).ignitedByLava()));

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
