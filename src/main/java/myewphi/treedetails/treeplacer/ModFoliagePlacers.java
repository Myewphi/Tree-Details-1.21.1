package myewphi.treedetails.treeplacer;

import myewphi.treedetails.TreeDetails;
import myewphi.treedetails.treeplacer.foliage.BirchFoliagePlacer;
import myewphi.treedetails.treeplacer.foliage.CherryFoliagePlacer;
import myewphi.treedetails.treeplacer.foliage.OakFoliagePlacer;
import myewphi.treedetails.treeplacer.foliage.SingleLeafFoliagePlacer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModFoliagePlacers {
    public static final DeferredRegister<FoliagePlacerType<?>> FOLIAGE_PLACER_TYPES =
            DeferredRegister.create(Registries.FOLIAGE_PLACER_TYPE, TreeDetails.MOD_ID);

    public static final DeferredHolder<FoliagePlacerType<?>, FoliagePlacerType<SingleLeafFoliagePlacer>> SINGLE_LEAF_FOLIAGE_PLACER =
            FOLIAGE_PLACER_TYPES.register("single_leaf_foliage_placer", () -> new FoliagePlacerType<>(SingleLeafFoliagePlacer.CODEC));
    public static final DeferredHolder<FoliagePlacerType<?>, FoliagePlacerType<OakFoliagePlacer>> OAK_FOLIAGE_PLACER =
            FOLIAGE_PLACER_TYPES.register("oak_foliage_placer", () -> new FoliagePlacerType<>(OakFoliagePlacer.CODEC));
    public static final DeferredHolder<FoliagePlacerType<?>, FoliagePlacerType<BirchFoliagePlacer>> BIRCH_FOLIAGE_PLACER =
            FOLIAGE_PLACER_TYPES.register("birch_foliage_placer", () -> new FoliagePlacerType<>(BirchFoliagePlacer.CODEC));
    public static final DeferredHolder<FoliagePlacerType<?>, FoliagePlacerType<CherryFoliagePlacer>> CHERRY_FOLIAGE_PLACER =
            FOLIAGE_PLACER_TYPES.register("cherry_foliage_placer", () -> new FoliagePlacerType<>(CherryFoliagePlacer.CODEC));

    public static void register(IEventBus eventBus){
        FOLIAGE_PLACER_TYPES.register(eventBus);
    }
}
