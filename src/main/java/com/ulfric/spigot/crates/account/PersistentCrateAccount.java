package com.ulfric.spigot.crates.account;

import com.ulfric.commons.spigot.data.DataSection;
import com.ulfric.commons.spigot.data.PersistentData;
import com.ulfric.spigot.crates.Crate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PersistentCrateAccount implements CrateAccount {
	
	private final Object lock = new Object();
	private final DataSection section;
	private final UUID uniqueId;
	private final Map<String, Integer> keys = new HashMap<>();
	
	public PersistentCrateAccount(UUID uniqueId, PersistentData data)
	{
		this.uniqueId = uniqueId;
		this.section = data.createSection("crates");
		this.loadKeys();
	}
	
	private void loadKeys()
	{
		this.section.getSections().forEach(this::loadKey);
	}
	
	private void loadKey(DataSection section)
	{
		String name = section.getName();
		int keys = section.getInt("keys");
		this.keys.put(name, keys);
	}
	
	@Override
	public int getKeys(Crate crate)
	{
		return this.keys.getOrDefault(crate.getName(), 0);
	}
	
	@Override
	public void setKeys(Crate crate, int amount)
	{
		this.keys.put(crate.getName(), amount);
		this.save(crate);
	}
	
	@Override
	public void addKeys(Crate crate, int amount)
	{
		this.keys.compute(crate.getName(), (name, keys) -> (keys == null ? 0 : keys) + amount);
		this.save(crate);
	}
	
	@Override
	public void takeKeys(Crate crate, int amount)
	{
		this.keys.computeIfPresent(crate.getName(), (name, keys) ->
		{
			int key = keys - amount;
			return key > 0 ? key : 0;
		});
		this.save(crate);
	}
	
	@Override
	public UUID getUniqueId()
	{
		return this.uniqueId;
	}
	
	private void save(Crate crate)
	{
		synchronized (this.lock)
		{
			this.section.set(crate.getName() + ".keys", this.getKeys(crate));
		}
	}
	
}
