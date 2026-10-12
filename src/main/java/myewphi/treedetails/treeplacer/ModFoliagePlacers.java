package myewphi.treedetails.treeplacer;

import myewphi.treedetails.TreeDetails;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModFoliagePlacers {
    public static final DeferredRegister<FoliagePlacerType<?>> FOLIAGE_PLACER_TYPES =
            DeferredRegister.create(Registries.FOLIAGE_PLACER_TYPE, TreeDetails.MOD_ID);

    public static final DeferredHolder<FoliagePlacerType<?>, FoliagePlacerType<BranchedFoliagePlacer>> BRANCHED_FOLIAGE_PLACER =
            FOLIAGE_PLACER_TYPES.register("branched_foliage_placer", () -> new FoliagePlacerType<>(BranchedFoliagePlacer.CODEC));

    public static void register(IEventBus eventBus){
        FOLIAGE_PLACER_TYPES.register(eventBus);
    }
}
