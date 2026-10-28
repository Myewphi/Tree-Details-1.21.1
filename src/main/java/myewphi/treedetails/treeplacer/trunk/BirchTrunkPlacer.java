package myewphi.treedetails.treeplacer.trunk;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import myewphi.treedetails.TreeDetails;
import myewphi.treedetails.treeplacer.ModTrunkPlacers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

import java.util.List;
import java.util.function.BiConsumer;

public class BirchTrunkPlacer extends TrunkPlacer {
    public static final MapCodec<BirchTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec(
            instance -> trunkPlacerParts(instance)
                    .and(BlockStateProvider.CODEC.fieldOf("branch_block").forGetter(placer -> placer.branchBlock))
                    .apply(instance, BirchTrunkPlacer::new)
    );
    private final BlockStateProvider branchBlock;

    public BirchTrunkPlacer(int baseHeight, int heightRandA, int heightRandB, BlockStateProvider branchBlock) {
        super(baseHeight, heightRandA, heightRandB);
        this.branchBlock = branchBlock;
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
}
