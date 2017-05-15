package com.ulfric.spigot.crates.command;

import org.bukkit.entity.Player;

import com.ulfric.commons.naming.Name;
import com.ulfric.commons.spigot.command.Context;
import com.ulfric.commons.spigot.command.MustBePlayer;
import com.ulfric.commons.spigot.command.Permission;
import com.ulfric.commons.spigot.command.argument.Argument;
import com.ulfric.commons.spigot.item.ItemUtils;
import com.ulfric.commons.spigot.text.Text;
import com.ulfric.spigot.crates.Crate;
import com.ulfric.spigot.crates.metadata.CrateMetadataDefaults;
import com.ulfric.spigot.crates.service.Crates;

@Name("crate")
@Permission("crate-crate-use")
@MustBePlayer
class CrateCrateCommand extends CrateCommand {
	
	@Argument
	private String name;
	
	@Override
	public void run(Context context)
	{
		Player player = (Player) context.getSender();
		
		Crates crates = Crates.getService();
		Text text = Text.getService();
		
		Crate crate = crates.getCrate(this.name);
		
		if (crate == null)
		{
			text.sendMessage(player, "crate-not-found");
			return;
		}
		
		ItemUtils.giveItems(player, crates.getCrateBlock(crate));
		
		text.sendMessage(player, "crate-block-give", CrateMetadataDefaults.LAST_CRATE_NAME, crate.getName());
	}
	
}
