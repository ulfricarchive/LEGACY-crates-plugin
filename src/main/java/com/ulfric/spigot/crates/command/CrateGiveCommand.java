package com.ulfric.spigot.crates.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.ulfric.commons.naming.Name;
import com.ulfric.commons.spigot.command.Context;
import com.ulfric.commons.spigot.command.Permission;
import com.ulfric.commons.spigot.command.argument.Argument;
import com.ulfric.commons.spigot.text.Text;
import com.ulfric.spigot.crates.Crate;
import com.ulfric.spigot.crates.account.CrateAccount;
import com.ulfric.spigot.crates.metadata.CrateMetadataDefaults;
import com.ulfric.spigot.crates.service.Crates;

@Name("give")
@Permission("crate-give")
public class CrateGiveCommand extends CrateCommand {
	
	@Argument
	private Player target;
	
	@Argument
	private String name;
	
	@Override
	public void run(Context context)
	{
		CommandSender sender = context.getSender();
		
		Crates crates = Crates.getService();
		Text text = Text.getService();
		
		Crate crate = crates.getCrate(this.name);
		
		if (crate == null)
		{
			text.sendMessage(sender, "crate-not-found");
			return;
		}
		
		CrateAccount account = crates.getAccount(this.target.getUniqueId());
		account.addKeys(crate, 1);
		
		text.sendMessage(sender, "crate-give",
				CrateMetadataDefaults.LAST_CRATE_GIVE_NAME, crate.getName());
	}
	
}
