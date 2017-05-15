package com.ulfric.spigot.crates.listener;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.ulfric.commons.spigot.intercept.RequirePermission;
import com.ulfric.commons.spigot.text.Text;
import com.ulfric.spigot.crates.Crate;
import com.ulfric.spigot.crates.Reward;
import com.ulfric.spigot.crates.account.CrateAccount;
import com.ulfric.spigot.crates.metadata.CrateMetadataDefaults;
import com.ulfric.spigot.crates.service.Crates;

class CrateListener implements Listener {
	
	@EventHandler
	@RequirePermission(permission = "crate-block-place")
	private void on(BlockPlaceEvent event)
	{
		Player player = event.getPlayer();
		ItemStack itemStack = event.getItemInHand();
		
		Crate crate = this.identify(itemStack);
		
		if (crate == null)
		{
			return;
		}
		
		Block block = event.getBlock();
		
		Crates.getService().setCrate(crate, block.getLocation());
		
		Text.getService().sendMessage(player, "crate-create", CrateMetadataDefaults.LAST_CRATE_NAME, crate.getName());
	}
	
	@EventHandler
	@RequirePermission(permission = "crate-block-break")
	private void on(BlockBreakEvent event)
	{
		Player player = event.getPlayer();
		Block block = event.getBlock();
		
		Crates crates = Crates.getService();
		
		if (!crates.isCrate(block.getLocation()))
		{
			return;
		}
		
		Text text = Text.getService();
		
		if (!player.isSneaking())
		{
			event.setCancelled(true);
			text.sendMessage(player, "crate-break-not-sneaking");
			return;
		}
		
		Crate crate = crates.getCrate(block);
		crates.removeCrate(crate, block.getLocation());
		
		text.sendMessage(player, "crate-delete", CrateMetadataDefaults.LAST_CRATE_NAME, crate.getName());
	}
	
	@EventHandler
	@RequirePermission(permission = "crate-key-use")
	private void on(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		Action action = event.getAction();
		
		if (action != Action.RIGHT_CLICK_BLOCK)
		{
			return;
		}
		
		Block block = event.getClickedBlock();
		
		Crates crates = Crates.getService();
		
		if (!crates.isCrate(block.getLocation()))
		{
			return;
		}
		
		event.setCancelled(true);
		
		Crate crate = crates.getCrate(block);
		
		this.handleCrate(player, crate);
	}
	
	private void handleCrate(Player player, Crate crate)
	{
		CrateAccount account = Crates.getService().getAccount(player.getUniqueId());
		
		Text text = Text.getService();
		
		if (!account.hasKeys(crate, 1))
		{
			text.sendMessage(player, "crate-not-enough-keys");
			return;
		}
		
		account.takeKeys(crate, 1);
		
		Reward reward = crate.randomReward();
		reward.getCommandsFor(player).forEach(this::execute);
		
		text.sendMessage(player, "crate-reward",
				CrateMetadataDefaults.LAST_CRATE_NAME, crate.getName(),
				CrateMetadataDefaults.LAST_CRATE_REWARD_NAME, reward.getName());
	}
	
	private void execute(String command)
	{
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
	}
	
	private Crate identify(ItemStack itemStack)
	{
		if (itemStack == null)
		{
			return null;
		}
		
		Crates crates = Crates.getService();
		
		for (Crate crate : crates.getCrates())
		{
			if (itemStack.isSimilar(crates.getCrateBlock(crate)))
			{
				return crate;
			}
		}
		
		return null;
	}
	
}
