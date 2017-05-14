package com.ulfric.spigot.crates;

import com.ulfric.commons.spigot.plugin.UlfricPlugin;
import com.ulfric.spigot.crates.command.CommandContainer;

public final class Crates extends UlfricPlugin {
	
	@Override
	public void init()
	{
		this.install(CommandContainer.class);
	}
	
}
