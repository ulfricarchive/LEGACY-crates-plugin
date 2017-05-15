package com.ulfric.spigot.crates.listener;

import com.ulfric.dragoon.container.Container;
import com.ulfric.dragoon.initialize.Initialize;

public class CrateListenerContainer extends Container {
	
	@Initialize
	private void initialize()
	{
		this.install(CrateListener.class);
	}
	
}
