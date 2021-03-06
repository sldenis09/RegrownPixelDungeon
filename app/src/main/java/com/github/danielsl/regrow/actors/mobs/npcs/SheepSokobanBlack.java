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
package com.github.danielsl.regrow.actors.mobs.npcs;

import com.github.danielsl.regrow.Dungeon;
import com.github.danielsl.regrow.actors.Actor;
import com.github.danielsl.regrow.actors.buffs.Buff;
import com.github.danielsl.regrow.effects.CellEmitter;
import com.github.danielsl.regrow.effects.Speck;
import com.github.danielsl.regrow.effects.particles.ShadowParticle;
import com.github.danielsl.regrow.levels.Level;
import com.github.danielsl.regrow.sprites.SokobanBlackSheepSprite;
import com.watabou.utils.Random;

public class SheepSokobanBlack extends NPC {

	private static final String[] QUOTES = { "Baa!", "Baa?", "Baa.",
	"Baa..." };

{
name = "sheep";
spriteClass = SokobanBlackSheepSprite.class;
}


@Override
protected boolean act() {
	throwItem();
	return super.act();
}

@Override
public void damage(int dmg, Object src) {
}
@Override
public void add(Buff buff) {
}

@Override
public String description() {
return "This is a magic sheep. What's so magical about it? You can't kill it. "
		+ "It will stand anywhere you move it, all the while chewing cud with a blank stare.";
}

@Override
public void interact() {
	int traps = Dungeon.level.countFleeceTraps(pos, 5);	
	int newPos = -1;
	int curPos = pos;
	int count = 100;
	int dist = 6;
	boolean moved = false;
	
	if (traps>0){
	
	  do {
		 newPos = Dungeon.level.randomRespawnCellSheep(pos, 5);
		 dist = Level.distance(newPos, pos);
		 if (count-- <= 0) {
			break;
		 }
	  } while (newPos == -1);
	
	}

	if (newPos == -1) {
		
		
		yell(Random.element(QUOTES));
		//yell("pos = " + dist);		
		destroy();
		sprite.killAndErase();
		sprite.emitter().burst(ShadowParticle.UP, 5);
		moved=true;
  
	} else {
		yell("BAA!");
		//yell("pos = " + dist);
		Actor.freeCell(pos);
		CellEmitter.get(pos).start(Speck.factory(Speck.LIGHT), 0.2f, 3);
		pos = newPos;
		move(pos);
		moved=true;
	}	

	if(moved){
	      Dungeon.hero.sprite.move(Dungeon.hero.pos, curPos);
		  Dungeon.hero.move(curPos);
		}
	
	Dungeon.hero.spend(1 / Dungeon.hero.speed());
	Dungeon.hero.busy();    
 }

}
