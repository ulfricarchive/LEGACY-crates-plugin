package com.ulfric.spigot.crates;

import com.google.common.collect.ImmutableList;

import org.bukkit.Location;
import org.bukkit.block.Block;

import com.ulfric.commons.bean.Bean;
import com.ulfric.commons.naming.Named;
import com.ulfric.commons.spigot.weighted.WeightedTable;
import com.ulfric.commons.spigot.weighted.WeightedValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public final class Crate extends Bean implements Named {
	
	public static Builder builder()
	{
		return new Builder();
	}
	
	public static final class Builder implements org.apache.commons.lang3.builder.Builder<Crate>
	{
		
		private String name;
		private Collection<Reward> rewards;
		private Collection<Location> locations;
		
		Builder()
		{
			
		}
		
		@Override
		public Crate build()
		{
			Objects.requireNonNull(this.name, "name");
			Objects.requireNonNull(this.rewards, "rewards");
			Objects.requireNonNull(this.locations, "locations");
			
			return new Crate(this.name, this.rewards, this.locations);
		}
		
		public Builder setName(String name)
		{
			this.name = name;
			return this;
		}
		
		public Builder setRewards(Collection<Reward> rewards)
		{
			this.rewards = rewards;
			return this;
		}
		
		public Builder setRewards(Reward... rewards)
		{
			return this.setRewards(new ArrayList<>(ImmutableList.copyOf(rewards)));
		}
		
		public Builder setLocations(Collection<Location> locations)
		{
			this.locations = locations;
			return this;
		}
		
	}
	
	private final String name;
	private final WeightedTable<Reward> rewards;
	private final Set<Location> locations;
	
	Crate(String name, Collection<Reward> rewards, Collection<Location> locations)
	{
		this.name = name;
		this.rewards = WeightedTable.<Reward>builder()
				.addAll(
						rewards.stream()
								.map(reward -> new WeightedValue<>(reward, reward.getChance()))
								.collect(Collectors.toList())
				)
				.build();
		this.locations = new HashSet<>(locations);
	}
	
	@Override
	public String getName()
	{
		return this.name;
	}

	public WeightedTable<Reward> getRewards()
	{
		return this.rewards;
	}
	
	public Reward randomReward()
	{
		return this.rewards.nextValue();
	}
	
	public Set<Location> getLocations()
	{
		return new HashSet<>(this.locations);
	}
	
	public boolean isLocation(Location location)
	{
		return this.locations.contains(location);
	}
	
	public boolean isLocation(Block block)
	{
		return this.isLocation(block.getLocation());
	}
	
	public boolean addLocation(Location location)
	{
		return this.locations.add(location);
	}
	
	public boolean removeLocation(Location location)
	{
		return this.locations.remove(location);
	}
	
}
