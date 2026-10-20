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
            .and(IntProvider.codec(1, 32).fieldOf("foliage_width").forGetter(placer -> placer.foliageWidth))
            .apply(instance, BirchFoliagePlacer::new));

    private final IntProvider foliageWidth;

    public BirchFoliagePlacer(IntProvider radius, IntProvider offset, IntProvider foliageWidth) {
        super(radius, offset);
        this.foliageWidth = foliageWidth;
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
        int width = foliageWidth.sample(randomSource) + offset;

        for(int i = 0; i < radius; i++){
            placeLeavesRow(level, setter, randomSource, config, attachment.pos().below(i), width, 0, attachment.doubleTrunk());
        }
    }

    @Override
    public int foliageHeight(RandomSource randomSource, int i, TreeConfiguration treeConfiguration) {
        return 0;
    }

    @Override
    protected boolean shouldSkipLocation(RandomSource random, int localX, int localY, int localZ, int range, boolean large) {
        if(localX == range && localZ == range){
            //rounds the corners
            return true;
        }
        return random.nextInt(10) < 6;
    }
}
