package myewphi.treedetails.treeplacer.trunk;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import myewphi.treedetails.TreeDetails;
import myewphi.treedetails.treeplacer.ModTrunkPlacers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

public class OakTrunkPlacer extends TrunkPlacer {
    public static final MapCodec<OakTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec(
            instance -> trunkPlacerParts(instance)
                    .and(BlockStateProvider.CODEC.fieldOf("branch_block").forGetter(placer -> placer.branchBlock))
                    .apply(instance, OakTrunkPlacer::new)
    );
    private final BlockStateProvider branchBlock;

    public OakTrunkPlacer(int baseHeight, int heightRandA, int heightRandB, BlockStateProvider branchBlock) {
        super(baseHeight, heightRandA, heightRandB);
        this.branchBlock = branchBlock;
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return ModTrunkPlacers.OAK_TRUNK_PLACER.get();
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(
            LevelSimulatedReader level,
            BiConsumer<BlockPos, BlockState> blockSetter,
            RandomSource random,
            int freeTreeHeight,
            BlockPos pos,
            TreeConfiguration config
    ) {
        List<FoliagePlacer.FoliageAttachment> foliagePlacements = new java.util.ArrayList<>(List.of());

        setDirtAt(level, blockSetter, random, pos.below(), config);

        //place trunk and small branches
        Direction currentBranchDir = Direction.UP;
        for (int i = 0; i < freeTreeHeight; i++) {

            //place trunk block
            this.placeLog(level, blockSetter, random, pos.above(i), config);

            //place small branches
            if(random.nextInt(4) == 0 && i >= 2){
                List<Direction> list = new java.util.ArrayList<>(List.of(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST));
                list.remove(currentBranchDir);

                currentBranchDir = list.get(random.nextInt(list.size()));
                //small chance to have 2 block branch, higher chance for 1 blockbranch
                if(random.nextInt(8) == 0){
                    blockSetter.accept(
                            pos.above(i).relative(currentBranchDir),
                            branchBlock.getState(random, pos)
                                    .setValue(TreeDetails.dirToBooleanProperty(currentBranchDir), true)
                                    .setValue(TreeDetails.dirToBooleanProperty(currentBranchDir.getOpposite()), true));
                    blockSetter.accept(
                            pos.above(i).relative(currentBranchDir, 2),
                            branchBlock.getState(random, pos)
                                    .setValue(TreeDetails.dirToBooleanProperty(currentBranchDir.getOpposite()), true));
                } else {
                    blockSetter.accept(
                            pos.above(i).relative(currentBranchDir),
                            branchBlock.getState(random, pos)
                                    .setValue(TreeDetails.dirToBooleanProperty(currentBranchDir.getOpposite()), true));
                }
            }
        }

        //place main branches
        int currentBranchHeight = random.nextIntBetweenInclusive(freeTreeHeight - 5, freeTreeHeight - 3);
        for(Direction dir : ImmutableList.of(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST)){
            foliagePlacements.addLast(placeBranch(pos.above(currentBranchHeight), dir, blockSetter, random));
            currentBranchHeight = randomStep(random, currentBranchHeight, freeTreeHeight - 5, freeTreeHeight - 3);
        }

        //place foliage
        foliagePlacements.addLast(new FoliagePlacer.FoliageAttachment(pos.above(freeTreeHeight), 0, false));
        return foliagePlacements;
    }

    protected FoliagePlacer.FoliageAttachment placeBranch(BlockPos pos, Direction dir, BiConsumer<BlockPos, BlockState> blockSetter, RandomSource random){
        int branchLength = random.nextIntBetweenInclusive(2, 3);
        int branchHeight = random.nextIntBetweenInclusive(1, 2);

        for(int i = 0; i < branchLength; i++){
            if(i < branchLength - 1){
                placeBranchBlock(pos.relative(dir, i + 1), dir, dir.getOpposite(), blockSetter, random);
            }
            else {
                placeBranchBlock(pos.relative(dir, i + 1), Direction.UP, dir.getOpposite(), blockSetter, random);
            }
        }
        for(int i = 0; i < branchHeight; i++){
            if(i < branchHeight - 1){
                placeBranchBlock(pos.relative(dir, branchLength).above(i + 1), Direction.UP, Direction.DOWN, blockSetter, random);
            }
            else {
                placeBranchBlock(pos.relative(dir, branchLength).above(i + 1), Direction.DOWN, Direction.DOWN, blockSetter, random);
            }
        }

        return new FoliagePlacer.FoliageAttachment(pos.relative(dir, branchLength).above(branchHeight + 1), 0, false);
    }
    protected void placeBranchBlock(BlockPos pos, Direction dirOne, Direction dirTwo, BiConsumer<BlockPos, BlockState> blockSetter, RandomSource random){
        //setup for small branch stuff
        List<Direction> list = new java.util.ArrayList<>(List.of(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP, Direction.DOWN));
        list.remove(dirOne);
        list.remove(dirTwo);

        //small chance to place a small branch off the main branch is calculated before placing main branch in case main branch shape needs to change
        if(random.nextInt(10) == 0){
            Direction smallBranchDir = list.get(random.nextInt(list.size()));
            //small branch
            blockSetter.accept(
                    pos.relative(smallBranchDir),
                    branchBlock.getState(random, pos)
                            .setValue(TreeDetails.dirToBooleanProperty(smallBranchDir.getOpposite()), true));
            //main branch
            blockSetter.accept(
                    pos,
                    branchBlock.getState(random, pos)
                            .setValue(TreeDetails.dirToBooleanProperty(dirOne), true)
                            .setValue(TreeDetails.dirToBooleanProperty(dirTwo), true)
                            .setValue(TreeDetails.dirToBooleanProperty(smallBranchDir), true));
        }
        //if a small offshoot branch is not being placed, just place main branch normally
        else {
            blockSetter.accept(
                    pos,
                    branchBlock.getState(random, pos)
                            .setValue(TreeDetails.dirToBooleanProperty(dirOne), true)
                            .setValue(TreeDetails.dirToBooleanProperty(dirTwo), true));
        }
    }
    protected int randomStep(RandomSource random, int current, int min, int max){
        if(current == max){
            return current - 1;
        }
        if(current == min){
            return current + 1;
        }

        if(random.nextIntBetweenInclusive(1, 2) == 1){
            return current + 1;
        }
        return current - 1;
    }
}
