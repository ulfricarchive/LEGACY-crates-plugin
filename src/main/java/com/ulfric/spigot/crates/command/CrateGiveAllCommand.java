package com.ulfric.spigot.crates.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import com.ulfric.commons.naming.Name;
import com.ulfric.commons.spigot.command.Alias;
import com.ulfric.commons.spigot.command.Context;
import com.ulfric.commons.spigot.command.Permission;
import com.ulfric.commons.spigot.command.argument.Argument;
import com.ulfric.commons.spigot.text.Text;
import com.ulfric.spigot.crates.Crate;
import com.ulfric.spigot.crates.account.CrateAccount;
import com.ulfric.spigot.crates.metadata.CrateMetadataDefaults;
import com.ulfric.spigot.crates.service.Crates;

@Name("giveall")
@Alias("ga")
@Permission("crate-giveall")
class CrateGiveAllCommand extends CrateCommand {
	
	@Argument
	private String name;
	
	@Argument(optional = true)
	private Integer amount;
	
	@Override
	public void run(Context context)
	{
		CommandSender sender = context.getSender();
		
		Crates crates = Crates.getService();
		Text text = Text.getService();
		
		Crate crate = crates.getCrate(this.name);
		int amount = this.amount == null ? 1 : this.amount;
		
		if (crate == null)
		{
			text.sendMessage(sender, "crate-not-found");
			return;
		}
		
		this.giveAll(crate, amount);
		
		text.sendMessage(sender, "crate-giveall",
				CrateMetadataDefaults.LAST_CRATE_NAME, crate.getName());
	}
	
	private void giveAll(Crate crate, int amount)
	{
		Bukkit.getOnlinePlayers().forEach(player ->
		{
			CrateAccount account = Crates.getService().getAccount(player.getUniqueId());
			account.addKeys(crate, amount);
		});
	}
	
}
