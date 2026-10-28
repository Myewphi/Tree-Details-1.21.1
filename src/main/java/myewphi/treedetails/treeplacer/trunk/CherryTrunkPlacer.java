package myewphi.treedetails.treeplacer.trunk;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import myewphi.treedetails.treeplacer.ModTrunkPlacers;
import net.minecraft.core.BlockPos;
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
        setDirtAt(level, blockSetter, random, pos.below(), config);

        return List.of();
    }
}
