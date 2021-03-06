package twopiradians.minewatch.common.sound;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import twopiradians.minewatch.common.Minewatch;
import twopiradians.minewatch.common.hero.EnumHero;

public class ModSoundEvents {

	public static SoundEvent guiHover;
	public static SoundEvent[] multikill = new SoundEvent[5];
	public static SoundEvent kill;
	public static SoundEvent headshot;
	public static SoundEvent hurt;
	public static SoundEvent abilityRecharge;
	public static SoundEvent abilityMultiRecharge;
	public static SoundEvent abilityNotReady;
	public static SoundEvent wallClimb;
	public static SoundEvent anaShoot;
	public static SoundEvent anaHeal;
	public static SoundEvent anaSleepShoot;
	public static SoundEvent anaSleepHit;
	public static SoundEvent anaSleepVoice;
	public static SoundEvent reaperShoot;
	public static SoundEvent reaperTeleportStart;
	public static SoundEvent reaperTeleportDuring;
	public static SoundEvent reaperTeleportStop;
	public static SoundEvent reaperTeleportFinal;
	public static SoundEvent reaperTeleportVoice;
	public static SoundEvent reaperWraith;
	public static SoundEvent hanzoShoot;
	public static SoundEvent hanzoDraw;
	public static SoundEvent hanzoSonicArrow;
	public static SoundEvent hanzoScatterArrow;
	public static SoundEvent reinhardtWeapon;
	public static SoundEvent genjiShoot;
	public static SoundEvent genjiDeflect;
	public static SoundEvent genjiDeflectHit;
	public static SoundEvent genjiStrike;
	public static SoundEvent genjiJump;
	public static SoundEvent tracerShoot;
	public static SoundEvent tracerBlink;
	public static SoundEvent mccreeShoot;
	public static SoundEvent mccreeFlashbang;
	public static SoundEvent mccreeRoll;
	public static SoundEvent soldier76Shoot;
	public static SoundEvent soldier76Helix;
	public static SoundEvent bastionShoot0;
	public static SoundEvent bastionShoot1;
	public static SoundEvent bastionReload;
	public static SoundEvent bastionTurretReload;
	public static SoundEvent meiShoot;
	public static SoundEvent meiIcicleShoot;
	public static SoundEvent meiFreeze;
	public static SoundEvent meiUnfreeze;
	public static SoundEvent widowmakerScopedShoot;
	public static SoundEvent widowmakerUnscopedShoot;
	public static SoundEvent widowmakerCharge;
	public static SoundEvent mercyShoot;
	public static SoundEvent mercyHeal;
	public static SoundEvent mercyDamage;
	public static SoundEvent mercyHover;
	public static SoundEvent mercyAngel;
	public static SoundEvent mercyAngelVoice;
	public static SoundEvent mercyBeamStart;
	public static SoundEvent mercyBeamDuring;
	public static SoundEvent mercyBeamStop;
	public static SoundEvent junkratShoot;
	public static SoundEvent junkratLaugh;
	public static SoundEvent junkratDeath;
	public static SoundEvent junkratGrenadeBounce;
	public static SoundEvent junkratGrenadeExplode;
	public static SoundEvent[] junkratGrenadeTick = new SoundEvent[4];
	public static SoundEvent junkratTrapThrow;
	public static SoundEvent junkratTrapLand;
	public static SoundEvent junkratTrapTrigger;
	public static SoundEvent junkratTrapBreak;
	public static SoundEvent junkratTrapTriggerOwner;
	public static SoundEvent junkratTrapTriggerVoice;
	public static SoundEvent junkratTrapPlacedVoice;
	public static SoundEvent sombraShoot;
	public static SoundEvent sombraInvisibleStart;
	public static SoundEvent sombraInvisibleStop;
	public static SoundEvent sombraInvisibleVoice;
	public static SoundEvent sombraTranslocatorLand;
	public static SoundEvent sombraTranslocatorThrow;
	public static SoundEvent sombraTranslocatorDuring;
	public static SoundEvent sombraTranslocatorTeleport;
	public static SoundEvent widowmakerMineThrow;
	public static SoundEvent widowmakerMineLand;
	public static SoundEvent widowmakerMineTrigger;
	public static SoundEvent widowmakerMineDestroyed;
	public static SoundEvent junkratMineThrow;
	public static SoundEvent junkratMineLand;
	public static SoundEvent junkratMineExplode;
	public static SoundEvent bastionReconfigure0;
	public static SoundEvent bastionReconfigure1;
	public static SoundEvent meiCrystalStart;
	public static SoundEvent meiCrystalStop;
	public static SoundEvent reinhardtStrikeThrow;
	public static SoundEvent reinhardtStrikeDuring;
	public static SoundEvent reinhardtStrikeCollide;

	public static void preInit() {
		for (EnumHero hero : EnumHero.values())
			if (hero != EnumHero.HANZO && hero != EnumHero.REINHARDT)
				hero.reloadSound = registerSound(hero.name.toLowerCase()+"_reload"+(hero.equals(EnumHero.BASTION) ? "_0" : ""));

		guiHover = registerSound("gui_hover");
		for (int i=2; i<7; ++i)
			multikill[i-2] = registerSound("multikill_"+i);
		kill = registerSound("kill");
		headshot = registerSound("headshot");
		hurt = registerSound("hurt");
		abilityRecharge = registerSound("ability_recharge");
		abilityMultiRecharge = registerSound("ability_multi_recharge");
		abilityNotReady = registerSound("ability_not_ready");
		wallClimb = registerSound("wall_climb");
		anaShoot = registerSound("ana_shoot");
		anaHeal = registerSound("ana_heal");
		anaSleepShoot = registerSound("ana_sleep_shoot");
		anaSleepHit = registerSound("ana_sleep_hit");
		anaSleepVoice = registerSound("ana_sleep_voice");
		reaperShoot = registerSound("reaper_shoot");
		reaperTeleportStart = registerSound("reaper_teleport_start");
		reaperTeleportDuring = registerSound("reaper_teleport_during");
		reaperTeleportStop = registerSound("reaper_teleport_stop");
		reaperTeleportFinal = registerSound("reaper_teleport_final");
		reaperTeleportVoice = registerSound("reaper_teleport_voice");
		reaperWraith = registerSound("reaper_wraith");
		hanzoShoot = registerSound("hanzo_shoot");
		hanzoDraw = registerSound("hanzo_draw");
		hanzoSonicArrow = registerSound("hanzo_sonic_arrow");
		hanzoScatterArrow = registerSound("hanzo_scatter_arrow");
		reinhardtWeapon = registerSound("reinhardt_weapon");
		genjiShoot = registerSound("genji_shoot");
		genjiDeflect = registerSound("genji_deflect");
		genjiDeflectHit = registerSound("genji_deflect_hit");
		genjiStrike = registerSound("genji_strike");
		genjiJump = registerSound("genji_jump");
		tracerShoot = registerSound("tracer_shoot");
		tracerBlink = registerSound("tracer_blink");
		mccreeShoot = registerSound("mccree_shoot");
		mccreeFlashbang = registerSound("mccree_flashbang");
		mccreeRoll = registerSound("mccree_roll");
		soldier76Shoot = registerSound("soldier76_shoot");
		soldier76Helix = registerSound("soldier76_helix");
		bastionReload = EnumHero.BASTION.reloadSound;
		bastionTurretReload = registerSound("bastion_reload_1");
		meiShoot = registerSound("mei_shoot_0");
		meiIcicleShoot = registerSound("mei_shoot_1");
		meiFreeze = registerSound("mei_freeze");
		meiUnfreeze = registerSound("mei_unfreeze");
		widowmakerUnscopedShoot = registerSound("widowmaker_shoot_0");
		widowmakerScopedShoot = registerSound("widowmaker_shoot_1");
		widowmakerCharge = registerSound("widowmaker_charge");
		mercyShoot = registerSound("mercy_shoot");
		mercyHeal = registerSound("mercy_heal");
		mercyDamage = registerSound("mercy_damage");
		mercyHover = registerSound("mercy_hover");
		mercyAngel = registerSound("mercy_angel");
		mercyAngelVoice = registerSound("mercy_angel_voice");
		mercyBeamStart = registerSound("mercy_beam_start");
		mercyBeamDuring = registerSound("mercy_beam_during");
		mercyBeamStop = registerSound("mercy_beam_stop");
		junkratShoot = registerSound("junkrat_shoot");
		junkratLaugh = registerSound("junkrat_laugh");
		junkratDeath = registerSound("junkrat_death");
		junkratGrenadeBounce = registerSound("junkrat_grenade_bounce");
		junkratGrenadeExplode = registerSound("junkrat_grenade_explode");
		for (int i=0; i<junkratGrenadeTick.length; ++i)
			junkratGrenadeTick[i] = registerSound("junkrat_grenade_tick_"+i);
		junkratTrapThrow = registerSound("junkrat_trap_throw");
		junkratTrapLand = registerSound("junkrat_trap_land");
		junkratTrapTrigger = registerSound("junkrat_trap_trigger");
		junkratTrapBreak = registerSound("junkrat_trap_break");
		junkratTrapTriggerOwner = registerSound("junkrat_trap_trigger_owner");
		junkratTrapTriggerVoice = registerSound("junkrat_trap_trigger_voice");
		junkratTrapPlacedVoice = registerSound("junkrat_trap_placed_voice");

		sombraShoot = registerSound("sombra_shoot");
		sombraInvisibleStart = registerSound("sombra_invisible_start");
		sombraInvisibleStop = registerSound("sombra_invisible_stop");
		sombraInvisibleVoice = registerSound("sombra_invisible_voice");
		sombraTranslocatorLand = registerSound("sombra_translocator_land");
		sombraTranslocatorThrow = registerSound("sombra_translocator_throw");
		sombraTranslocatorDuring = registerSound("sombra_translocator_during");
		sombraTranslocatorTeleport = registerSound("sombra_translocator_teleport");
		widowmakerMineThrow = registerSound("widowmaker_mine_throw");
		widowmakerMineLand = registerSound("widowmaker_mine_land");
		widowmakerMineTrigger = registerSound("widowmaker_mine_trigger");
		widowmakerMineDestroyed = registerSound("widowmaker_mine_destroyed");
		junkratMineThrow = registerSound("junkrat_mine_throw");
		junkratMineLand = registerSound("junkrat_mine_land");
		junkratMineExplode = registerSound("junkrat_mine_explode");
		bastionShoot0 = registerSound("bastion_shoot_0");
		bastionShoot1 = registerSound("bastion_shoot_1");
		bastionReconfigure0 = registerSound("bastion_reconfigure_0");
		bastionReconfigure1 = registerSound("bastion_reconfigure_1");
		meiCrystalStart = registerSound("mei_crystal_start");
		meiCrystalStop = registerSound("mei_crystal_stop");
		reinhardtStrikeThrow = registerSound("reinhardt_strike_throw");
		reinhardtStrikeDuring = registerSound("reinhardt_strike_during");
		reinhardtStrikeCollide = registerSound("reinhardt_strike_collide");
	}

	private static SoundEvent registerSound(String soundName) {
		ResourceLocation loc = new ResourceLocation(Minewatch.MODID, soundName);
		SoundEvent sound = new SoundEvent(loc);
		GameRegistry.register(sound, loc);
		return sound;
	}
}