package twopiradians.minewatch.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import twopiradians.minewatch.client.model.ModelAnaBullet;
import twopiradians.minewatch.common.Minewatch;
import twopiradians.minewatch.common.entity.EntityWidowmakerBullet;
import twopiradians.minewatch.common.entity.ModEntities;
import twopiradians.minewatch.packet.SPacketSyncSpawningEntity;

public class RenderWidowmakerBullet extends Render<EntityWidowmakerBullet>
{
	private final ModelAnaBullet WIDOWMAKER_BULLET_MODEL = new ModelAnaBullet();
	
	public RenderWidowmakerBullet(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityWidowmakerBullet entity) {
		return new ResourceLocation(Minewatch.MODID, "textures/entity/widowmaker_bullet.png");
	}
	
	@Override
	public void doRender(EntityWidowmakerBullet entity, double x, double y, double z, float entityYaw, float partialTicks) {
		// correct trajectories of fast entities
		if (ModEntities.spawningEntities.containsKey(entity.getPersistentID())) {
			SPacketSyncSpawningEntity packet = ModEntities.spawningEntities.get(entity.getPersistentID());
			entity.rotationPitch = packet.pitch;
			entity.prevRotationPitch = packet.pitch;
			entity.rotationYaw = packet.yaw;
			entity.prevRotationYaw = packet.yaw;
			entity.motionX = packet.motionX;
			entity.motionY = packet.motionY;
			entity.motionZ = packet.motionZ;
			entity.posX = packet.posX;
			entity.posY = packet.posY;
			entity.posZ = packet.posZ;
			ModEntities.spawningEntities.remove(entity.getPersistentID());
		}
				
		GlStateManager.pushMatrix();
		GlStateManager.translate((float)x, (float)y, (float)z);
		GlStateManager.scale(0.1F, 0.1F, 0.1F);
		GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks, 0.0F, 1.0F, 0.0F);
	    GlStateManager.rotate(-entity.rotationPitch, 1.0F, 0.0F, 0.0F);
		this.bindEntityTexture(entity);
		this.WIDOWMAKER_BULLET_MODEL.render(entity, 0, 0, 0, 0, entity.rotationPitch, 0.5f);
		GlStateManager.popMatrix();
    }
}