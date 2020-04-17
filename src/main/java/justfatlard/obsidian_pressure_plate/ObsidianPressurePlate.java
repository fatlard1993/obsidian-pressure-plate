package justfatlard.obsidian_pressure_plate;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

interface CollisionTask { void run(BlockState state, World world, BlockPos pos, Entity entity); }

interface CollisionValidator { boolean canCollide(Entity entity); }

public class ObsidianPressurePlate extends PressurePlateBlock {
	public CollisionTask collisionTask = (state, world, pos, entity) -> { };
	public CollisionValidator collisionValidator = PlayerEntity.class::isInstance;

	public ObsidianPressurePlate(){
		super(ActivationRule.EVERYTHING, FabricBlockSettings.of(Material.STONE).strength(0.6F, 26.0F).build());
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
}