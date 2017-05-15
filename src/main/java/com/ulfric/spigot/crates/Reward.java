package com.ulfric.spigot.crates;

import org.bukkit.entity.Player;

import com.ulfric.commons.naming.Named;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Stream;

public class Reward implements Named {
	
	public static Builder builder()
	{
		return new Builder();
	}
	
	public static final class Builder implements org.apache.commons.lang3.builder.Builder<Reward>
	{
		
		private String name;
		private int chance;
		private Collection<String> commands;
		
		Builder()
		{
			
		}
		
		@Override
		public Reward build()
		{
			Objects.requireNonNull(this.name, "name");
			Objects.requireNonNull(this.commands, "commands");
			
			return new Reward(this.name, this.chance, this.commands);
		}
		
		public Builder setName(String name)
		{
			this.name = name;
			return this;
		}
		
		public Builder setChance(int chance)
		{
			this.chance = chance;
			return this;
		}
		
		public Builder setCommands(Collection<String> commands)
		{
			this.commands = commands;
			return this;
		}
		
	}
	
	private static final String PLAYER_NAME_PLACEHOLDER = "{PLAYER_NAME}";
	
	private final String name;
	private final int chance;
	private final List<String> commands;
	
	Reward(String name, int chance, Collection<String> commands)
	{
		this.name = name;
		this.chance = chance;
		this.commands = new ArrayList<>(commands);
	}
	
	@Override
	public String getName()
	{
		return this.name;
	}
	
	public int getChance()
	{
		return this.chance;
	}
	
	public List<String> getCommands()
	{
		return new ArrayList<>(this.commands);
	}
	
	public Stream<String> getCommandsFor(Player player)
	{
		return new ArrayList<>(this.commands).stream().map(command -> this.commandProvider().apply(command, player));
	}
	
	private BiFunction<String, Player, String> commandProvider()
	{
		return (command, player) -> command.replace(Reward.PLAYER_NAME_PLACEHOLDER, player.getName());
	}
	
}
