package myewphi.treedetails.datagen;

import myewphi.treedetails.item.ModItems;
import myewphi.treedetails.TreeDetails;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, TreeDetails.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        //basicItem(ModItems.EXAMPLE_ITEM.get());
    }
}
