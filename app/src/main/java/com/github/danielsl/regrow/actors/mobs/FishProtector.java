/*
 * Pixel Dungeon
 * Copyright (C) 2012-2014  Oleg Dolya
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.github.danielsl.regrow.actors.mobs;

import java.util.HashSet;

import com.github.danielsl.regrow.Dungeon;
import com.github.danielsl.regrow.ResultDescriptions;
import com.github.danielsl.regrow.actors.Char;
import com.github.danielsl.regrow.actors.buffs.Invisibility;
import com.github.danielsl.regrow.effects.particles.SparkParticle;
import com.github.danielsl.regrow.items.VioletDewdrop;
import com.github.danielsl.regrow.levels.Level;
import com.github.danielsl.regrow.levels.traps.LightningTrap;
import com.github.danielsl.regrow.mechanics.Ballistica;
import com.github.danielsl.regrow.sprites.CharSprite;
import com.github.danielsl.regrow.sprites.FishProtectorSprite;
import com.github.danielsl.regrow.utils.GLog;
import com.github.danielsl.regrow.utils.Utils;
import com.watabou.noosa.Camera;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class FishProtector extends Mob implements Callback {

	private static final float TIME_TO_ZAP = 2f;

	private static final String TXT_LIGHTNING_KILLED = "%s's lightning bolt killed you...";

	{
		name = "flying protector";
		spriteClass = FishProtectorSprite.class;

		EXP = 15;
		state = HUNTING;
		flying = true;
		
		HP = HT = 75;
		defenseSkill = 25;
		
		loot = new VioletDewdrop();
		lootChance = 1f;
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange(12, 20);
	}

	@Override
	public int attackSkill(Char target) {
		return 25;
	}

	@Override
	public int dr() {
		return 15;
	}

		
	@Override
	protected boolean canAttack(Char enemy) {
		return Ballistica.cast(pos, enemy.pos, false, true) == enemy.pos;
	}

	@Override
	protected boolean doAttack(Char enemy) {

		if (Level.distance(pos, enemy.pos) <= 1) {

			return super.doAttack(enemy);

		} else {

			boolean visible = Level.fieldOfView[pos]
					|| Level.fieldOfView[enemy.pos];
			if (visible) {
				((FishProtectorSprite) sprite).zap(enemy.pos);
			}

			spend(TIME_TO_ZAP);

			if (hit(this, enemy, true)) {
				int dmg = Random.Int(25, 60);
				if (Level.water[enemy.pos] && !enemy.flying) {
					dmg *= 1.5f;
				}
				enemy.damage(dmg, LightningTrap.LIGHTNING);

				enemy.sprite.centerEmitter().burst(SparkParticle.FACTORY, 3);
				enemy.sprite.flash();

				if (enemy == Dungeon.hero) {

					Camera.main.shake(2, 0.3f);

					if (!enemy.isAlive()) {
						Dungeon.fail(Utils.format(ResultDescriptions.MOB,
								Utils.indefinite(name)));
						GLog.n(TXT_LIGHTNING_KILLED, name);
					}
				}
			} else {
				enemy.sprite
						.showStatus(CharSprite.NEUTRAL, enemy.defenseVerb());
			}

			return !visible;
		}
	}

	@Override
	public void call() {
		next();
	}

	@Override
	public void notice() {
		super.notice();
		yell("Leave this place!");
	}
	
	@Override
	public String description() {
		return "This guardian protects the cave from overfishing. ";
	}

	private static final HashSet<Class<?>> RESISTANCES = new HashSet<Class<?>>();
	static {
		RESISTANCES.add(LightningTrap.Electricity.class);
		RESISTANCES.add(Invisibility.class);
	}

	@Override
	public HashSet<Class<?>> resistances() {
		return RESISTANCES;
	}
}
