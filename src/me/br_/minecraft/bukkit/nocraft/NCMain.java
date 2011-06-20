package me.br_.minecraft.bukkit.nocraft;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import net.minecraft.server.CraftingManager;
import net.minecraft.server.ShapedRecipes;
import net.minecraft.server.ShapelessRecipes;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

public class NCMain extends JavaPlugin {
	private Configuration config;
	private Set<Integer> forbidden = new HashSet<Integer>();
	private Logger log = Logger.getLogger("minecraft");

	public void onEnable() {
		config = this.getConfiguration();
		config.load();
		List<Integer> list = config.getIntList("ids", null);
		if (list == null || list.size() == 0) {
			list = new ArrayList<Integer>();
			list.add(46);
			config.setProperty("ids", list);
			config.save();
			log.info("[NoCraft] Default configuration created.");
		}
		int g = 0;
		forbidden = new HashSet<Integer>(list);
		Iterator<?> itr = CraftingManager.a().b().iterator();
		while (itr.hasNext()) {
			Object o = itr.next();
			if (o instanceof ShapedRecipes) {
				if (forbidden.contains(((ShapedRecipes) o).a)) {
					itr.remove();
					++g;
				}
			} else if (o instanceof ShapelessRecipes) {
				if (forbidden.contains(((ShapelessRecipes) o).b().id)) {
					itr.remove();
					++g;
				}
			}
		}
		log.info("[NoCraft] " + g + " recipes disabled.");
	}

	public void onDisable() {
		config.save();
	}

	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (!sender.isOp()) {
			sender.sendMessage("[NoCraft] You can't do that!");
			return true;
		}
		int i = 0;
		try {
			i = Integer.parseInt(args[1]);
		} catch (Exception e) {
			return false;
		}
		if (args[0].equalsIgnoreCase("add")) {
			forbidden.add(i);
		} else if (args[0].equalsIgnoreCase("remove")) {
			forbidden.remove(i);
		} else {
			return false;
		}
		sender.sendMessage("[NoCraft] This will take effect after the next server restart.");
		config.setProperty("ids", new ArrayList<Integer>(forbidden));
		return true;
	}
}
