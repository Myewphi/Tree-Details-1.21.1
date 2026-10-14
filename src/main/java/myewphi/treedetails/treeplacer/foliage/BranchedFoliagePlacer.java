package myewphi.treedetails.treeplacer.foliage;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import myewphi.treedetails.treeplacer.ModFoliagePlacers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;

import java.util.function.Predicate;

public class BranchedFoliagePlacer extends FoliagePlacer {
    public static final MapCodec<BranchedFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec((instance) -> foliagePlacerParts(instance).apply(instance, BranchedFoliagePlacer::new));

    public BranchedFoliagePlacer(IntProvider radius, IntProvider offset) {
        super(radius, offset);
    }

    @Override
    protected FoliagePlacerType<?> type() {
        return ModFoliagePlacers.BRANCHED_FOLIAGE_PLACER.get();
    }

    @Override
    protected void createFoliage(
            LevelSimulatedReader level,
            FoliageSetter setter,
            RandomSource randomSource,
            TreeConfiguration config,
            int maxFreeTreeHeight,
            FoliageAttachment attachment,
            int height,
            int radius,
            int offset) {
        this.placeLeavesModified(level, setter, randomSource, config, attachment.pos(), 1, 0, attachment.doubleTrunk());
        this.placeLeavesModified(level, setter, randomSource, config, attachment.pos(), 2, -1, attachment.doubleTrunk());
        this.placeLeavesModified(level, setter, randomSource, config, attachment.pos(), 2, -2, attachment.doubleTrunk());
    }

    @Override
    public int foliageHeight(RandomSource randomSource, int i, TreeConfiguration treeConfiguration) {
        return 3;
    }

    protected boolean shouldSkipLocation(RandomSource random, int localX, int localY, int localZ, int range, boolean large, LevelSimulatedReader level, BlockPos pos, BlockState log) {
        if(localY == 0){
            return localX == range && localZ == range && (random.nextInt(3) != 0);
        }
        if(localY == -1){
            if((Math.abs(localX) == 1 && localZ == 0) || (Math.abs(localZ) == 1 && localX == 0)){
                return false;
            }
        }
        if(localY == -2){
            //inner corners
            if(Math.abs(localX) == range - 1 && Math.abs(localZ) == range - 1){
                return random.nextInt(2) == 0;
            }
            //outer next to corners
            if((Math.abs(localX) == range - 1 && localZ == 0) || (Math.abs(localZ) == range - 1 && localX == 0) ){
                if(random.nextInt(2) == 0){
                    return true;
                }
            }
        }

        if(localX == range && localZ == range){
            //rounds the corners
            return true;
        }
        else {
            if(localX <= 1 && localZ <= 1){
                //inside leaves
                return random.nextInt(6) != 0;
            }
            else {
                //outer leaves
                if(hasAdjacentBlock(level, pos, state -> state.getBlock().equals(log.getBlock()))){
                    return true;
                }
                return random.nextInt(6) == 0;
            }
        }
    }

    boolean hasAdjacentBlock(LevelSimulatedReader level, BlockPos pos, Predicate<BlockState> predicate){
        for(Direction dir : Direction.values()){
            if(level.isStateAtPosition(pos.relative(dir), predicate)){
                return true;
            }
        }
        return false;
    }


    @Override
    protected boolean shouldSkipLocation(RandomSource random, int localX, int localY, int localZ, int range, boolean large) {
        return false;
    }
    protected boolean shouldSkipLocationSigned(RandomSource random, int localX, int localY, int localZ, int range, boolean large, LevelSimulatedReader level, BlockPos pos, BlockState log) {
        int i;
        int j;
        if (large) {
            i = Math.min(Math.abs(localX), Math.abs(localX - 1));
            j = Math.min(Math.abs(localZ), Math.abs(localZ - 1));
        } else {
            i = Math.abs(localX);
            j = Math.abs(localZ);
        }

        return this.shouldSkipLocation(random, i, localY, j, range, large, level, pos, log);
    }
    protected void
    placeLeavesModified(
            LevelSimulatedReader level,
            FoliagePlacer.FoliageSetter setter,
            RandomSource random,
            TreeConfiguration config,
            BlockPos pos,
            int range,
            int localY,
            boolean large
    ) {
        int i = large ? 1 : 0;
        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();

        for (int localX = -range; localX <= range + i; localX++) {
            for (int localZ = -range; localZ <= range + i; localZ++) {
                if (!this.shouldSkipLocationSigned(random, localX, localY, localZ, range, large, level, pos.offset(localX, localY, localZ), config.trunkProvider.getState(random, pos))) {
                    blockPos.setWithOffset(pos, localX, localY, localZ);
                    tryPlaceLeaf(level, setter, random, config, blockPos);
                }
            }
        }
    }
}
