package abstractland.spawners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;


public class Main extends JavaPlugin {

	public static Main instance;

	public static Main getInstance() {
		return instance;
	}

	public List<Gerador> geradorCache = new ArrayList<>();
	public Config data = new Config(this, "data.yml");
	
	@SuppressWarnings("deprecation")
	@Override
	public void onEnable() {
		instance = this;
		data.saveDefaultConfig();
		Bukkit.getServer().getPluginManager().registerEvents(new SpawnersEvents(), this);
		
		data.getConfig().getConfigurationSection("Geradores").getKeys(false).forEach(a -> {
			Location loc = Gerador.desarializeLoc(data.getString("Geradores." + a + ".Loc"));
			String dono = data.getString("Geradores." + a + ".Dono");
			EntityType tipo = EntityType.valueOf(data.getString("Geradores." + a + ".Tipo"));
			int stackados = data.getInt("Geradores." + a + ".Stackados");
			boolean publico = data.getBoolean("Geradores." + a + ".Publico");
			boolean droparDrops = data.getBoolean("Geradores." + a + ".DroparDrops");
			int idDrop = data.getInt("Geradores." + a + ".DropsID");
			int quantiaDrop = data.getInt("Geradores." + a + ".DropsQuantia");
			
			ArrayList<Drop> list = new ArrayList<>();
			list.add(new Drop(new ItemStack(Material.getMaterial(idDrop)), quantiaDrop));
			
			geradorCache.add(new Gerador(loc, dono, tipo, stackados, publico, droparDrops, list));
		});
		
		
	}

	@Override
	public void onDisable() {
		Gerador.getAll().forEach(a -> a.save());
		data.saveConfig();
	}

}