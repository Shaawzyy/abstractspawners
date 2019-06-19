package abstractland.spawners;

import org.bukkit.inventory.ItemStack;

public class Drop {

	private ItemStack item;
	private int quantia;
	public Drop(ItemStack item, int quantia) {
		this.item = item;
		this.quantia = quantia;
	}
	public ItemStack getItem() {
		return item;
	}
	public void setItem(ItemStack item) {
		this.item = item;
	}
	public int getQuantia() {
		return quantia;
	}
	public void setQuantia(int quantia) {
		this.quantia = quantia;
	}
	
	
	
}

