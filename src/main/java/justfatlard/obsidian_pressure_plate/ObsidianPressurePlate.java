package justfatlard.obsidian_pressure_plate;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.entity.EntityType;

interface CollisionTask { void run(BlockState state, World world, BlockPos pos, Entity entity); }

interface CollisionValidator { boolean canCollide(Entity entity); }

public class ObsidianPressurePlate extends PressurePlateBlock {
	public CollisionTask collisionTask = (state, world, pos, entity) -> { };
	public CollisionValidator collisionValidator = PlayerEntity.class::isInstance;

	public ObsidianPressurePlate() {
		super(ActivationRule.EVERYTHING, FabricBlockSettings.of(Material.STONE).noCollision().strength(0.6F, 26.0F));
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity){
		if(world.isClient) return;

		if(collisionValidator.canCollide(entity)){
			int outputLevel = this.getRedstoneOutput(state);

			if(outputLevel == 0){
				this.updatePlateState(entity, world, pos, state, outputLevel);

				if(collisionTask != null) collisionTask.run(state, world, pos, entity);
			}
		}
	}

	@Override
	protected int getRedstoneOutput(World world, BlockPos pos) {
		Box box = BOX.offset(pos);
		boolean playerCollision = !world.getEntitiesByType(EntityType.PLAYER, box, EntityPredicates.EXCEPT_SPECTATOR).isEmpty();

		if (playerCollision) {
			return 15;
		}

		return 0;
	}
}