package myewphi.treedetails.datagen;

import myewphi.treedetails.block.ModBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ModBlockLootTableProvider extends BlockLootSubProvider {

    private static final List<Block> unknownBlocks = List.of();

    protected ModBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        //OAK
        dropSelf(ModBlocks.OAK_BRANCH.get());
        //MOSSY OAK
        dropSelf(ModBlocks.MOSSY_OAK_WOOD.get());
        dropSelf(ModBlocks.MOSSY_OAK_LOG.get());
        dropSelf(ModBlocks.MOSSY_OAK_BRANCH.get());
        //BIRCH
        dropSelf(ModBlocks.BIRCH_BRANCH.get());
        //MOSSY BIRCH
        dropSelf(ModBlocks.MOSSY_BIRCH_WOOD.get());
        dropSelf(ModBlocks.MOSSY_BIRCH_LOG.get());
        dropSelf(ModBlocks.MOSSY_BIRCH_BRANCH.get());
        //SPRUCE
        dropSelf(ModBlocks.SPRUCE_BRANCH.get());
        //MOSSY SPRUCE
        dropSelf(ModBlocks.MOSSY_SPRUCE_WOOD.get());
        dropSelf(ModBlocks.MOSSY_SPRUCE_LOG.get());
        dropSelf(ModBlocks.MOSSY_SPRUCE_BRANCH.get());
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks(){
        Iterable<Block> allBlocks = ModBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
        ArrayList<Block> knownBlocks = new ArrayList<>();
        allBlocks.forEach(block -> {
            if(!unknownBlocks.contains(block)){
                knownBlocks.add(block);
            }
        });
        return knownBlocks;
    }
}
