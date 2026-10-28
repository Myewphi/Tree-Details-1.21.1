package myewphi.treedetails.treeplacer.trunk;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import myewphi.treedetails.TreeDetails;
import myewphi.treedetails.treeplacer.ModTrunkPlacers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
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

public class SpruceTrunkPlacer extends TrunkPlacer {
    public static final MapCodec<SpruceTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec(
            instance -> trunkPlacerParts(instance)
                    .and(IntProvider.CODEC.fieldOf("floor_clearance").forGetter(placer -> placer.floorClearance))
                    .and(IntProvider.CODEC.fieldOf("log_gap").forGetter(placer -> placer.logGap))
                    .and(BlockStateProvider.CODEC.fieldOf("branch_block").forGetter(placer -> placer.branchBlock))
                    .apply(instance, SpruceTrunkPlacer::new)
    );
    private final IntProvider floorClearance;
    private final IntProvider logGap;
    private final BlockStateProvider branchBlock;

    public SpruceTrunkPlacer(int baseHeight, int heightRandA, int heightRandB, IntProvider floorClearance, IntProvider logGap, BlockStateProvider branchBlock) {
        super(baseHeight, heightRandA, heightRandB);
        this.branchBlock = branchBlock;
        this.floorClearance = floorClearance;
        this.logGap = logGap;
    }


    @Override
    protected TrunkPlacerType<?> type() {
        return ModTrunkPlacers.SPRUCE_TRUNK_PLACER.get();
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
        setDirtAt(level, blockSetter, random, pos.below(), config);

        List<FoliagePlacer.FoliageAttachment> foliagePlacements = new java.util.ArrayList<>(List.of());

        int logGapConst = logGap.sample(random);
        int currentLogGap = 0;

        int branchGrowth = 1;
        int currentBranchGrowth = branchGrowth;

        int floorClearanceConst = floorClearance.sample(random);

        for (int i = freeTreeHeight - 1; i >= 0; i--) {
            //place main log block
            this.placeLog(level, blockSetter, random, pos.above(i), config);

            //place branches
            currentLogGap += 1;
            if(i > floorClearanceConst && currentLogGap > logGapConst){
                currentLogGap = 0;

                for(Direction dir : ImmutableList.of(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST)) {
                    placeBranch(blockSetter, random, pos.above(i).relative(dir), dir, currentBranchGrowth, foliagePlacements);
                }

                currentBranchGrowth += branchGrowth;
            }
        }

        placeCrown(random, pos.above(freeTreeHeight - 1), foliagePlacements);

        return foliagePlacements;
    }

    void placeBranch(BiConsumer<BlockPos, BlockState> blockSetter, RandomSource random, BlockPos origin, Direction dir, int length, List<FoliagePlacer.FoliageAttachment> foliagePlacements){
        //can only branch and step once, and only if the length is 3 or greater
        //when we branch or step we will set corresponding boolean to true which stops anymore branches or steps
        //which is why we also set it to true if the length is too short letting us avoid branching or stepping on awkwardly short branches
        boolean branched = length < 3;
        boolean stepped = length < 3;
        int stepVertOffset = 0;
        int stepHoriOffset = 0;
        Direction stepDir = Direction.UP;

        //place first branch block with no modifiers
        blockSetter.accept(
                origin,
                branchBlock.getState(random, origin)
                        .setValue(TreeDetails.dirToBooleanProperty(dir), true)
                        .setValue(TreeDetails.dirToBooleanProperty(dir.getOpposite()), true));
        placeAdjacentLeaves(random, origin, foliagePlacements);

        //place mid-branch blocks with potential for modifiers
        for(int i = 1; i < length - 1; i++){

            if(!stepped && (random.nextInt(4) == 0 || i == length - 2)){
                stepped = true;
                stepVertOffset = 1;
                if(random.nextInt(2) == 0){
                    stepDir = random.nextInt(2) == 0 ? dir.getClockWise() : dir.getCounterClockWise();
                    stepHoriOffset = 1;
                    branched = placeMidBranchBlock(foliagePlacements, blockSetter, random, origin.relative(dir, i), stepDir, dir.getOpposite(), branched, dir, Direction.UP);
                    branched = placeMidBranchBlock(foliagePlacements, blockSetter, random, origin.relative(dir, i).relative(stepDir), stepDir.getOpposite(), Direction.UP, branched, dir, dir.getOpposite());
                    branched = placeMidBranchBlock(foliagePlacements, blockSetter, random, origin.relative(dir, i).above().relative(stepDir), Direction.DOWN, dir, branched);
                    continue;

                }
                else {
                    branched = placeMidBranchBlock(foliagePlacements, blockSetter, random, origin.relative(dir, i), Direction.UP, dir.getOpposite(), branched);
                    branched = placeMidBranchBlock(foliagePlacements, blockSetter, random, origin.relative(dir, i).above(), Direction.DOWN, dir, branched);
                    continue;
                }
            }

            branched = placeMidBranchBlock(foliagePlacements, blockSetter, random, origin.relative(dir, i).above(stepVertOffset).relative(stepDir, stepHoriOffset), dir, dir.getOpposite(), branched);
        }

        //place last branch block with no modifiers
        blockSetter.accept(
                origin.relative(dir, length - 1).above(stepVertOffset).relative(stepDir, stepHoriOffset),
                branchBlock.getState(random, origin)
                        .setValue(TreeDetails.dirToBooleanProperty(dir.getOpposite()), true));
        placeAdjacentLeaves(random, origin.relative(dir, length - 1).above(stepVertOffset).relative(stepDir, stepHoriOffset), foliagePlacements);

        //place a leaf at the end of the branch
        foliagePlacements.add(new FoliagePlacer.FoliageAttachment(origin.relative(dir, length).above(stepVertOffset).relative(stepDir, stepHoriOffset), 0, false));
    }
    boolean placeMidBranchBlock(List<FoliagePlacer.FoliageAttachment> foliagePlacements, BiConsumer<BlockPos, BlockState> blockSetter, RandomSource random, BlockPos pos, Direction dirOne, Direction dirTwo, boolean branched, Direction ... noBranches){
        placeAdjacentLeaves(random, pos, foliagePlacements);
        if(!branched){
            if(random.nextInt(3) == 0){
                List<Direction> list = new java.util.ArrayList<>(List.of(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP));
                list.remove(dirOne);
                list.remove(dirTwo);
                for(Direction noDir : noBranches){
                    list.remove(noDir);
                }

                Direction branchDir = list.get(random.nextInt(list.size()));

                blockSetter.accept(
                        pos,
                        branchBlock.getState(random, pos)
                                .setValue(TreeDetails.dirToBooleanProperty(dirOne), true)
                                .setValue(TreeDetails.dirToBooleanProperty(dirTwo), true)
                                .setValue(TreeDetails.dirToBooleanProperty(branchDir), true));
                blockSetter.accept(
                        pos.relative(branchDir),
                        branchBlock.getState(random, pos)
                                .setValue(TreeDetails.dirToBooleanProperty(branchDir.getOpposite()), true));
                placeAdjacentLeaves(random, pos.relative(branchDir), foliagePlacements);
                return true;
            }
        }
        blockSetter.accept(
                pos,
                branchBlock.getState(random, pos)
                        .setValue(TreeDetails.dirToBooleanProperty(dirOne), true)
                        .setValue(TreeDetails.dirToBooleanProperty(dirTwo), true));

        return branched;
    }
    boolean placeMidBranchBlock(List<FoliagePlacer.FoliageAttachment> foliagePlacements, BiConsumer<BlockPos, BlockState> blockSetter, RandomSource random, BlockPos pos, Direction dirOne, Direction dirTwo, boolean branched){
        return placeMidBranchBlock(foliagePlacements, blockSetter, random, pos, dirOne, dirTwo, branched, dirTwo.getOpposite());
    }
    void placeAdjacentLeaves(RandomSource random, BlockPos pos, List<FoliagePlacer.FoliageAttachment> foliagePlacements){
        for(Direction dir : ImmutableList.of(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP)) {
            if(random.nextInt(2) == 0){
                foliagePlacements.add(new FoliagePlacer.FoliageAttachment(pos.relative(dir), 0, false));
            }
        }
    }
    void placeCrown(RandomSource random, BlockPos crown, List<FoliagePlacer.FoliageAttachment> foliagePlacements){
        for(BlockPos pos : List.of(crown.north(), crown.east(), crown.south(), crown.west(), crown.above(), crown.above(2))){
            foliagePlacements.add(new FoliagePlacer.FoliageAttachment(pos, 0, false));
            if(random.nextInt(3) == 0){
                foliagePlacements.add(new FoliagePlacer.FoliageAttachment(pos.above(), 0, false));
            }
        }
    }
}
