package com.ichmed.trinketeers.world;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import com.ichmed.trinketeers.entity.Entity;
import com.ichmed.trinketeers.entity.Player;
import com.ichmed.trinketeers.entity.Torch;
import com.ichmed.trinketeers.entity.mob.FlameElemental;
import com.ichmed.trinketeers.entity.mob.Zombie;
import com.ichmed.trinketeers.util.AxisAllignedBoundingBox;
import com.ichmed.trinketeers.util.render.GLHelper;
import com.ichmed.trinketeers.util.render.GraphicSorterYAxis;
import com.ichmed.trinketeers.util.render.IGraphic;
import com.ichmed.trinketeers.util.render.IWorldGraphic;
import com.ichmed.trinketeers.util.render.TextureLibrary;
import com.ichmed.trinketeers.util.render.TrueTypeFont;
import com.ichmed.trinketeers.util.render.light.ILight;
import com.ichmed.trinketeers.util.render.light.LightRenderer;
import com.ichmed.trinketeers.world.tile.Tile;

public class World
{
	public Player player = new Player();
	List<IGraphic> uiGraphics = new ArrayList<>();
	public Level currentLevel;
	public int currentHeight = 0;

	public final String name = "world_0";

	public World()
	{
		currentLevel = new Level(this, currentHeight);
		currentLevel.init();
//		LightRenderer.setAmbientLight(1f, 1f, 1f);
		 LightRenderer.setAmbientLight(0.0f, 0.0f, 0.0f);
		spawn(player);

		uiGraphics.add(new IGraphic()
		{

			@Override
			public void render()
			{
				boolean b1 = player.currentSpellLeft != null;
				boolean b2 = player.currentSpellRight != null;

				if (b1) GLHelper.renderTexturedQuad(-1, 0.8f, 0.2f, 0.2f, "scroll");
				if (b2) GLHelper.renderTexturedQuad(0.8f, 0.8f, 0.2f, 0.2f, "scroll");

				if (b1) GLHelper.renderTexturedQuad(-0.79f, 0.8f, 0.02f, 0.2f - Math.max(0, ((float) player.shotCooldownLeft / (float) player.currentSpellLeft.cooldown) * 0.2f), "spellCooldownBar");
				if (b2) GLHelper.renderTexturedQuad(0.78f, 0.8f, 0.02f, 0.2f - Math.max(0, ((float) player.shotCooldownRight / (float) player.currentSpellRight.cooldown) * 0.2f), "spellCooldownBar");

				if (b1)
				{
					GLHelper.renderTexturedQuad(-0.975f, 0.825f, 0.15f, 0.15f, player.currentSpellLeft.element.toLowerCase() + "Projectile");
				}

				if (b2)
				{
					GLHelper.renderTexturedQuad(0.825f, 0.825f, 0.15f, 0.15f, player.currentSpellRight.element.toLowerCase() + "Projectile");
				}
				if (b1)
				{
					GLHelper.renderText(-0.98f, 0.76f, player.currentSpellLeft.getName());
					GLHelper.renderText(-0.98f, 0.70f, "" + (int) player.currentSpellLeft.getManaCost());
				}
				if (b2)
				{
					GLHelper.renderText(0.98f, 0.76f, player.currentSpellRight.getName(), 0.001f, 0.001f, TrueTypeFont.ALIGN_RIGHT);
					GLHelper.renderText(0.98f, 0.70f, "" + (int) player.currentSpellRight.getManaCost(), 0.001f, 0.001f, TrueTypeFont.ALIGN_RIGHT);

				}
			}
		});

		uiGraphics.add(new IGraphic()
		{

			@Override
			public void render()
			{
				float mana = player.mana / player.maxMana;
				float start = -0.5f;
				GLHelper.renderTexturedQuad(start, 0.92f, 1, 0.05f, "manaBarEmpty");
				GLHelper.renderTexturedQuad(start + (1 - mana) / 2f, 0.92f, mana, 0.05f, "manaBarFull");
				GLHelper.renderText(0, 0.9f, "" + (int) player.mana, 0.002f, 0.002f, TrueTypeFont.ALIGN_CENTER);
			}
		});

		uiGraphics.add(new IGraphic()
		{

			@Override
			public void render()
			{
				float health = (player.health / player.maxHealth);
				float start = -0.5f;
				GLHelper.renderTexturedQuad(start, 0.85f, 1, 0.05f, "healthBarEmpty");
				GLHelper.renderTexturedQuad(start + (1 - health) / 2f, 0.85f, health, 0.05f, "healthBarFull");
				GLHelper.renderText(0, 0.83f, "" + (int) player.health, 0.002f, 0.002f, TrueTypeFont.ALIGN_CENTER);
			}
		});
	}

	public void nextLevel()
	{
		this.currentHeight++;
		this.currentLevel = new Level(this, this.currentHeight);
		this.currentLevel.init();
		spawn(player);
	}

	public void generateZombies(int amount)
	{
		for (int i = 0; i < amount; i++)
			spawn((new Zombie()).setPosition(new Vector2f((float) Math.random() - 0.5f, (float) Math.random() * 0.5f)));
	}

	public void generateFlameElementals(int amount)
	{
		for (int i = 0; i < amount; i++)
			spawn((new FlameElemental()).setPosition(new Vector2f((float) Math.random() - 0.5f, (float) Math.random() * 0.5f)));
	}

	public void generateTorches(int amount)
	{
		for (int i = 0; i < amount; i++)
			spawn((new Torch()).setPosition(new Vector2f((float) (Math.random() - 0.5) * 4, (float) (Math.random() - 0.5) * 4)));
	}

	public void addLight(ILight l)
	{
		if (!currentLevel.lights.contains(l)) currentLevel.lights.add(l);
	}

	public void removeLight(ILight l)
	{
		currentLevel.lights.remove(l);
	}

	public void tick()
	{
		if (currentLevel.entities.size() > 0) currentLevel.entitiesNextTick = new ArrayList<Entity>(currentLevel.entities);
		for (Entity e : currentLevel.entities)
			e.tick(this);
		currentLevel.entities = new ArrayList<>(currentLevel.entitiesNextTick);
		render();
	}

	public void render()
	{
		// GLHelper.renderBackground(this);
		glPushMatrix();
		Vector2f v = player.getCenter();
		glTranslatef(-v.x, -v.y, 0);
		renderChunks(false);
		currentLevel.worldGraphics.sort(new GraphicSorterYAxis());
		for (IWorldGraphic g : currentLevel.worldGraphics)
			g.render(this);
		renderChunks(true);
		glPopMatrix();
		LightRenderer.renderLights(this, currentLevel.lights);
		TextureLibrary.rebind();
		for (IGraphic g : uiGraphics)
			g.render();
	}

	private void renderChunks(boolean b)
	{
		for (int i = -1; i < 17; i++)
			for (int j = -1; j < 17; j++)
			{
				int x = i + (int) ((player.getCenter().x - 1) * 8);
				int y = j + (int) ((player.getCenter().y - 1) * 8);
				Tile t = Tile.tiles[Chunk.getTile(x, y, currentHeight)];
				if (t.renderInFront(this, x, y) == b) t.render(this, x, y);
			}
	}

	public boolean removeEntity(Entity e)
	{
		currentLevel.entitiesNextTick.remove(e);
		currentLevel.worldGraphics.remove(e);
		currentLevel.shadows.remove(e);
		return currentLevel.entitiesNextTick.contains(e);
	}

	public List<Entity> getListOfIntersectingEntities(Entity e, boolean onlySolids)
	{
		return getListOfIntersectingEntities(e.getColissionBox(), e.getEntitiesExcludedFromCollision(), onlySolids);
	}

	public List<Entity> getListOfIntersectingEntities(AxisAllignedBoundingBox aabb, boolean onlySolids)
	{
		return getListOfIntersectingEntities(aabb, null, onlySolids);
	}

	public List<Entity> getListOfIntersectingEntities(AxisAllignedBoundingBox aabb, List<Entity> exclude, boolean onlySolids)
	{
		ArrayList<Entity> list = new ArrayList<>();
		for (Entity f : currentLevel.entitiesNextTick)
			if ((exclude == null || !(exclude.contains(f))) && AxisAllignedBoundingBox.doAABBsIntersect(aabb, f.getColissionBox())) if (!onlySolids || f.isSolid) list.add(f);
		return list;
	}

	public boolean spawn(Entity e)
	{
		return this.spawn(e, true);
	}

	public boolean spawn(Entity e, boolean checkForColission)
	{
		return this.spawn(e, checkForColission, true);
	}

	public boolean spawn(Entity e, boolean checkForColission, boolean checkSolidsOnly)
	{
		return this.currentLevel.spawn(e, checkForColission, checkSolidsOnly);
	}

	public List<Entity> getEntitiesByDistance(Entity source, float maxDistance)
	{
		List<Entity> l = new ArrayList<>(this.currentLevel.entitiesNextTick);
		l.sort(new Comparator<Entity>()
		{

			@Override
			public int compare(Entity o1, Entity o2)
			{
				float distA = new Vector2f(source.position.x - o1.position.x, source.position.y - o1.position.y).length();
				float distB = new Vector2f(source.position.x - o2.position.x, source.position.y - o2.position.y).length();
				return distA < distB ? -1 : distA == distB ? 0 : 1;
			}
		});
		List<Entity> temp = new ArrayList<>(l);
		l.remove(source);
		if (maxDistance > 0) for (Entity e : temp)
			if (new Vector2f(source.position.x - e.position.x, source.position.y - e.position.y).length() > maxDistance) l.remove(e);
		return l;
	}

	public Entity getClosestEntityToSource(Entity source, float maxDistance, Class<? extends Entity> entityClass)
	{
		return getClosestEntityToSource(source, maxDistance, entityClass, true);
	}

	public Entity getClosestEntityToSource(Entity source, float maxDistance, Class<? extends Entity> entityClass, boolean alliveOnly)
	{
		List<Entity> l = getEntitiesByDistance(source, maxDistance);
		for (Entity e : l)
			if (!e.isDead && entityClass.isAssignableFrom(e.getClass())) return e;
		return null;
	}

	public boolean isPositionStuckInGeometry(AxisAllignedBoundingBox predictedPosition, int height)
	{
		int x1 = (int) (predictedPosition.pos.x * 8);
		int x2 = (int) ((predictedPosition.pos.x + predictedPosition.size.x) * 8);
		int y1 = (int) (predictedPosition.pos.y * 8);
		int y2 = (int) ((predictedPosition.pos.y + predictedPosition.size.y) * 8);

		if (predictedPosition.pos.x <= 0) x1--;
		if ((predictedPosition.pos.x + predictedPosition.size.x) > 0) x2++;
		if (predictedPosition.pos.y < 0) y1--;
		if((predictedPosition.pos.y + predictedPosition.size.y) > 0)y2++;

		int i = x1;
		do
		{
			int j = y1;
			do
			{

				if (isPointStuckInGeometry(new Vector3f(i, j, height))) return true;
				j++;
			} while (j < y2);
			i++;
		} while (i < x2);

		return false;
	}

	public boolean isPointStuckInGeometry(Vector3f point)
	{
		int pointX = (int) (point.x);
		int pointY = (int) (point.y);
		return Tile.tiles[Chunk.getTile(pointX, pointY, (int) point.z)].massive;
	}
}
