package myewphi.treedetails.datagen;

import myewphi.treedetails.block.BranchBlock;
import myewphi.treedetails.block.ModBlocks;
import myewphi.treedetails.TreeDetails;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.neoforged.neoforge.client.model.generators.*;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import org.jetbrains.annotations.NotNull;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, TreeDetails.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        //OAK
        branchBlockWithItem(ModBlocks.OAK_BRANCH.get(), Blocks.OAK_LOG);
        //MOSSY OAK
        logBlockWithItem(ModBlocks.MOSSY_OAK_WOOD.get(), ModBlocks.MOSSY_OAK_LOG.get());
        branchBlockWithItem(ModBlocks.MOSSY_OAK_BRANCH.get(), ModBlocks.MOSSY_OAK_LOG.get());

        //BIRCH
        branchBlockWithItem(ModBlocks.BIRCH_BRANCH.get(), Blocks.BIRCH_LOG);
        //MOSSY BIRCH
        logBlockWithItem(ModBlocks.MOSSY_BIRCH_WOOD.get(), ModBlocks.MOSSY_BIRCH_LOG.get());
        branchBlockWithItem(ModBlocks.MOSSY_BIRCH_BRANCH.get(), ModBlocks.MOSSY_BIRCH_LOG.get());

        //SPRUCE
        branchBlockWithItem(ModBlocks.SPRUCE_BRANCH.get(), Blocks.SPRUCE_LOG);
        //MOSSY SPRUCE
        logBlockWithItem(ModBlocks.MOSSY_SPRUCE_WOOD.get(), ModBlocks.MOSSY_SPRUCE_LOG.get());
        branchBlockWithItem(ModBlocks.MOSSY_SPRUCE_BRANCH.get(), ModBlocks.MOSSY_SPRUCE_LOG.get());

        //JUNGLE
        branchBlockWithItem(ModBlocks.JUNGLE_BRANCH.get(), Blocks.JUNGLE_LOG);
        //MOSSY JUNGLE
        logBlockWithItem(ModBlocks.MOSSY_JUNGLE_WOOD.get(), ModBlocks.MOSSY_JUNGLE_LOG.get());
        branchBlockWithItem(ModBlocks.MOSSY_JUNGLE_BRANCH.get(), ModBlocks.MOSSY_JUNGLE_LOG.get());

        //ACACIA
        branchBlockWithItem(ModBlocks.ACACIA_BRANCH.get(), Blocks.ACACIA_LOG);
        //MOSSY ACACIA
        logBlockWithItem(ModBlocks.MOSSY_ACACIA_WOOD.get(), ModBlocks.MOSSY_ACACIA_LOG.get());
        branchBlockWithItem(ModBlocks.MOSSY_ACACIA_BRANCH.get(), ModBlocks.MOSSY_ACACIA_LOG.get());

        //DARK_OAK
        branchBlockWithItem(ModBlocks.DARK_OAK_BRANCH.get(), Blocks.DARK_OAK_LOG);
        //MOSSY DARK_OAK
        logBlockWithItem(ModBlocks.MOSSY_DARK_OAK_WOOD.get(), ModBlocks.MOSSY_DARK_OAK_LOG.get());
        branchBlockWithItem(ModBlocks.MOSSY_DARK_OAK_BRANCH.get(), ModBlocks.MOSSY_DARK_OAK_LOG.get());

        //CHERRY
        branchBlockWithItem(ModBlocks.CHERRY_BRANCH.get(), Blocks.CHERRY_LOG);
        //MOSSY CHERRY
        logBlockWithItem(ModBlocks.MOSSY_CHERRY_WOOD.get(), ModBlocks.MOSSY_CHERRY_LOG.get());
        branchBlockWithItem(ModBlocks.MOSSY_CHERRY_BRANCH.get(), ModBlocks.MOSSY_CHERRY_LOG.get());

        //MANGROVE
        branchBlockWithItem(ModBlocks.MANGROVE_BRANCH.get(), Blocks.MANGROVE_LOG);
        //MOSSY MANGROVE
        logBlockWithItem(ModBlocks.MOSSY_MANGROVE_WOOD.get(), ModBlocks.MOSSY_MANGROVE_LOG.get());
        branchBlockWithItem(ModBlocks.MOSSY_MANGROVE_BRANCH.get(), ModBlocks.MOSSY_MANGROVE_LOG.get());
    }

    private void blockWithItem(DeferredBlock<?> deferredBlock){
        simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()));
    }
    private void logBlockWithItem(RotatedPillarBlock wood, RotatedPillarBlock log){
        ModelFile woodModel = models().cubeColumn(name(wood), blockTexture(log), blockTexture(log));
        axisBlock(wood, woodModel, woodModel);
        simpleBlockItem(wood, woodModel);

        ModelFile logModelHorizontal = models().cubeColumn(name(log), blockTexture(log).withSuffix("_top"), blockTexture(log));
        ModelFile logModelVertical = models().cubeColumn(name(log), blockTexture(log), blockTexture(log).withSuffix("_top"));
        axisBlock(log, logModelVertical, logModelHorizontal);
        simpleBlockItem(log, logModelHorizontal);
    }
    private void branchBlockWithItem(Block branch, Block log){
        MultiPartBlockStateBuilder multipartBuilder = getMultipartBuilder(branch);
        ModelFile branchCenter = models().withExistingParent(name(branch).concat("_center"), "treedetails:block/branch_center")
                .texture("0", blockTexture(log)).texture("particle", blockTexture(log));
        ModelFile branchSide = models().withExistingParent(name(branch).concat("_side"), "treedetails:block/branch_side")
                .texture("0", blockTexture(log)).texture("particle", blockTexture(log));
        simpleBlockItem(branch, branchCenter);

        //center none
        multipartBuilder.part().modelFile(branchCenter).rotationY(90).addModel()
                .condition(BranchBlock.UP, Boolean.FALSE)
                .condition(BranchBlock.DOWN, Boolean.FALSE)
                .condition(BranchBlock.NORTH, Boolean.FALSE)
                .condition(BranchBlock.EAST, Boolean.FALSE)
                .condition(BranchBlock.SOUTH, Boolean.FALSE)
                .condition(BranchBlock.WEST, Boolean.FALSE)
                .end();
        //center up
        multipartBuilder.part().modelFile(branchCenter).rotationX(90).addModel()
                .condition(BranchBlock.UP, Boolean.TRUE)
                .condition(BranchBlock.NORTH, Boolean.FALSE)
                .condition(BranchBlock.EAST, Boolean.FALSE)
                .condition(BranchBlock.SOUTH, Boolean.FALSE)
                .condition(BranchBlock.WEST, Boolean.FALSE)
                .end();
        //side up
        multipartBuilder.part().modelFile(branchSide).rotationX(270).addModel()
                .condition(BranchBlock.UP, Boolean.TRUE)
                .end();
        //center down
        multipartBuilder.part().modelFile(branchCenter).rotationX(90).addModel()
                .condition(BranchBlock.DOWN, Boolean.TRUE)
                .condition(BranchBlock.NORTH, Boolean.FALSE)
                .condition(BranchBlock.EAST, Boolean.FALSE)
                .condition(BranchBlock.SOUTH, Boolean.FALSE)
                .condition(BranchBlock.WEST, Boolean.FALSE)
                .end();
        //side down
        multipartBuilder.part().modelFile(branchSide).rotationX(90).addModel()
                .condition(BranchBlock.DOWN, Boolean.TRUE)
                .end();
        //center north
        multipartBuilder.part().modelFile(branchCenter).addModel()
                .condition(BranchBlock.NORTH, Boolean.TRUE)
                .end();
        //side north
        multipartBuilder.part().modelFile(branchSide).addModel()
                .condition(BranchBlock.NORTH, Boolean.TRUE)
                .end();
        //center east
        multipartBuilder.part().modelFile(branchCenter).rotationY(90).addModel()
                .condition(BranchBlock.EAST, Boolean.TRUE)
                .condition(BranchBlock.NORTH, Boolean.FALSE)
                .condition(BranchBlock.SOUTH, Boolean.FALSE)
                .end();
        //side east
        multipartBuilder.part().modelFile(branchSide).rotationY(90).addModel()
                .condition(BranchBlock.EAST, Boolean.TRUE)
                .end();
        //center south
        multipartBuilder.part().modelFile(branchCenter).addModel()
                .condition(BranchBlock.SOUTH, Boolean.TRUE)
                .end();
        //side south
        multipartBuilder.part().modelFile(branchSide).rotationY(180).addModel()
                .condition(BranchBlock.SOUTH, Boolean.TRUE)
                .end();
        //center west
        multipartBuilder.part().modelFile(branchCenter).rotationY(90).addModel()
                .condition(BranchBlock.WEST, Boolean.TRUE)
                .condition(BranchBlock.NORTH, Boolean.FALSE)
                .condition(BranchBlock.SOUTH, Boolean.FALSE)
                .end();
        //side west
        multipartBuilder.part().modelFile(branchSide).rotationY(270).addModel()
                .condition(BranchBlock.WEST, Boolean.TRUE)
                .end();
    }
    private void crossBlock(DeferredBlock<?> deferredBlock){
        simpleBlock(deferredBlock.get(), models().cross(name(deferredBlock.get()), blockTexture(deferredBlock.get())).renderType("cutout"));
    }

    private String name(Block block) {
        return key(block).getPath();
    }
    private ResourceLocation key(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    }
    public @NotNull ResourceLocation blockTexture(@NotNull Block block) {
        ResourceLocation name = key(block);
        return ResourceLocation.fromNamespaceAndPath(name.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + name.getPath());
    }
}
