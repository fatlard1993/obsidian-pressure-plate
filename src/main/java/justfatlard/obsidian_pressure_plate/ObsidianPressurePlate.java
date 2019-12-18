package justfatlard.obsidian_pressure_plate;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.block.FabricBlockSettings;

interface CollisionTask { void run(BlockState state, World world, BlockPos pos, Entity entity); }

interface CollisionValidator { boolean canCollide(Entity entity); }

class BasePressurePlateBlock extends PressurePlateBlock {
	private CollisionTask collisionTask;
	private CollisionValidator collisionValidator;

	private BasePressurePlateBlock(Builder builder){
		super(builder.activationRule, builder.settings);

		collisionTask = builder.collisionTask;
		collisionValidator = builder.collisionValidator;
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity){
		if(world.isClient) return;

		if(collisionValidator.canCollide(entity)){
			int outputLevel = this.getRedstoneOutput(state);

			if(outputLevel == 0){
				this.updatePlateState(world, pos, state, outputLevel);

				if(collisionTask != null) collisionTask.run(state, world, pos, entity);
			}
		}
	}

	public static final class Builder {
		private Settings settings = FabricBlockSettings.of(Material.STONE).hardness(1).build();
		private ActivationRule activationRule = ActivationRule.EVERYTHING;
		private CollisionTask collisionTask = (state, world, pos, entity) -> { };
		private CollisionValidator collisionValidator = entity -> true;

		public Builder(){}

		public Builder withSettings(Settings settings){
			this.settings = settings;

			return this;
		}

		public Builder withActivationRule(ActivationRule rule){
			activationRule = rule;

			return this;
		}

		public Builder onCollision(CollisionTask task){
			collisionTask = task;

			return this;
		}

		public Builder withCollisionValidator(CollisionValidator validator){
			collisionValidator = validator;

			return this;
		}

		public BasePressurePlateBlock buildAndRegister(){
			BasePressurePlateBlock registered = Registry.register(Registry.BLOCK, justfatlard.obsidian_pressure_plate.ObsidianPressurePlate.gen_id("obsidian_pressure_plate"), new BasePressurePlateBlock(this));
			Registry.register(Registry.ITEM, justfatlard.obsidian_pressure_plate.ObsidianPressurePlate.gen_id("obsidian_pressure_plate"), new BlockItem(registered, new Item.Settings().group(ItemGroup.REDSTONE)));

			return registered;
		}
	}
}

public class ObsidianPressurePlate implements ModInitializer {
	private static final String MODID = "obsidian-pressure-plate-justfatlard";
	public static final Block OBSIDIAN_PRESSURE_PLATE = new BasePressurePlateBlock.Builder().withCollisionValidator(PlayerEntity.class::isInstance).buildAndRegister();

	@Override
	public void onInitialize(){
		System.out.println("Loaded Obsidian Pressure Plate!");
	}

	public static Identifier gen_id(String name){
		return new Identifier(MODID, name);
	}
}