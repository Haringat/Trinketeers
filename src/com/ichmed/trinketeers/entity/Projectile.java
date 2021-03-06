package com.ichmed.trinketeers.entity;

import java.util.List;

import org.lwjgl.util.vector.Vector2f;

import com.ichmed.trinketeers.savefile.data.ElementData;
import com.ichmed.trinketeers.spell.Spell;
import com.ichmed.trinketeers.world.World;

public class Projectile extends Entity
{
	private Entity controller;
	private String element;
	private Spell child, trailSpell;
	private float wobble;
	public int trailDelay;
	public int maxTrailDelay;
	public int travelBeforeStandStill = -1;
	public boolean destroyOnImpact = true;

	public Projectile(World w, Vector2f size, String element, Spell child, float wobble, Spell trailSpell)
	{
		super(w);
		this.size = new Vector2f(size);
		this.element = element;
		this.isSolid = false;
		this.child = child;
		this.trailSpell = trailSpell;
		this.wobble = wobble;
		this.despawnCountDown = 1;
		this.renderSize = null;
	}

	@Override
	public void tick(World world)
	{
		super.tick(world);
		if(world.isPositionStuckInGeometry(this.getColissionBox(), (int)this.position.z)) this.kill(world);
		this.rotation = (float) Math.toDegrees(Math.atan(this.direction.y / this.direction.x));
		if (this.direction.x < 0) rotation += 180;
		if (!this.isDead)
		{
			this.travelBeforeStandStill--;
			if (this.travelBeforeStandStill == 0) this.speed = 0;
			float s = this.direction.length() * (1 - ElementData.elements.get(element).getDensity());
			this.direction.x += ((float) Math.random() - .5f) * wobble;
			this.direction.y += ((float) Math.random() - .5f) * wobble;
			this.direction.normalise().scale(s);
			List<Entity> hit = world.getListOfIntersectingEntities(this, true, (int)this.position.z);
			for (Entity e : hit)
			{
				if (e.isVulnerable)
				{
					e.damage(this.size.x * ElementData.elements.get(element).getBaseDamage());
					if (e.isMoveable)
					{
						e.direction = this.direction;
						e.speed = ElementData.elements.get(element).getDensity();
						e.stun = 2;
						if (this.destroyOnImpact) this.kill(world);

					}
					break;
				} else if (e.isMoveable)
				{
					e.direction = this.direction;
					e.speed = ElementData.elements.get(element).getDensity();
					e.stun = 2;
					if (this.destroyOnImpact) this.kill(world);
					break;

				}
				if(e.isSolid)this.kill(world);
			}

			if (this.trailSpell != null && this.trailDelay == 0)
			{
				 trailSpell.cast(world, this.controller, this.getCenter().x,
				 this.getCenter().y, direction);
				this.trailDelay = this.maxTrailDelay;
			}
			trailDelay--;
		}
	}

	public void setController(Entity controller)
	{
		this.controller = controller;
	}
	
	

	@Override
	public String getTextureForState(World w)
	{
		return this.element.toLowerCase() + "Projectile";
	}

	@Override
	protected void onDeath(World world)
	{
		if (this.child != null) this.child.cast(world, this.controller, this.getCenter().x, this.getCenter().y, this.direction);
		super.onDeath(world);
	}

	@Override
	public List<Entity> getEntitiesExcludedFromCollision()
	{
		List<Entity> l = super.getEntitiesExcludedFromCollision();
		l.add(this.controller);
		return l;
	}
}
