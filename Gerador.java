package abstractland.spawners;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import abstractland.spawners.Main;
public class Gerador {

	private Location loc;
	private String dono;
	private EntityType tipo;
	private int stackados;
	private boolean publico, dropardrops;
	private List<Drop> drops;
	public Gerador(Location loc, String dono, EntityType tipo, int stackados, boolean publico, boolean dropardrops, List<Drop> drops) {
		this.loc = loc;
		this.dono = dono;
		this.setTipo(tipo);
		this.stackados = stackados;
		this.publico = publico;
		this.dropardrops = dropardrops;
		this.drops = drops;
	}
	public Location getLoc() {
		return loc;
	}
	public void setLoc(Location loc) {
		this.loc = loc;
	}
	public String getDono() {
		return dono;
	}
	public void setDono(String dono) {
		this.dono = dono;
	}
	public int getStackados() {
		return stackados;
	}
	public void setStackados(int stackados) {
		this.stackados = stackados;
	}
	public boolean isPublico() {
		return publico;
	}
	public void setPublico(boolean publico) {
		this.publico = publico;
	}
	public boolean isDropardrops() {
		return dropardrops;
	}
	public void setDropardrops(boolean dropardrops) {
		this.dropardrops = dropardrops;
	}
	public List<Drop> getDrops() {
		return drops;
	}
	public void setDrops(List<Drop> drops) {
		this.drops = drops;
	}
	public static List<Gerador> getAll(){
		return Main.getInstance().geradorCache;
	}
	public EntityType getTipo() {
		return tipo;
	}
	public void setTipo(EntityType tipo) {
		this.tipo = tipo;
	}
	public ItemStack getPublicItem() {
		if(isPublico()) {
			return Criar.add(Material.INK_SACK, "§ePúblico", 2, new String[] {"§7Status:§a Ativado.", " ", "§fJogadores vão conseguir matar mobs", "§fque nasceram por este gerador."});
		}else {
			return Criar.add(Material.INK_SACK, "§ePúblico", 1, new String[] {"§7Status:§c Desativado.", " ", "§fJogadores não vão conseguir matar mobs", "§fque nasceram por este gerador."});
		}
	}
	
	public ItemStack getDropStatusItem() {
		if(isDropardrops()) {
			return Criar.add(Material.INK_SACK, "§eDrop status", 2, new String[] {"§7Status:§a Ativado.", " ", "§fOs drops serão dropados normalmente no chão."});
		}else {
			return Criar.add(Material.INK_SACK, "§eDrop status", 1, new String[] {"§7Status:§c Desativado.", " ", "§fOs drops serão armazenados em seus containers."});
		}
	}
	
	public static String serializeLoc(Location loc) {
		return loc.getX() + "#" + loc.getY() + "#" + loc.getZ() + "#" + loc.getWorld().getName();
	}

	public static Location desarializeLoc(String loc) {
		String[] split = loc.split("#");
		Location loc2 = new Location(Bukkit.getWorld(split[3]), Double.parseDouble(split[0]),
				Double.parseDouble(split[1]), Double.parseDouble(split[2]));
		return loc2;
	}
	
	@SuppressWarnings("deprecation")
	public void save() {
		String idR = abstractland.spawners.Utilidades.randomString(15);
		Main.getInstance().data.set("Geradores." + idR + ".Loc", serializeLoc(loc));
		Main.getInstance().data.set("Geradores." + idR + ".Dono", getDono());
		Main.getInstance().data.set("Geradores." + idR + ".Tipo", getTipo().toString());
		Main.getInstance().data.set("Geradores." + idR + ".Stackados", getStackados());
		Main.getInstance().data.set("Geradores." + idR + ".Publico", isPublico());
		Main.getInstance().data.set("Geradores." + idR + ".DroparDrops", isDropardrops());
		Main.getInstance().data.set("Geradores." + idR + ".DropsID", getDrops().get(0).getItem().getTypeId());
		Main.getInstance().data.set("Geradores." + idR + ".DropsQuantia", getDrops().get(0).getQuantia());
				
		
	}
	
}