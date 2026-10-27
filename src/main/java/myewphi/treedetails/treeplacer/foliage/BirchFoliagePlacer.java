package myewphi.treedetails.treeplacer.foliage;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import myewphi.treedetails.treeplacer.ModFoliagePlacers;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;

public class BirchFoliagePlacer extends FoliagePlacer {
    public static final MapCodec<BirchFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec((instance) -> foliagePlacerParts(instance)
            .apply(instance, BirchFoliagePlacer::new));

    public BirchFoliagePlacer(IntProvider radius, IntProvider offset) {
        super(radius, offset);
    }

    @Override
    protected FoliagePlacerType<?> type() {
        return ModFoliagePlacers.BIRCH_FOLIAGE_PLACER.get();
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

        tryPlaceLeaf(level, setter, randomSource, config, attachment.pos());
        tryPlaceLeaf(level, setter, randomSource, config, attachment.pos().north());
        tryPlaceLeaf(level, setter, randomSource, config, attachment.pos().east());
        tryPlaceLeaf(level, setter, randomSource, config, attachment.pos().south());
        tryPlaceLeaf(level, setter, randomSource, config, attachment.pos().west());
        for(int i = 1; i < height; i++){
            placeLeavesRow(level, setter, randomSource, config, attachment.pos().below(i), radius, 0, attachment.doubleTrunk());
        }
    }

    @Override
    public int foliageHeight(RandomSource randomSource, int i, TreeConfiguration treeConfiguration) {
        return 3;
    }

    @Override
    protected boolean shouldSkipLocation(RandomSource random, int localX, int localY, int localZ, int range, boolean large) {
        if(localX == range && localZ == range){
            //rounds the corners
            return true;
        }
        return random.nextInt(4) == 0;
    }
}
