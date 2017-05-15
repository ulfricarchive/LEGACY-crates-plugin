package com.ulfric.spigot.crates.service;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import com.ulfric.commons.naming.Name;
import com.ulfric.commons.service.Service;
import com.ulfric.commons.spigot.service.ServiceUtils;
import com.ulfric.commons.version.Version;
import com.ulfric.spigot.crates.Crate;
import com.ulfric.spigot.crates.Reward;
import com.ulfric.spigot.crates.account.CrateAccount;

import java.util.List;
import java.util.UUID;

@Name("Crates")
@Version(1)
public interface Crates extends Service {
	
	public static Crates getService()
	{
		return ServiceUtils.getService(Crates.class);
	}
	
	Crate getCrate(String name);
	
	Crate getCrate(Location location);
	
	default Crate getCrate(Block block)
	{
		return this.getCrate(block.getLocation());
	}
	
	boolean isCrate(Location location);
	
	void setCrate(Crate crate, Location location);
	
	void removeCrate(Crate crate, Location location);
	
	List<Crate> getCrates();
	
	Reward getRandomReward(Crate crate);
	
	CrateAccount getAccount(UUID uniqueId);
	
	ItemStack getCrateBlock(Crate crate);
	
}
