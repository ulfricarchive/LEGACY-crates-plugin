package com.ulfric.spigot.crates.service;

import com.ulfric.commons.spigot.data.Data;
import com.ulfric.commons.spigot.data.DataSection;
import com.ulfric.commons.spigot.data.DataStore;
import com.ulfric.commons.spigot.data.PersistentData;
import com.ulfric.dragoon.container.Container;
import com.ulfric.dragoon.initialize.Initialize;
import com.ulfric.dragoon.inject.Inject;
import com.ulfric.spigot.crates.Crate;
import com.ulfric.spigot.crates.Reward;
import com.ulfric.spigot.crates.account.CrateAccount;
import com.ulfric.spigot.crates.account.PersistentCrateAccount;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CrateService implements Crates {
	
	@Inject
	private Container owner;
	
	private final Map<String, Crate> crates = new HashMap<>();
	private final Map<UUID, CrateAccount> accounts = new ConcurrentHashMap<>();
	
	private DataStore playerData;
	
	@Initialize
	private void initialize()
	{
		// todo need to set up access for player data
		
		DataStore dataStore = Data.getDataStore(this.owner).getDataStore("crates");
		dataStore.loadAllData().forEach(this::loadCrate);
	}
	
	private void loadCrate(PersistentData data)
	{
		String name = this.getName(data);
		List<Reward> rewards = this.getRewards(data.getSection("rewards").getSections().stream());
		
		Crate crate = Crate.builder()
				.setName(name)
				.setRewards(rewards)
				.build();
		
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
	
}
