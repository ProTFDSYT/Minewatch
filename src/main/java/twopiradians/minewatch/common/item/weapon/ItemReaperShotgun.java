package twopiradians.minewatch.common.item.weapon;

import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Nullable;

import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.CombatRules;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import twopiradians.minewatch.client.key.Keys.KeyBind;
import twopiradians.minewatch.client.model.ModelMWArmor;
import twopiradians.minewatch.common.Minewatch;
import twopiradians.minewatch.common.entity.hero.EntityHero;
import twopiradians.minewatch.common.entity.projectile.EntityReaperBullet;
import twopiradians.minewatch.common.hero.Ability;
import twopiradians.minewatch.common.hero.EnumHero;
import twopiradians.minewatch.common.item.armor.ItemMWArmor;
import twopiradians.minewatch.common.sound.ModSoundEvents;
import twopiradians.minewatch.common.tickhandler.TickHandler;
import twopiradians.minewatch.common.tickhandler.TickHandler.Handler;
import twopiradians.minewatch.common.tickhandler.TickHandler.Identifier;
import twopiradians.minewatch.common.util.EntityHelper;
import twopiradians.minewatch.common.util.Handlers;
import twopiradians.minewatch.packet.SPacketSimple;

public class ItemReaperShotgun extends ItemMWWeapon {

	public static HashMap<EntityPlayer, Boolean> wraithViewBobbing = Maps.newHashMap();
	public static final Handler WRAITH = new Handler(Identifier.REAPER_WRAITH, false) {
		@Override
		@SideOnly(Side.CLIENT)
		public boolean onClientTick() {
			if (player == Minecraft.getMinecraft().player)
				if (this.ticksLeft > 1)
					Minecraft.getMinecraft().gameSettings.viewBobbing = false;
				else if (wraithViewBobbing.containsKey(player)) {
					Minecraft.getMinecraft().gameSettings.viewBobbing = wraithViewBobbing.get(player);
					wraithViewBobbing.remove(player);
				}
			if (this.ticksLeft > 8) {
				boolean firstPerson = player == Minecraft.getMinecraft().player && 
						Minecraft.getMinecraft().gameSettings.thirdPersonView == 0 ;
				for (int i=0; i<3; ++i) {
					entityLiving.world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, 
							entityLiving.posX+entityLiving.world.rand.nextDouble()-0.5d, 
							entityLiving.posY+entityLiving.world.rand.nextDouble()*(firstPerson ? 0d : 1.5d), 
							entityLiving.posZ+entityLiving.world.rand.nextDouble()-0.5d, 
							0, (firstPerson ? -0.1d : 0d), 0);
					entityLiving.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, 
							entityLiving.posX+entityLiving.world.rand.nextDouble()-0.5d, 
							entityLiving.posY+entityLiving.world.rand.nextDouble()*(firstPerson ? 0d : 1.5d), 
							entityLiving.posZ+entityLiving.world.rand.nextDouble()-0.5d, 
							0, (firstPerson ? -0.1d : 0d), 0);
				}
			}
			return super.onClientTick();
		}

		@Override
		public boolean onServerTick() {
			// set resistance high to prevent hurt sound/animations 
			// (entityLiving.hurtResistantTime > entityLiving.maxHurtResistantTime / 2.0F)
			entityLiving.hurtResistantTime = (int) (entityLiving.maxHurtResistantTime*2.1f); 
			return super.onServerTick();
		}

		@Override
		public Handler onServerRemove() {
			EnumHero.REAPER.ability2.keybind.setCooldown(entityLiving, 160, false);
			entityLiving.hurtResistantTime = 0;
			return super.onServerRemove();
		}
	};

	public static HashMap<EntityPlayer, Integer> tpThirdPersonView = Maps.newHashMap();
	public static final Handler TPS = new Handler(Identifier.REAPER_TELEPORT, true) {
		@Override
		@SideOnly(Side.CLIENT)
		public boolean onClientTick() {
			// stop handler and play sound if needed
			if ((entityLiving.getHeldItemMainhand() == null || entityLiving.getHeldItemMainhand().getItem() != EnumHero.REAPER.weapon ||
					!EnumHero.REAPER.ability1.isSelected(entityLiving) || 
					!EnumHero.REAPER.weapon.canUse(entityLiving, true, EnumHand.MAIN_HAND, true)) && this.ticksLeft == -1) {
				entityLiving.playSound(ModSoundEvents.reaperTeleportStop, 1.0f, 1.0f);
				return true;
			}
			else {		
				// change view
				if (this.ticksLeft != -1 && Minecraft.getMinecraft().player == player) {
					if (this.ticksLeft > 1)
						Minecraft.getMinecraft().gameSettings.thirdPersonView = 1;
					else if (tpThirdPersonView.containsKey(player)) {
						Minecraft.getMinecraft().gameSettings.thirdPersonView = tpThirdPersonView.get(player);
						tpThirdPersonView.remove(player);
					}
				}
				// particles
				if (this.ticksLeft != -1 || entityLiving == Minecraft.getMinecraft().player) {
					if (entityLiving.ticksExisted % 2 == 0)
						Minewatch.proxy.spawnParticlesReaperTeleport(entityLiving.world, entityLiving, false, 1);
					else if (entityLiving.ticksExisted % 3 == 0)
						Minewatch.proxy.spawnParticlesReaperTeleport(entityLiving.world, entityLiving, false, 3);
					Minewatch.proxy.spawnParticlesReaperTeleport(entityLiving.world, entityLiving, false, 2);
				}
				// tp sound
				if (entityLiving.ticksExisted % 13 == 0 && this.ticksLeft == -1)
					entityLiving.playSound(ModSoundEvents.reaperTeleportDuring, entityLiving.world.rand.nextFloat()*0.5f+0.3f, entityLiving.world.rand.nextFloat()*0.5f+0.75f);
				// particles at entityLiving
				if (this.ticksLeft > 40 && this.ticksLeft != -1) {
					if (entityLiving.ticksExisted % 2 == 0)
						Minewatch.proxy.spawnParticlesReaperTeleport(entityLiving.world, entityLiving, true, 1);
					else if (entityLiving.ticksExisted % 3 == 0)
						Minewatch.proxy.spawnParticlesReaperTeleport(entityLiving.world, entityLiving, true, 3);
					Minewatch.proxy.spawnParticlesReaperTeleport(entityLiving.world, entityLiving, true, 2);
				}
			}
			return this.ticksLeft != -1 && --this.ticksLeft <= 0;
		}

		@Override
		public boolean onServerTick() {
			if (this.ticksLeft <= 50 && this.ticksLeft >= 25)
				entityLiving.hurtResistantTime = (int) (entityLiving.maxHurtResistantTime*2.1f); 
			else if (this.ticksLeft == 24)
				entityLiving.hurtResistantTime = 0;
			if (this.ticksLeft == 40) {
				if (entityLiving.isRiding())
					entityLiving.dismountRidingEntity();
				entityLiving.setPositionAndUpdate(this.position.xCoord, 
						this.position.yCoord, 
						this.position.zCoord);
				if (entityLiving.world.rand.nextBoolean())
					entityLiving.world.playSound(null, entityLiving.getPosition(), ModSoundEvents.reaperTeleportVoice, SoundCategory.PLAYERS, 1.0f, 1.0f);
			}
			return super.onServerTick();
		}

		@SideOnly(Side.CLIENT)
		@Override
		public Handler onClientRemove() {
			if (this.ticksLeft != -1) 
				EnumHero.REAPER.ability1.toggle(entityLiving, false);
			return super.onClientRemove();
		}

		@Override
		public Handler onServerRemove() {
			EnumHero.REAPER.ability1.keybind.setCooldown(entityLiving, 200, false); 
			return super.onServerRemove();
		}
	};

	public ItemReaperShotgun() {
		super(30);
		this.saveEntityToNBT = true;
		this.hasOffhand = true;
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public void onItemLeftClick(ItemStack stack, World world, EntityLivingBase player, EnumHand hand) { 
		// shoot
		if (this.canUse(player, true, hand, false) && 
				TickHandler.getHandler(player, Identifier.REAPER_TELEPORT) == null && 
				!hero.ability1.isSelected(player)) {
			if (!world.isRemote) {
				for (int i=0; i<20; i++) {
					EntityReaperBullet bullet = new EntityReaperBullet(world, player, hand.ordinal());
					EntityHelper.setAim(bullet, player, player.rotationPitch, player.rotationYawHead, -1, 8F, hand, 14, 0.55f);
					world.spawnEntity(bullet);
				}
				world.playSound(null, player.posX, player.posY, player.posZ, 
						ModSoundEvents.reaperShoot, SoundCategory.PLAYERS, 
						world.rand.nextFloat()+0.5F, world.rand.nextFloat()/2+0.75f);	
				this.subtractFromCurrentAmmo(player, 1, hand);
				this.setCooldown(player, 11);
				if (world.rand.nextInt(8) == 0)
					player.getHeldItem(hand).damageItem(1, player);
			}
		}
	}

	@SuppressWarnings("deprecation")
	@Nullable
	private Vec3d getTeleportPos(EntityLivingBase player) {
		try {
			RayTraceResult result = player.world.rayTraceBlocks(EntityHelper.getPositionEyes(player), 
					player.getLookVec().scale(Integer.MAX_VALUE), true, true, true);
			if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK && result.hitVec != null) {
				BlockPos pos = new BlockPos(result.hitVec.xCoord, result.getBlockPos().getY(), result.hitVec.zCoord);

				double adjustZ = result.sideHit == EnumFacing.SOUTH ? -0.5d : 0;
				double adjustX = result.sideHit == EnumFacing.EAST ? -0.5d : 0;

				pos = pos.add(adjustX, 0, adjustZ);
				IBlockState state = player.world.getBlockState(pos);
				IBlockState state1 = player.world.getBlockState(pos.up());
				IBlockState state2 = player.world.getBlockState(pos.up(2));

				if ((player.world.isAirBlock(pos.up()) || state1.getBlock().getCollisionBoundingBox(state1, player.world, pos.up()) == null ||
						state1.getBlock().getCollisionBoundingBox(state1, player.world, pos.up()) == Block.NULL_AABB) && 
						(player.world.isAirBlock(pos.up(2)) || state2.getBlock().getCollisionBoundingBox(state2, player.world, pos.up(2)) == null ||
						state2.getBlock().getCollisionBoundingBox(state2, player.world, pos.up(2)) == Block.NULL_AABB) && 
						!player.world.isAirBlock(pos) && 
						state.getBlock().getCollisionBoundingBox(state, player.world, pos) != null &&
						state.getBlock().getCollisionBoundingBox(state, player.world, pos) != Block.NULL_AABB &&
						Math.sqrt(result.getBlockPos().distanceSq(player.posX, player.posY, player.posZ)) <= 35)
					return new Vec3d(result.hitVec.xCoord + adjustX, 
							result.getBlockPos().getY()+1+(state.getBlock() instanceof BlockFence ? 0.5d : 0), 
							result.hitVec.zCoord + adjustZ);
			}
		}
		catch (Exception e) {}
		return null;
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
		super.onUpdate(stack, world, entity, itemSlot, isSelected);

		if (entity instanceof EntityLivingBase && isSelected) {
			EntityLivingBase player = (EntityLivingBase)entity;
			// teleport
			if (hero.ability1.isSelected(player) && this.canUse((EntityLivingBase) entity, true, EnumHand.MAIN_HAND, true)) {   
				if (world.isRemote) {
					Vec3d tpVec = this.getTeleportPos(player);
					Handler handler = TickHandler.getHandler(player, Identifier.REAPER_TELEPORT);
					if (tpVec != null) {
						if (handler == null) {
							TickHandler.register(true, TPS.setEntity(player).setPosition(tpVec).setTicks(-1));
							if (player == Minewatch.proxy.getClientPlayer())
								Minewatch.proxy.spawnParticlesReaperTeleport(world, player, false, 0);
							if (KeyBind.ABILITY_2.isKeyDown(player))
								player.playSound(ModSoundEvents.reaperTeleportStart, 1.0f, 1.0f);
						}
						else if (handler.ticksLeft == -1)
							handler.setPosition(tpVec);
					}
					else if (handler != null && handler.ticksLeft == -1)
						TickHandler.unregister(true, handler);
				}
				else if (KeyBind.LMB.isKeyDown(player)) {
					Vec3d tpVec = this.getTeleportPos(player);
					// don't tp if hero and tp is less than 10 blocks or tp is farther from attack target than current position
					if (tpVec != null && !world.isRemote && 
							!(player instanceof EntityHero && (player.getPositionVector().distanceTo(tpVec) < 10 || 
									(((EntityHero)player).getAttackTarget() != null && 
									player.getDistanceToEntity(((EntityHero)player).getAttackTarget()) < 
									((EntityHero)player).getAttackTarget().getPositionVector().distanceTo(tpVec))))) {
						player.rotationPitch = 0;
						Minewatch.network.sendToAll(new SPacketSimple(1, player, false, Math.floor(tpVec.xCoord)+0.5d, tpVec.yCoord, Math.floor(tpVec.zCoord)+0.5d));
						Minewatch.proxy.playFollowingSound(player, ModSoundEvents.reaperTeleportFinal, SoundCategory.PLAYERS, 1.0f, 1.0f, false);
						TickHandler.register(false, TPS.setEntity(player).setTicks(70).setPosition(new Vec3d(Math.floor(tpVec.xCoord)+0.5d, tpVec.yCoord, Math.floor(tpVec.zCoord)+0.5d)),
								Ability.ABILITY_USING.setEntity(player).setTicks(70).setAbility(EnumHero.REAPER.ability1),
								Handlers.PREVENT_INPUT.setEntity(player).setTicks(70),
								Handlers.PREVENT_MOVEMENT.setEntity(player).setTicks(70), 
								Handlers.PREVENT_ROTATION.setEntity(player).setTicks(70));
						if (player instanceof EntityPlayerMP)
							Minewatch.network.sendTo(new SPacketSimple(9, player, false, 70, 0, 0), (EntityPlayerMP) player);
					}
				}

				if (KeyBind.RMB.isKeyDown(player))
					hero.ability1.toggle(player, false);
			}
			// wraith
			else if (hero.ability2.isSelected(player) && !world.isRemote &&
					this.canUse((EntityLivingBase) entity, true, EnumHand.MAIN_HAND, true)) {
				TickHandler.register(false, Ability.ABILITY_USING.setEntity(player).setTicks(60).setAbility(hero.ability2),
						WRAITH.setEntity(player).setTicks(60));
				Minewatch.network.sendToAll(new SPacketSimple(10, player, false));
				this.setCurrentAmmo(player, this.getMaxAmmo(player), EnumHand.MAIN_HAND, EnumHand.OFF_HAND);
				player.addPotionEffect(new PotionEffect(MobEffects.SPEED, 60, 1, true, false));
				Minewatch.proxy.playFollowingSound(player, ModSoundEvents.reaperWraith, SoundCategory.PLAYERS, 1, 1, false);
			}
		}
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return (Minewatch.proxy.getClientPlayer() != null && 
				TickHandler.hasHandler(Minewatch.proxy.getClientPlayer(), Identifier.REAPER_WRAITH)) ? 
						true : super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void renderWraithOverlay(RenderGameOverlayEvent.Pre event) {
		EntityPlayer player = Minecraft.getMinecraft().player;
		if (event.getType() == ElementType.ALL && player != null && 
				TickHandler.hasHandler(player, Identifier.REAPER_WRAITH)) {
			float ticks = TickHandler.hasHandler(player, Identifier.REAPER_WRAITH) ? 
					60 - TickHandler.getHandler(player, Identifier.REAPER_WRAITH).ticksLeft+Minecraft.getMinecraft().getRenderPartialTicks() : 10;
					double height = event.getResolution().getScaledHeight_double();
					double width = event.getResolution().getScaledWidth_double();

					GlStateManager.pushMatrix();
					GlStateManager.enableBlend();
					//PORT scale x event.getResolution().getScaleFactor()
					GlStateManager.scale(width/256d, height/256d, 1);
					int firstImage = (int) (ticks / 10);
					int secondImage = firstImage + 1;
					if (firstImage < 6) {
						GlStateManager.color(1, 1, 1, 1.1f-((ticks) % 10)/10f);
						Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(Minewatch.MODID, "textures/gui/reaper_wraith_"+firstImage+".png"));
						GuiUtils.drawTexturedModalRect(0, 0, 0, 0, 256, 256, 0);
					}
					if (secondImage < 6) {
						GlStateManager.color(1, 1, 1, (ticks % 10)/10f);
						Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(Minewatch.MODID, "textures/gui/reaper_wraith_"+secondImage+".png"));
						GuiUtils.drawTexturedModalRect(0, 0, 0, 0, 256, 256, 0);
					}
					GlStateManager.popMatrix();
		}
	}

	@SubscribeEvent
	public void preventWraithDamage(LivingAttackEvent event) {
		if (TickHandler.hasHandler(event.getEntity(), Identifier.REAPER_WRAITH) &&
				!event.getSource().canHarmInCreative()) 
			event.setCanceled(true);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void moveTpCamera(FOVUpdateEvent event) {
		if (Minecraft.getMinecraft().world != null &&
				TickHandler.getHandler(Minecraft.getMinecraft().player, Identifier.REAPER_TELEPORT) != null &&
				TickHandler.getHandler(Minecraft.getMinecraft().player, Identifier.REAPER_TELEPORT).ticksLeft > 0) 
			event.setNewfov(event.getFov()+0.8f);
	}

	@SubscribeEvent
	public void damageEntities(LivingHurtEvent event) {
		if (event.getSource().getEntity() instanceof EntityLivingBase && event.getEntityLiving() != null) {
			EntityLivingBase source = ((EntityLivingBase)event.getSource().getEntity());
			EntityLivingBase target = event.getEntityLiving();
			// heal reaper
			if (!source.world.isRemote && ItemMWArmor.SetManager.getWornSet(source) == hero &&
					source.getHeldItemMainhand() != null && source.getHeldItemMainhand().getItem() == this) {
				try {
					float damage = event.getAmount();
					damage = CombatRules.getDamageAfterAbsorb(damage, (float)event.getEntityLiving().getTotalArmorValue(), (float)event.getEntityLiving().getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
					damage = EnumHero.RenderManager.applyPotionDamageCalculations(source, event.getSource(), damage);
					if (damage > 0) 
						source.heal(damage * 0.2f);
				}
				catch (Exception e) {}
			}
			// cancel attack in wraith
			if (!source.world.isRemote && TickHandler.hasHandler(target, Identifier.REAPER_WRAITH)) 
				event.setCanceled(true);
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void blockWhileTping(RenderLivingEvent.Pre<EntityLivingBase> event) {
		Handler handler = TickHandler.getHandler(event.getEntity(), Identifier.REAPER_TELEPORT);
		if (handler != null && handler.ticksLeft != -1 && event.getRenderer().getMainModel() instanceof ModelBiped) {
			ModelBiped model = (ModelBiped) event.getRenderer().getMainModel();
			model.leftArmPose = ModelBiped.ArmPose.BLOCK;
			model.rightArmPose = ModelBiped.ArmPose.BLOCK;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean preRenderArmor(EntityLivingBase entity, ModelMWArmor model) {
		// wraith
		if (TickHandler.hasHandler(entity, Identifier.REAPER_WRAITH)) { 
			Handler handler = TickHandler.getHandler(entity, Identifier.REAPER_WRAITH);
			float delay = 10;
			float color = handler.ticksLeft > (60-delay) ? 1f-(1f-(handler.ticksLeft-60+delay)/delay)*0.6f : 
				handler.ticksLeft < delay ? 1f-handler.ticksLeft/delay*0.6f : 0.4f;
			GlStateManager.color(color-0.1f, color-0.1f, color-0.1f, color);
			return true;
		}
		// teleport
		else if (TickHandler.hasHandler(entity, Identifier.REAPER_TELEPORT)) { 
			Handler handler = TickHandler.getHandler(entity, Identifier.REAPER_TELEPORT);
			float delay = 10;
			float color = (handler.ticksLeft > (40-delay) && handler.ticksLeft < (40+delay)) ? 
					Math.abs((handler.ticksLeft-40)/(delay*2f)) : 1f;
					GlStateManager.color(color, color, color, color);
					return true;
		}
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ArrayList<String> getAllModelLocations(ArrayList<String> locs) {
		locs.add("_0");
		locs.add("_1");
		return locs;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getModelLocation(ItemStack stack, @Nullable EntityLivingBase entity) {
		Handler handler = TickHandler.getHandler(entity, Identifier.REAPER_TELEPORT);
		boolean tping = handler != null && handler.ticksLeft != -1;
		return tping ? "_1" : "_0";
	}

}