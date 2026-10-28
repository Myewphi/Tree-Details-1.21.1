package myewphi.treedetails.treeplacer;

import myewphi.treedetails.TreeDetails;
import myewphi.treedetails.treeplacer.decorator.RootsDecorator;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModTreeDecorators {
    public static final DeferredRegister<TreeDecoratorType<?>> TREE_DECORATOR_TYPES =
            DeferredRegister.create(Registries.TREE_DECORATOR_TYPE, TreeDetails.MOD_ID);

    public static final DeferredHolder<TreeDecoratorType<?>, TreeDecoratorType<RootsDecorator>> ROOTS_DECORATOR =
            TREE_DECORATOR_TYPES.register("roots_decorator", () -> new TreeDecoratorType<>(RootsDecorator.CODEC));

    public static void register(IEventBus eventBus){
        TREE_DECORATOR_TYPES.register(eventBus);
    }

}
