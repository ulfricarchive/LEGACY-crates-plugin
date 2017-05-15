package com.ulfric.spigot.crates;

import com.google.common.collect.ImmutableList;

import com.ulfric.commons.bean.Bean;
import com.ulfric.commons.naming.Named;
import com.ulfric.commons.spigot.weighted.WeightedTable;
import com.ulfric.commons.spigot.weighted.WeightedValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
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
		
		Builder()
		{
			
		}
		
		@Override
		public Crate build()
		{
			Objects.requireNonNull(this.name, "name");
			Objects.requireNonNull(this.rewards, "rewards");
			
			return new Crate(this.name, this.rewards);
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
		
	}
	
	private final String name;
	private final WeightedTable<Reward> rewards;
	
	Crate(String name, Collection<Reward> rewards)
	{
		this.name = name;
		this.rewards = WeightedTable.<Reward>builder()
				.addAll(
						rewards.stream()
								.map(reward -> new WeightedValue<>(reward, reward.getChance()))
								.collect(Collectors.toList())
				)
				.build();
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
	
}
