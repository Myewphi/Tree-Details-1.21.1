package myewphi.treedetails.datagen;

import myewphi.treedetails.TreeDetails;
import myewphi.treedetails.block.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, TreeDetails.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(BlockTags.MINEABLE_WITH_AXE)
                //OAK
                .add(ModBlocks.OAK_BRANCH.getKey())
                //MOSSY OAK
                .add(ModBlocks.MOSSY_OAK_WOOD.getKey())
                .add(ModBlocks.MOSSY_OAK_LOG.getKey())
                .add(ModBlocks.MOSSY_OAK_BRANCH.getKey());
        this.tag(BlockTags.LOGS)
                //OAK
                .add(ModBlocks.OAK_BRANCH.getKey())
                //MOSSY OAK
                .add(ModBlocks.MOSSY_OAK_WOOD.getKey())
                .add(ModBlocks.MOSSY_OAK_LOG.getKey())
                .add(ModBlocks.MOSSY_OAK_BRANCH.getKey());
    }
}
