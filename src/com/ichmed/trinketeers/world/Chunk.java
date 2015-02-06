package com.ichmed.trinketeers.world;

import java.util.HashMap;

public class Chunk
{
	public static HashMap<String, Chunk> chunks = new HashMap<>();
	
	public static int chunkSize = 16;
	public int[] tiles = new int[chunkSize * chunkSize];
	
	public static void createNewChunk(int x, int y)
	{
		Chunk c = new Chunk();
		c.populate();
		chunks.put(getHashString(x, y), c);
	}
	
	public void populate()
	{
	}
	
	public static String getHashString(int x, int y)
	{
		return x + "x" + y;
	}
	
	public static int getTile(int x, int y)
	{
		int chunkX = x / chunkSize;
		int chunkY = y / chunkSize;
		
		if(x < 0) chunkX--;
		if(y < 0) chunkY--;
		
		if(chunks.get(getHashString(chunkX, chunkY)) == null) createNewChunk(chunkX, chunkY);
		
		int xTemp = x % chunkSize;
		if(xTemp < 0) xTemp += chunkSize;
		int yTemp = y % chunkSize;
		if(yTemp < 0) yTemp += chunkSize;
		return chunks.get(getHashString(chunkX, chunkY)).getTileFromChunk(xTemp, yTemp);
	}
	
	private int getTileFromChunk(int x, int y)
	{
		return tiles[chunkSize * y + x];
	}
}
