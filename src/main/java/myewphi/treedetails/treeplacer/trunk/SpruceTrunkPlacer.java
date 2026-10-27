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

public class SpruceTrunkPlacer extends TrunkPlacer {
    public static final MapCodec<SpruceTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec(
            instance -> trunkPlacerParts(instance)
                    .and(BlockStateProvider.CODEC.fieldOf("branch_block").forGetter(placer -> placer.branchBlock))
                    .and(BlockStateProvider.CODEC.fieldOf("root_block").forGetter(placer -> placer.rootBlock))
                    .apply(instance, SpruceTrunkPlacer::new)
    );
    private final BlockStateProvider branchBlock;
    private final BlockStateProvider rootBlock;

    public SpruceTrunkPlacer(int baseHeight, int heightRandA, int heightRandB, BlockStateProvider branchBlock, BlockStateProvider rootBlock) {
        super(baseHeight, heightRandA, heightRandB);
        this.branchBlock = branchBlock;
        this.rootBlock = rootBlock;
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
        List<FoliagePlacer.FoliageAttachment> foliagePlacements = new java.util.ArrayList<>(List.of());

        int logGap = random.nextInt(4) == 0 ? 3 : 2;
        int currentLogGap = 0;

        int branchGrowth = 1;
        int currentBranchGrowth = branchGrowth;

        for (int i = freeTreeHeight - 1; i >= 0; i--) {
            //place main log block
            this.placeLog(level, blockSetter, random, pos.above(i), config);

            //place branches
            currentLogGap += 1;
            if(i > 5 && currentLogGap > logGap){
                currentLogGap = 0;

                for(Direction dir : ImmutableList.of(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST)) {
                    placeBranch(blockSetter, random, pos.above(i).relative(dir), dir, currentBranchGrowth, foliagePlacements);
                }

                currentBranchGrowth += branchGrowth;
            }
        }

        //place roots
        for(Direction dir : ImmutableList.of(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST)){
            if(random.nextInt(4) != 0){
                placeRoots(level, random, pos, dir, blockSetter);
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

    protected void placeRoots(LevelSimulatedReader level, RandomSource random, BlockPos pos, Direction dir, BiConsumer<BlockPos, BlockState> blockSetter){
        BlockPos currentPos = pos.below(random.nextInt(2)).relative(dir);
        Direction currentDir = dir;

        //place first root block and branch on top and root the dirt
        blockSetter.accept(
                currentPos,
                rootBlock.getState(random, currentPos)
                        .setValue(RotatedPillarBlock.AXIS, currentDir.getAxis()));
        blockSetter.accept(
                currentPos.above(),
                branchBlock.getState(random, currentPos)
                        .setValue(TreeDetails.dirToBooleanProperty(currentDir.getOpposite()), true)
                        .setValue(TreeDetails.dirToBooleanProperty(Direction.DOWN), true));
        placeRootedDirt(level, random, blockSetter, currentPos);

        //use loop to creep our roots ever onward
        for(int i = 0; i < 4; i++){
            //root the dirt
            placeRootedDirt(level, random, blockSetter, currentPos);

            //should we continue placing roots? chance decreases with each loop
            if(random.nextInt(i + 1) != 0){
                break;
            }

            //place downwards if air is below
            if(level.isStateAtPosition(currentPos.below(), state -> state.isAir() || state.getBlock().equals(Blocks.WATER))){
                currentPos = currentPos.below();
                blockSetter.accept(
                        currentPos,
                        rootBlock.getState(random, currentPos)
                                .setValue(RotatedPillarBlock.AXIS, currentDir.getAxis()));
                continue;
            }

            //find out if there is an adjacent spot where a root can go down
            Optional<Direction> nearbySlope = getNearbySlopes(level, random, currentPos);

            //if flat ground, grow forwards
            if(nearbySlope.isEmpty()){
                currentPos = currentPos.relative(currentDir);
                blockSetter.accept(
                        currentPos,
                        rootBlock.getState(random, currentPos)
                                .setValue(RotatedPillarBlock.AXIS, currentDir.getAxis()));
                continue;
            }

            //if sloped ground, grow downwards
            currentDir = nearbySlope.get();
            currentPos = currentPos.relative(currentDir).below();
            blockSetter.accept(
                    currentPos,
                    rootBlock.getState(random, currentPos)
                            .setValue(RotatedPillarBlock.AXIS, currentDir.getAxis()));
            blockSetter.accept(
                    currentPos.above(),
                    branchBlock.getState(random, currentPos)
                            .setValue(TreeDetails.dirToBooleanProperty(currentDir.getOpposite()), true)
                            .setValue(TreeDetails.dirToBooleanProperty(Direction.DOWN), true));
        }
    }
    protected Optional<Direction> getNearbySlopes(LevelSimulatedReader level, RandomSource random, BlockPos pos){
        List<Direction> list = new java.util.ArrayList<>(List.of());
        for(Direction dir : List.of(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST)){
            if(level.isStateAtPosition(pos.relative(dir), state -> state.isAir() || state.getBlock().equals(Blocks.WATER))){
                list.add(dir);
            }
        }
        if(list.size() > 0){
            return Optional.of(list.get(random.nextInt(list.size())));
        } else {
            return Optional.empty();
        }
    }
    protected void placeRootedDirt(LevelSimulatedReader level, RandomSource random, BiConsumer<BlockPos, BlockState> blockSetter, BlockPos pos){
        for(Direction dir : Direction.values()){
            if(level.isStateAtPosition(pos.relative(dir), state -> state.getBlock().equals(Blocks.DIRT) || state.getBlock().equals(Blocks.GRASS_BLOCK))){
                if(random.nextInt(2) == 0){
                    blockSetter.accept(pos.relative(dir), Blocks.ROOTED_DIRT.defaultBlockState());
                }
            }
        }
    }
}
