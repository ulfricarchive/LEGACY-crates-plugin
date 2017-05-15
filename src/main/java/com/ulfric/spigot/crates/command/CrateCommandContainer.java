package com.ulfric.spigot.crates.command;

import com.ulfric.dragoon.container.Container;
import com.ulfric.dragoon.initialize.Initialize;

public class CrateCommandContainer extends Container {
	
	@Initialize
	private void initialize()
	{
		this.install(CrateCommand.class);
		this.install(CrateGiveCommand.class);
		this.install(CrateGiveAllCommand.class);
		this.install(CrateCrateCommand.class);
	}
	
}
