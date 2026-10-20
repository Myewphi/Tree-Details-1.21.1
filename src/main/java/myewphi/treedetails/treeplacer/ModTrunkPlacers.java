package myewphi.treedetails.treeplacer;

import myewphi.treedetails.TreeDetails;
import myewphi.treedetails.treeplacer.trunk.BirchTrunkPlacer;
import myewphi.treedetails.treeplacer.trunk.BranchedTrunkPlacer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModTrunkPlacers {
    public static final DeferredRegister<TrunkPlacerType<?>> TRUNK_PLACER_TYPES =
            DeferredRegister.create(Registries.TRUNK_PLACER_TYPE, TreeDetails.MOD_ID);

    public static final DeferredHolder<TrunkPlacerType<?>, TrunkPlacerType<BranchedTrunkPlacer>> BRANCHED_TRUNK_PLACER =
            TRUNK_PLACER_TYPES.register("branched_trunk_placer", () -> new TrunkPlacerType<>(BranchedTrunkPlacer.CODEC));
    public static final DeferredHolder<TrunkPlacerType<?>, TrunkPlacerType<BirchTrunkPlacer>> BIRCH_TRUNK_PLACER =
            TRUNK_PLACER_TYPES.register("birch_trunk_placer", () -> new TrunkPlacerType<>(BirchTrunkPlacer.CODEC));

    public static void register(IEventBus eventBus){
        TRUNK_PLACER_TYPES.register(eventBus);
    }
}
