package justfatlard.obsidian_pressure_plate;

import java.util.Iterator;
import java.util.List;

import net.fabricmc.fabric.api.block.FabricBlockSettings;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
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

	public ObsidianPressurePlate(){
		super(ActivationRule.EVERYTHING, FabricBlockSettings.of(Material.STONE).noCollision().strength(0.6F, 26.0F).build());
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

	@Override
	protected int getRedstoneOutput(World world, BlockPos pos) {
		Box box = BOX.offset(pos);
		List list3 = world.getEntities((Entity)null, box);

		if (!list3.isEmpty()) {
			Iterator var5 = list3.iterator();

			while(var5.hasNext()) {
				Entity entity = (Entity)var5.next();
				if (entity.getType() == EntityType.PLAYER){
					return 15;
				}
			}
		}

		return 0;
	}
}