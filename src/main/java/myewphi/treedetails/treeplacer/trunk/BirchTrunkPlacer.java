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

public class BirchTrunkPlacer extends TrunkPlacer {
    public static final MapCodec<BirchTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec(
            instance -> trunkPlacerParts(instance)
                    .and(BlockStateProvider.CODEC.fieldOf("branch_block").forGetter(placer -> placer.branchBlock))
                    .and(BlockStateProvider.CODEC.fieldOf("root_block").forGetter(placer -> placer.rootBlock))
                    .apply(instance, BirchTrunkPlacer::new)
    );
    private final BlockStateProvider branchBlock;
    private final BlockStateProvider rootBlock;

    public BirchTrunkPlacer(int baseHeight, int heightRandA, int heightRandB, BlockStateProvider branchBlock, BlockStateProvider rootBlock) {
        super(baseHeight, heightRandA, heightRandB);
        this.branchBlock = branchBlock;
        this.rootBlock = rootBlock;
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return ModTrunkPlacers.BIRCH_TRUNK_PLACER.get();
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(
            LevelSimulatedReader level,
            BiConsumer<BlockPos, BlockState> blockSetter,
            RandomSource random,
            int freeTreeHeight,
            BlockPos pos,
            TreeConfiguration config
    ){
        List<FoliagePlacer.FoliageAttachment> foliagePlacements = new java.util.ArrayList<>(List.of());

        setDirtAt(level, blockSetter, random, pos.below(), config);

        //place trunk and small branches
        Direction currentBranchDir = Direction.UP;
        for (int i = 0; i < freeTreeHeight; i++) {
            //place trunk
            this.placeLog(level, blockSetter, random, pos.above(i), config);

            //place branches
            if(random.nextInt(2) == 0 && i > 4 && i < freeTreeHeight - 2) {
                List<Direction> list = new java.util.ArrayList<>(List.of(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST));
                list.remove(currentBranchDir);

                currentBranchDir = list.get(random.nextInt(list.size()));

                blockSetter.accept(
                        pos.above(i).relative(currentBranchDir),
                        branchBlock.getState(random, pos)
                                .setValue(TreeDetails.dirToBooleanProperty(currentBranchDir.getOpposite()), true));
            }
        }

        //place roots
        for(Direction dir : ImmutableList.of(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST)){
            if(random.nextInt(2) == 0){
                placeRoots(level, random, pos, dir, blockSetter);
            }
        }

        //place leaves
        int counter = 99;
        for(int i = freeTreeHeight; i > 6; i--){
            counter += 1;
            if(counter > 2){
                counter = 0;
                foliagePlacements.add(new FoliagePlacer.FoliageAttachment(pos.above(i), 0, false));
            }
        }

        return foliagePlacements;
    }

    protected void placeRoots(LevelSimulatedReader level, RandomSource random, BlockPos pos, Direction dir, BiConsumer<BlockPos, BlockState> blockSetter){
        BlockPos currentPos = pos.below(1).relative(dir);
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
        for(int i = 0; i < 1; i++){
            //root the dirt
            placeRootedDirt(level, random, blockSetter, currentPos);

            //should we continue placing roots?
            if(random.nextInt(i + 2) != 0){
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
