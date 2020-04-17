package justfatlard.obsidian_pressure_plate;

import net.fabricmc.api.ModInitializer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Main implements ModInitializer {
	public final static ObsidianPressurePlate OBSIDIAN_PRESSURE_PLATE = new ObsidianPressurePlate();

	@Override
	public void onInitialize(){
		Registry.register(Registry.BLOCK, new Identifier("obsidian-pressure-plate-justfatlard", "obsidian_pressure_plate"), OBSIDIAN_PRESSURE_PLATE);
		Registry.register(Registry.ITEM, new Identifier("obsidian-pressure-plate-justfatlard", "obsidian_pressure_plate"), new BlockItem(OBSIDIAN_PRESSURE_PLATE, new Item.Settings().group(ItemGroup.REDSTONE)));

		System.out.println("Loaded Obsidian Pressure Plate!");
	}
}