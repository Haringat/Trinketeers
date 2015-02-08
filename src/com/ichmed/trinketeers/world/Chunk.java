package com.ichmed.trinketeers.world;

import java.util.HashMap;

public class Chunk
{
	public static HashMap<String, Chunk> chunks = new HashMap<>();

	public static final int chunkSize = 16;
	public static final int clusterSize = 4;
	public int[] tiles = new int[chunkSize * chunkSize];

	public static Chunk createNewChunk(int x, int y, int z)
	{
		Chunk c = new Chunk();
		c.populate();
		chunks.put(getHashString(x, y, z), c);
		return c;
	}

	public void populate()
	{
		for(int i =0; i < chunkSize * chunkSize; i++) tiles[i] = 2;
	}

	public static String getHashString(int x, int y, int z)
	{
		return x + "x" + y + "x" + z;
	}
	
	public static void setTile(int x, int y, int z, int id)
	{

		int chunkX = x / chunkSize;
		int chunkY = y / chunkSize;

		if (x < 0) chunkX--;
		if (y < 0) chunkY--;

		Chunk c = getChunk(chunkX, chunkY, z);
		int xTemp = x % chunkSize;
		if (xTemp < 0) xTemp += chunkSize;
		int yTemp = y % chunkSize;
		if (yTemp < 0) yTemp += chunkSize;
		c.setTileInChunk(xTemp, yTemp, id);
	}

	private void setTileInChunk(int x, int y, int id)
	{
		this.tiles[chunkSize * y + x] = id;
	}

	public static int getTile(int x, int y, int z)
	{
		int chunkX = x / chunkSize;
		int chunkY = y / chunkSize;

		if (x < 0) chunkX--;
		if (y < 0) chunkY--;

		if (chunks.get(getHashString(chunkX, chunkY, z)) == null) createNewChunk(chunkX, chunkY, 0);

		int xTemp = x % chunkSize;
		if (xTemp < 0) xTemp += chunkSize;
		int yTemp = y % chunkSize;
		if (yTemp < 0) yTemp += chunkSize;
		return getChunk(chunkX, chunkY, z).getTileFromChunk(xTemp, yTemp);
	}

	private int getTileFromChunk(int x, int y)
	{
		return this.tiles[chunkSize * y + x];
	}

	public static Chunk getChunk(int x, int y, int z)
	{
		Chunk c = chunks.get(getHashString(x, y, z));
		if (c == null) return createNewChunk(x, y, z);
		return c;
	}

	public void unloadCluster(int x, int y, int z)
	{
		for (int i = 0; i < clusterSize; i++)
			for (int j = 0; j < clusterSize; j++)
				for (int k = 0; k < clusterSize; k++)
				{
					chunks.put(getHashString(i + x * clusterSize, j + y * clusterSize, k + z * clusterSize), null);
				}
	}
}
