package com.ulfric.spigot.crates.account;

import com.ulfric.commons.identity.Unique;
import com.ulfric.spigot.crates.Crate;

public interface CrateAccount extends Unique {
	
	int getKeys(Crate crate);
	
	void setKeys(Crate crate, int amount);
	
	void addKeys(Crate crate, int amount);
	
	void takeKeys(Crate crate, int amount);
	
}
