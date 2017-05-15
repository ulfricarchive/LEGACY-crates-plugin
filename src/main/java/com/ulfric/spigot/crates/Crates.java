package com.ulfric.spigot.crates;

import com.ulfric.commons.spigot.plugin.UlfricPlugin;
import com.ulfric.spigot.crates.command.CommandContainer;
import com.ulfric.spigot.crates.service.CrateService;

public final class Crates extends UlfricPlugin {
	
	@Override
	public void init()
	{
		this.install(CrateService.class);
		this.install(CommandContainer.class);
	}
	
}
