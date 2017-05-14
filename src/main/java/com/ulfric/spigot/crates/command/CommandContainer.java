package com.ulfric.spigot.crates.command;

import com.ulfric.dragoon.container.Container;
import com.ulfric.dragoon.initialize.Initialize;

public class CommandContainer extends Container {
	
	@Initialize
	private void initialize()
	{
		this.install(CrateCommand.class);
	}
	
}
