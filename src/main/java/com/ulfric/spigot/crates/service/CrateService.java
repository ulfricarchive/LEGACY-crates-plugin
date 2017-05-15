package com.ulfric.spigot.crates.service;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.ulfric.commons.spigot.data.Data;
import com.ulfric.commons.spigot.data.DataSection;
import com.ulfric.commons.spigot.data.DataStore;
import com.ulfric.commons.spigot.data.PersistentData;
import com.ulfric.commons.spigot.item.ItemUtils;
import com.ulfric.commons.spigot.location.LocationUtils;
import com.ulfric.commons.spigot.text.Text;
import com.ulfric.dragoon.container.Container;
import com.ulfric.dragoon.initialize.Initialize;
import com.ulfric.dragoon.inject.Inject;
import com.ulfric.plugin.platform.data.PlayerData;
import com.ulfric.spigot.crates.Crate;
import com.ulfric.spigot.crates.Reward;
import com.ulfric.spigot.crates.account.CrateAccount;
import com.ulfric.spigot.crates.account.PersistentCrateAccount;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CrateService implements Crates {
	
	private static final String CRATE_NAME_PLACEHOLDER = Text.getService().getPlainMessage("crate-name-placeholder");
	
	@Inject
	private Container owner;
	
	private final Map<String, Crate> crates = new HashMap<>();
	private final Map<Location, Crate> locationCrates = new HashMap<>();
	private final Map<UUID, CrateAccount> accounts = new ConcurrentHashMap<>();
	
	private DataStore folder;
	private DataStore playerData;
	
	private ItemStack crateBlock;
	
	@Initialize
	private void initialize()
	{
		this.playerData = PlayerData.getPlayerData(this.owner).getDataStore("crates");
		
		this.folder = Data.getDataStore(this.owner).getDataStore("crates");
		this.folder.loadAllData().forEach(this::loadCrate);
	}
	
	private void loadSettings()
	{
		PersistentData data = Data.getDataStore(this.owner).getDefault();
		
		this.crateBlock = ItemUtils.deserializeItem(data.getString("crateBlock"));
	}
	
	private void loadCrate(PersistentData data)
	{
		String name = this.getName(data);
		List<Reward> rewards = this.getRewards(data.getSection("rewards").getSections().stream());
		List<Location> locations = this.getLocations(data.getStringList("locations"));
		
		Crate crate = Crate.builder()
				.setName(name)
				.setRewards(rewards)
				.setLocations(locations)
				.build();
		
		locations.forEach(location -> this.locationCrates.put(location, crate));
		
		this.crates.put(name, crate);
	}
	
	private String getName(PersistentData data)
	{
		String name = data.getString("name");
		
		if (name == null)
		{
			name = data.getName();
		}
		
		return name;
	}
	
	private List<Reward> getRewards(Stream<DataSection> sections)
	{
		return sections.map(this::getReward).collect(Collectors.toList());
	}
	
	private List<Location> getLocations(List<String> serializedLocations)
	{
		return serializedLocations.stream().map(LocationUtils::fromString).filter(Objects::nonNull).collect(Collectors.toList());
	}
	
	private Reward getReward(DataSection section)
	{
		String name = section.getString("name");
		int chance = section.getInt("chance");
		List<String> commands = section.getStringList("commands");
		
		return Reward.builder()
				.setName(name)
				.setChance(chance)
				.setCommands(commands)
				.build();
	}
	
	@Override
	public Crate getCrate(String name)
	{
		return this.crates.get(name);
	}
	
	@Override
	public List<Crate> getCrates()
	{
		return new ArrayList<>(this.crates.values());
	}
	
	@Override
	public Reward getRandomReward(Crate crate)
	{
		return crate.randomReward();
	}
	
	@Override
	public CrateAccount getAccount(UUID uniqueId)
	{
		return this.accounts.computeIfAbsent(uniqueId, this::createAccount);
	}
	
	private CrateAccount createAccount(UUID uniqueId)
	{
		return new PersistentCrateAccount(uniqueId, this.getCrateData(uniqueId));
	}
	
	private PersistentData getCrateData(UUID uniqueId)
	{
		return this.playerData.getData(String.valueOf(uniqueId));
	}
	
	@Override
	public Crate getCrate(Location location)
	{
		return this.locationCrates.get(location);
	}
	
	@Override
	public boolean isCrate(Location location)
	{
		return this.locationCrates.containsKey(location);
	}
	
	@Override
	public void setCrate(Crate crate, Location location)
	{
		this.locationCrates.put(location, crate);
		crate.addLocation(location);
	}
	
	@Override
	public void removeCrate(Crate crate, Location location)
	{
		this.locationCrates.remove(location);
		crate.removeLocation(location);
	}
	
	private void save(Crate crate)
	{
		PersistentData data = this.folder.getData(crate.getName());
		
		List<String> serializedLocations = crate.getLocations().stream()
				.map(LocationUtils::toString)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
		
		data.set("locations", serializedLocations);
		data.save();
	}
	
	@Override
	public ItemStack getCrateBlock(Crate crate)
	{
		ItemStack clone = this.crateBlock.clone();
		
		if (clone.hasItemMeta())
		{
			ItemMeta meta = clone.getItemMeta();
			
			String name = crate.getName();
			
			if (meta.hasDisplayName())
			{
				meta.setDisplayName(meta.getDisplayName().replace(CrateService.CRATE_NAME_PLACEHOLDER, name));
			}
			
			if (meta.hasLore())
			{
				meta.setLore(
						new ArrayList<>(meta.getLore().stream()
								.map(current -> this.nameProvider().apply(current, name))
								.collect(Collectors.toList()))
				);
			}
			
			clone.setItemMeta(meta);
		}
		
		return clone;
	}
	
	private BiFunction<String, String, String> nameProvider()
	{
		return (current, name) -> current.replace(CrateService.CRATE_NAME_PLACEHOLDER, name);
	}
	
}
