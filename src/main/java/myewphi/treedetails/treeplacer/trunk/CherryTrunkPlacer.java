package myewphi.treedetails.treeplacer.trunk;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import myewphi.treedetails.TreeDetails;
import myewphi.treedetails.treeplacer.ModTrunkPlacers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public class CherryTrunkPlacer extends TrunkPlacer {
    public static final MapCodec<CherryTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec(
            instance -> trunkPlacerParts(instance)
                    .and(BlockStateProvider.CODEC.fieldOf("branch_block").forGetter(placer -> placer.branchBlock))
                    .apply(instance, CherryTrunkPlacer::new)
    );
    private final BlockStateProvider branchBlock;

    public CherryTrunkPlacer(int baseHeight, int heightRandA, int heightRandB, BlockStateProvider branchBlock) {
        super(baseHeight, heightRandA, heightRandB);
        this.branchBlock = branchBlock;
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return ModTrunkPlacers.CHERRY_TRUNK_PLACER.get();
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
        List<TrunkSection> trunkSections = new java.util.ArrayList<>(List.of());
        HashMap<Direction, Integer> weightMap = HashMap.newHashMap(4);
        weightMap.put(Direction.NORTH, 0);
        weightMap.put(Direction.EAST, 0);
        weightMap.put(Direction.SOUTH, 0);
        weightMap.put(Direction.WEST, 0);

        setDirtAt(level, blockSetter, random, pos.below(), config);

        int numberOfTrunkSections = Math.round(freeTreeHeight / 3f);
        numberOfTrunkSections = Math.max(numberOfTrunkSections - (random.nextInt(4) == 0 ? 1 : 0), 2); //chance to cannabilize one of the trunk sections and give its length to the rest

        //create trunk sections with starter values
        for(int i = 0; i < numberOfTrunkSections; i++){
            trunkSections.add(new TrunkSection(pos, 3, Direction.UP));
        }
        //distribute the remainder across the trunk sections
        int remainder = (freeTreeHeight + (numberOfTrunkSections - 1)) - (3 * numberOfTrunkSections);
        for(int i = 0; i < remainder; i++){
            trunkSections.get(random.nextInt(numberOfTrunkSections)).height += 1;
        }
        //set the locations of trunk sections
        BlockPos curPos = pos;
        Direction lastSectionDir = Direction.UP;
        for(int i = 0; i < numberOfTrunkSections; i++){
            trunkSections.get(i).origin = curPos;

            trunkSections.get(i).direction = lastSectionDir;
            List<Direction> list = new java.util.ArrayList<>(List.of(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST));
            list.remove(lastSectionDir);
            lastSectionDir = list.get(random.nextInt(list.size()));
            curPos = curPos.above(trunkSections.get(i).height - 1).relative(lastSectionDir);

            TreeDetails.LOGGER.info("TEST: " + lastSectionDir);

            switch(lastSectionDir){
                case Direction.NORTH -> weightMap.put(Direction.NORTH, weightMap.get(Direction.NORTH) + 1);
                case Direction.EAST -> weightMap.put(Direction.EAST, weightMap.get(Direction.EAST) + 1);
                case Direction.SOUTH -> weightMap.put(Direction.SOUTH, weightMap.get(Direction.SOUTH) + 1);
                case Direction.WEST -> weightMap.put(Direction.WEST, weightMap.get(Direction.WEST) + 1);
            }
        }

        //create trunk
        //place boughs starting from the top and working down
        Direction lastBoughDir = Direction.UP;
        for(int i = numberOfTrunkSections - 1; i >= 0; i--){
            TrunkSection section = trunkSections.get(i);

            for(int y = 0; y < section.height; y++){
                this.placeLog(level, blockSetter, random, section.origin.above(y), config);
            }

            if(!section.origin.equals(pos)){
                BlockPos boughPos = section.origin.above(section.height - (i == numberOfTrunkSections - 1 ? 1 : (random.nextInt(2) + 1)));
                Direction aboveSectionDir = trunkSections.size() == i + 1 ? Direction.UP : trunkSections.get(i + 1).direction;
                List<Direction> list = new java.util.ArrayList<>(List.of(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST));

                list.remove(aboveSectionDir);
                list.remove(aboveSectionDir == Direction.UP ? Direction.UP : aboveSectionDir.getCounterClockWise());
                list.remove(lastBoughDir);

                Direction currentBoughDir = list.get(random.nextInt(list.size()));
                lastBoughDir = currentBoughDir;

                blockSetter.accept(
                        boughPos.relative(currentBoughDir),
                        Blocks.DIAMOND_BLOCK.defaultBlockState());
                blockSetter.accept(
                        boughPos.relative(currentBoughDir.getClockWise()),
                        Blocks.DIAMOND_BLOCK.defaultBlockState());

                //TreeDetails.LOGGER.info("TEST: " + aboveSectionDir + currentBoughDir);

                /*
                Stream<Map.Entry<Direction,Integer>> sorted =
                        weightMap.entrySet().stream()
                                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()));
                Direction heaviestDir = sorted.findFirst().get().getKey().getOpposite();

                blockSetter.accept(
                        boughPos.relative(heaviestDir),
                        Blocks.DIAMOND_BLOCK.defaultBlockState());

                blockSetter.accept(
                        boughPos.relative(weightMap.get(heaviestDir.getClockWise()) > weightMap.get(heaviestDir.getCounterClockWise()) ?
                                heaviestDir.getClockWise() : heaviestDir.getCounterClockWise()),
                        Blocks.DIAMOND_BLOCK.defaultBlockState());
                */

                //WHERE I LEFT OFF
                /*
                ive got heaviest dir and should probs make a variable for second heaviest dir
                need to make it so that the weight changes each time a bough is placed
                need to randomize dirs if the heaviest/second heaviest spots are equal
                need to make sure neither dir intersects with other trunk sections
                expirement with making it just random instead of using weights?
                 */
            }
        }

        //foliagePlacements.add(new FoliagePlacer.FoliageAttachment(pos, 0, false));
        return foliagePlacements;
    }

    public class TrunkSection {
        public TrunkSection(BlockPos origin, int height, Direction direction){
            this.origin = origin;
            this.height = height;
            this.direction = direction;
        }

        public BlockPos origin;
        public int height;
        public Direction direction;
    }
}
