package twopiradians.minewatch.common.entity;

import java.util.ArrayList;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Rotations;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IThrowableEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import twopiradians.minewatch.common.util.EntityHelper;

public abstract class EntityMW extends Entity implements IThrowableEntity {

	public static final DataParameter<Rotations> VELOCITY = EntityDataManager.<Rotations>createKey(EntityMW.class, DataSerializers.ROTATIONS);
	public static final DataParameter<Integer> HAND = EntityDataManager.<Integer>createKey(EntityMW.class, DataSerializers.VARINT);
	public boolean notDeflectible;
	public int lifetime;
	private EntityLivingBase thrower;
	public boolean isFriendly;

	public EntityMW(World worldIn) {
		this(worldIn, null, -1);
	}

	/**@param hand -1 no muzzle, 0 main-hand, 1 off-hand, 2 middle*/
	public EntityMW(World worldIn, @Nullable EntityLivingBase throwerIn, int hand) {
		super(worldIn);
		if (throwerIn != null) {
			this.thrower = throwerIn;
			this.setPosition(throwerIn.posX, throwerIn.posY + (double)throwerIn.getEyeHeight() - 0.1D, throwerIn.posZ);
		}
		this.dataManager.set(HAND, hand);
	}

	@Override
	protected void entityInit() {
		this.dataManager.register(VELOCITY, new Rotations(0, 0, 0));
		this.dataManager.register(HAND, -1);
	}

	@Override
	public void notifyDataManagerChange(DataParameter<?> key) {
		if (key.getId() == VELOCITY.getId()) {
			this.motionX = this.dataManager.get(VELOCITY).getX();
			this.motionY = this.dataManager.get(VELOCITY).getY();
			this.motionZ = this.dataManager.get(VELOCITY).getZ();
			EntityHelper.setRotations(this);	
		}
		// muzzle particle
		else if (key.getId() == HAND.getId() && this.world.isRemote && this.ticksExisted == 0 && !this.isDead && 
				this.dataManager.get(HAND) != -1 && this.getThrower() instanceof EntityLivingBase)
			this.spawnMuzzleParticles(this.dataManager.get(HAND) >= 0 && this.dataManager.get(HAND) < EnumHand.values().length ? 
					EnumHand.values()[this.dataManager.get(HAND)] : null, this.getThrower());

	}

	/**Spawn muzzle particles when first spawning*/
	public void spawnMuzzleParticles(EnumHand hand, EntityLivingBase shooter) {}

	@Override
	public void onUpdate() {		
		this.prevPosX = this.posX; 
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.prevRotationPitch = this.rotationPitch;
		this.prevRotationYaw = this.rotationYaw;

		// check for impacts
		ArrayList<RayTraceResult> results = EntityHelper.checkForImpact(this);
		RayTraceResult nearest = EntityHelper.getNearestImpact(this, results);
		for (RayTraceResult result : results) 
			if (result != null && isValidImpact(result, result == nearest))
				this.onImpact(result);
		// move if still alive and has motion
		if (!this.isDead && Math.sqrt(motionX*motionX+motionY*motionY+motionZ*motionZ) > 0) {
			if (this.hasNoGravity())
				this.setPosition(this.posX+this.motionX, this.posY+this.motionY, this.posZ+this.motionZ);
			else // needed to set onGround / do block collisions
				this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ); 
		}

		// set dead if needed
		if (!this.world.isRemote && ((this.ticksExisted > lifetime && lifetime > 0) ||
				!(this.getThrower() instanceof EntityLivingBase) || posY <= -64))
			this.setDead();

		this.firstUpdate = false;
	}

	/**Should this result trigger onImpact - moves to hit position if entity*/
	protected boolean isValidImpact(RayTraceResult result, boolean nearest) {
		return result != null && result.typeOfHit != RayTraceResult.Type.MISS && 
				(result.typeOfHit != RayTraceResult.Type.ENTITY || 
				(EntityHelper.shouldHit(getThrower(), result.entityHit, isFriendly) && nearest));
	}

	/**Should this move to the hit position of the RayTraceResult*/
	protected boolean shouldMoveToHitPosition(RayTraceResult result) {
		return result != null;
	}

	protected void onImpact(RayTraceResult result) {
		if (this.shouldMoveToHitPosition(result))
			EntityHelper.moveToHitPosition(this, result);
	}

	@Override
	public float getEyeHeight() {
		return this.height/2f;
	}

	@Override
	public boolean isImmuneToExplosions() {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isInRangeToRenderDist(double distance){
		return distance < 3000;
	}

	@Override
	public EntityLivingBase getThrower() {
		return this.thrower;
	}

	@Override
	public void setThrower(Entity entity) {
		if (entity instanceof EntityLivingBase) 
			this.thrower = (EntityLivingBase) entity;
	}

	@Override
	public boolean writeToNBTOptional(NBTTagCompound compound) {return false;}
	@Override
	public boolean writeToNBTAtomically(NBTTagCompound compound) {return false;}
	@Override
	public void readFromNBT(NBTTagCompound compound) {}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {return compound;}
	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {}
	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {}

}