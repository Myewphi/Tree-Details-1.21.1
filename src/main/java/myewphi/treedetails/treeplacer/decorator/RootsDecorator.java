package myewphi.treedetails.treeplacer.decorator;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import myewphi.treedetails.TreeDetails;
import myewphi.treedetails.treeplacer.ModTreeDecorators;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

import java.util.List;
import java.util.Optional;

public class RootsDecorator extends TreeDecorator {
    public static final MapCodec<RootsDecorator> CODEC = RecordCodecBuilder.mapCodec(
            (instance) -> instance.group(
                    IntProvider.codec(1, 16).fieldOf("roots_length").forGetter((decorator) -> decorator.rootsLength),
                    IntProvider.codec(0, 4).fieldOf("roots_count").forGetter((decorator) -> decorator.rootsCount),
                    BlockStateProvider.CODEC.fieldOf("branch_block").forGetter((decorator) -> decorator.branchBlock),
                    BlockStateProvider.CODEC.fieldOf("root_block").forGetter((decorator) -> decorator.rootBlock))
                    .apply(instance, RootsDecorator::new));

    private final IntProvider rootsLength;
    private final IntProvider rootsCount;
    private final BlockStateProvider branchBlock;
    private final BlockStateProvider rootBlock;

    public RootsDecorator(IntProvider rootsLength, IntProvider rootsCount, BlockStateProvider branchBlock, BlockStateProvider rootBlock) {
        this.rootsLength = rootsLength;
        this.rootsCount = rootsCount;
        this.branchBlock = branchBlock;
        this.rootBlock = rootBlock;
    }

    @Override
    protected TreeDecoratorType<?> type() {
        return ModTreeDecorators.ROOTS_DECORATOR.get();
    }

    @Override
    public void place(Context context) {
        int rootsCountConst = rootsCount.sample(context.random());
        List<Direction> list = new java.util.ArrayList<>(List.of(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST));

        for(int i = 0; i < rootsCountConst; i++){
            Direction dir = list.get(context.random().nextInt(list.size()));
            list.remove(dir);
            placeRoots(context.level(), context.random(), context.logs().getFirst().above(), dir, context, rootsLength.sample(context.random()));
        }
    }

    protected void placeRoots(LevelSimulatedReader level, RandomSource random, BlockPos pos, Direction dir, Context context, int length){
        BlockPos currentPos = pos.below(random.nextInt(2)).relative(dir);
        Direction currentDir = dir;

        //place first root block and branch on top and root the dirt
        context.setBlock(
                currentPos,
                rootBlock.getState(random, currentPos)
                        .setValue(RotatedPillarBlock.AXIS, currentDir.getAxis()));
        context.setBlock(
                currentPos.above(),
                branchBlock.getState(random, currentPos)
                        .setValue(TreeDetails.dirToBooleanProperty(currentDir.getOpposite()), true)
                        .setValue(TreeDetails.dirToBooleanProperty(Direction.DOWN), true));

        placeRootedDirt(level, random, currentPos, context);

        //use loop to creep our roots ever onward
        for(int i = 0; i < length - 1; i++){
            //root the dirt
            placeRootedDirt(level, random, currentPos, context);

            //place downwards if air is below
            if(level.isStateAtPosition(currentPos.below(), state -> state.isAir() || state.getBlock().equals(Blocks.WATER))){
                currentPos = currentPos.below();
                context.setBlock(
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
                context.setBlock(
                        currentPos,
                        rootBlock.getState(random, currentPos)
                                .setValue(RotatedPillarBlock.AXIS, currentDir.getAxis()));
                continue;
            }

            //if sloped ground, grow downwards
            currentDir = nearbySlope.get();
            currentPos = currentPos.relative(currentDir).below();
            context.setBlock(
                    currentPos,
                    rootBlock.getState(random, currentPos)
                            .setValue(RotatedPillarBlock.AXIS, currentDir.getAxis()));
            context.setBlock(
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
    protected void placeRootedDirt(LevelSimulatedReader level, RandomSource random, BlockPos pos, Context context){
        for(Direction dir : Direction.values()){
            if(level.isStateAtPosition(pos.relative(dir), state -> state.getBlock().equals(Blocks.DIRT) || state.getBlock().equals(Blocks.GRASS_BLOCK))){
                if(random.nextInt(2) == 0){
                    context.setBlock(pos.relative(dir), Blocks.ROOTED_DIRT.defaultBlockState());
                }
            }
        }
    }
}
