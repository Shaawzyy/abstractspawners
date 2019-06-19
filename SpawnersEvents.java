package abstractland.spawners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import abstractland.spawners.Main;
import abstractland.spawners.Utilidades;
import abstractland.spawner.CreaturasSpawners;
import abstractland.spawners.Criar;
import abstractland.spawners.Drop;
import abstractland.spawners.Gerador;

public class SpawnersEvents extends Utilidades implements Listener {

	public static List<Block> getNearSpawners(Location loc, int radraioius, EntityType tipo) {
		ArrayList<Block> spawners = new ArrayList<>();
		int xx = radraioius;
		int yy = radraioius;
		int zz = radraioius;
		for (int x = -xx; x <= xx; x++) {
			for (int y = -yy; y <= yy; y++) {
				for (int z = -zz; z <= zz; z++) {
					Block b = loc.clone().add(x, y, z).getBlock();
					if (b.getType() == Material.MOB_SPAWNER) {
						BlockState stat = b.getState();
						CreatureSpawner spawnercr = (CreatureSpawner) stat;
						if (spawnercr.getSpawnedType() == tipo) {
							spawners.add(b);
						}
					}
				}

			}
		}
		return spawners;
	}

	public void setSpawner(Block block, EntityType ent) {
		BlockState blockState = block.getState();
		CreatureSpawner spawner = ((CreatureSpawner) blockState);
		spawner.setSpawnedType(ent);
		blockState.update();
	}

	@EventHandler
	public void placeEvent(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		if (p.getItemInHand().getType() == Material.MOB_SPAWNER) {
			if (p.getItemInHand() != null) {
				if (p.getItemInHand().hasItemMeta()) {
					if (p.getItemInHand().getItemMeta().hasLore()) {
						for (CreaturasSpawners cs : CreaturasSpawners.values()) {
							if (p.getItemInHand().getItemMeta().getLore().get(0)
									.contains("§7Tipo:§f " + cs.getName())) {
								if (getNearSpawners(e.getBlock().getLocation(), 10, cs.getTipo()).size() >= 1) {

									e.setCancelled(true);
									Location loc = getNearSpawners(e.getBlock().getLocation(), 10, cs.getTipo()).get(0)
											.getLocation();
									for (Gerador g : Main.getInstance().geradorCache) {
										if (g.getLoc().equals(loc)) {
											if (!g.getDono().equals(p.getName())) {
												p.sendMessage(prefixo
														+ "§cVocê está tentando stackar mais um spawner em um gerador que não é seu.");
												return;
											}
											g.setStackados(g.getStackados() + 1);
											p.sendMessage(prefixo
													+ "§aSeu spawner foi stackado ao mais próximo na distancia de 10 blocos a partir de seu gerador.");
										}
									}
									return;
								}
								setSpawner(e.getBlock(), cs.getTipo());
								p.sendMessage(Utilidades.prefixo + "§aSpawner do tipo §f" + cs.getName()
										+ " §acolocado com sucesso! Clique nele para interagir com o mesmo.");

								BlockState blockState = e.getBlock().getState();
								CreatureSpawner spawner = ((CreatureSpawner) blockState);

								ItemStack item = null;
								if (spawner.getSpawnedType().equals(EntityType.COW)) {
									item = new ItemStack(Material.LEATHER);
								}
								if (spawner.getSpawnedType().equals(EntityType.BLAZE)) {
									item = new ItemStack(Material.BLAZE_ROD);
								}
								if (spawner.getSpawnedType().equals(EntityType.IRON_GOLEM)) {
									item = new ItemStack(Material.IRON_INGOT);
								}
								if (spawner.getSpawnedType().equals(EntityType.PIG_ZOMBIE)) {
									item = new ItemStack(Material.GOLD_INGOT);
								}
								if (spawner.getSpawnedType().equals(EntityType.ZOMBIE)) {
									item = new ItemStack(Material.ROTTEN_FLESH);
								}
								if (spawner.getSpawnedType().equals(EntityType.PIG)) {
									item = new ItemStack(Material.RAW_BEEF);
								}
								if (spawner.getSpawnedType().equals(EntityType.SKELETON)) {
									item = new ItemStack(Material.BONE);
								}

								ArrayList<Drop> list = new ArrayList<>();
								list.add(new Drop(item, 0));
								Gerador g = new Gerador(e.getBlock().getLocation(), p.getName(), cs.getTipo(), 1, true,
										true, list);
								abstractland.spawners.Main.getInstance().geradorCache.add(g);
							}
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void dropItem(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if (e.getClickedBlock() instanceof Block) {
			Block b = e.getClickedBlock();
			if (e.getAction().toString().startsWith("RIGHT")) {
				if (b.getType().equals(Material.MOB_SPAWNER)) {
					for (int i = 0; i < Main.getInstance().geradorCache.size(); i++) {
						Gerador g = Main.getInstance().geradorCache.get(i);
						if (g.getLoc().equals(b.getLocation())) {
							if (!g.getDono().equals(p.getName())) {
								p.sendMessage(prefixo + "§cVocê não pode interagir com um spawner que não é seu.");
								return;
							}
							Inventory inv = Bukkit.createInventory(null, 3 * 9, "Gerador - Painel");

							inv.setItem(11, Criar.add(Material.CHEST, "§eGerencie seus drops",
									new String[] { "§fClique para acessar o painel gerenciador", "§fde drops." }));

							inv.setItem(13,
									Criar.add(Material.PAPER, "§eInformações de seu gerador",
											new String[] { "§7Dono: §f" + g.getDono(),
													"§7Tipo da creatura:§f " + g.getTipo(),
													"§7Geradores stackados:§f " + g.getStackados() }));
							inv.setItem(15, g.getPublicItem());

							p.openInventory(inv);
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void clickInv(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if (e.getInventory().getTitle().equals("Gerador - Painel")) {
			if (e.getCurrentItem() != null) {
				if (e.getCurrentItem().hasItemMeta()) {
					e.setCancelled(true);
					Location loc = getNearSpawners(p.getLocation(), 10,
							EntityType
									.valueOf(e.getInventory().getItem(13).getItemMeta().getLore().get(1).split(" ")[3]))
											.get(0).getLocation();
					if (e.getCurrentItem().getType() == Material.CHEST) {
						Inventory inv = Bukkit.createInventory(null, 3 * 9, "Gerador - Drops "
								+ e.getInventory().getItem(13).getItemMeta().getLore().get(1).split(" ")[3]);

						inv.setItem(12,
								Criar.add(Material.CHEST, "§eContainer de drops",
										new String[] { "§fAqui que ficam guardados seus drops caso",
												"§f a função §7dropar no chão §festiver desabilitada." }));

						for (Gerador g : Main.getInstance().geradorCache) {
							if (g.getLoc().equals(loc)) {
								inv.setItem(14, g.getDropStatusItem());
							}
						}

						p.openInventory(inv);
						return;
					}
					for (Gerador g : Main.getInstance().geradorCache) {
						if (g.getLoc().equals(loc)) {
							if (e.getCurrentItem().getType().equals(Material.INK_SACK)) {
								if (g.isPublico()) {
									g.setPublico(false);
									Inventory inv = Bukkit.createInventory(null, 3 * 9, "Gerador - Painel");

									inv.setItem(11,
											Criar.add(Material.CHEST, "§eGerencie seus drops", new String[] {
													"§fClique para acessar o painel gerenciador", "§fde drops." }));

									inv.setItem(13,
											Criar.add(Material.PAPER, "§eInformações de seu gerador",
													new String[] { "§7Dono: §f" + g.getDono(),
															"§7Tipo da creatura:§f " + g.getTipo(),
															"§7Geradores stackados:§f " + g.getStackados() }));
									inv.setItem(15, g.getPublicItem());

									p.openInventory(inv);

								} else {
									g.setPublico(true);
									Inventory inv = Bukkit.createInventory(null, 3 * 9, "Gerador - Painel");

									inv.setItem(11,
											Criar.add(Material.CHEST, "§eGerencie seus drops", new String[] {
													"§fClique para acessar o painel gerenciador", "§fde drops." }));

									inv.setItem(13,
											Criar.add(Material.PAPER, "§eInformações de seu gerador",
													new String[] { "§7Dono: §f" + g.getDono(),
															"§7Tipo da creatura:§f " + g.getTipo(),
															"§7Geradores stackados:§f " + g.getStackados() }));
									inv.setItem(15, g.getPublicItem());

									p.openInventory(inv);

								}
								return;
							}
						}
					}
				}
			}
			return;
		}
		if (e.getInventory().getTitle().startsWith("Gerador - Drops")) {
			if (e.getCurrentItem() != null) {
				if (e.getCurrentItem().hasItemMeta()) {
					e.setCancelled(true);
					String title = e.getInventory().getTitle().split(" ")[3];
					Location loc = getNearSpawners(p.getLocation(), 10, EntityType.valueOf(title)).get(0).getLocation();
					if (e.getCurrentItem().getType().equals(Material.CHEST)) {
						for (Gerador g : Main.getInstance().geradorCache) {
							if (g.getLoc().equals(loc)) {
								if (g.getDrops().isEmpty()) {
									p.sendMessage(
											prefixo + "§cVocê não tem nenhum drop armazenado em seus containers.");
									return;
								}
								Inventory inv = Bukkit.createInventory(null, 3 * 9, "Gerador - Container " + title);
								inv.setItem(13,
										Criar.add(g.getDrops().get(0).getItem().getType(), "§eDrop 1",
												new String[] { " ", "§7Quantia:§f " + g.getDrops().get(0).getQuantia(),
														"", "§7Botão esquerdo:§f Vende tudo.",
														"§7Botão direito:§f Recolhe 1 pack." }));
								p.openInventory(inv);
							}
						}
						return;
					}
					for (Gerador g : Main.getInstance().geradorCache) {
						if (g.getLoc().equals(loc)) {
							if (e.getCurrentItem().getType().equals(Material.INK_SACK)) {
								if (g.isDropardrops()) {
									g.setDropardrops(false);
									Inventory inv = Bukkit.createInventory(null, 3 * 9, "Gerador - Drops " + title);

									inv.setItem(12,
											Criar.add(Material.CHEST, "§eContainer de drops",
													new String[] { "§fAqui que ficam guardados seus drops caso",
															"§f a função §7dropar no chão §festiver desabilitada." }));

									inv.setItem(14, g.getDropStatusItem());

									p.openInventory(inv);

								} else {
									g.setDropardrops(true);
									Inventory inv = Bukkit.createInventory(null, 3 * 9, "Gerador - Drops " + title);

									inv.setItem(12,
											Criar.add(Material.CHEST, "§eContainer de drops",
													new String[] { "§fAqui que ficam guardados seus drops caso",
															"§f a função §7dropar no chão §festiver desabilitada." }));

									inv.setItem(14, g.getDropStatusItem());

									p.openInventory(inv);

								}
								return;
							}
						}
					}
				}
			}
			return;
		}

		if (e.getInventory().getTitle().startsWith("Gerador - Container")) {
			if (e.getCurrentItem() != null) {
				if (e.getCurrentItem().hasItemMeta()) {
					e.setCancelled(true);
					String title = e.getInventory().getTitle().split(" ")[3];
					Location loc = getNearSpawners(p.getLocation(), 10, EntityType.valueOf(title)).get(0).getLocation();
					for (Gerador g : Main.getInstance().geradorCache) {
						if (g.getLoc().equals(loc)) {
							if (e.isRightClick()) {
								if (p.getInventory().firstEmpty() == -1) {
									p.sendMessage(prefixo + "§cSem espaço no inventário para recolher itens.");
									return;
								}
								if (g.getDrops().get(0).getQuantia() < 64) {
									p.sendMessage(
											prefixo + "§cVocê deve ter pelo menos 64 itens aqui para recolher 1 pack.");
									return;
								}
								ItemStack item = g.getDrops().get(0).getItem().clone();
								item.setAmount(64);
								p.getInventory().addItem(item);
								g.getDrops().get(0).setQuantia(g.getDrops().get(0).getQuantia() - 64);

								Inventory inv = Bukkit.createInventory(null, 3 * 9, "Gerador - Container " + title);
								inv.setItem(13,
										Criar.add(g.getDrops().get(0).getItem().getType(), "§eDrop 1",
												new String[] { " ", "§7Quantia:§f " + g.getDrops().get(0).getQuantia(),
														"", "§7Botão esquerdo:§f Vende tudo.",
														"§7Botão direito:§f Recolhe 1 pack." }));
								p.openInventory(inv);
							} else {
							p.chat("/vender");
							}
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void quebrar(BlockBreakEvent e) {
		Player p = e.getPlayer();
		Block b = e.getBlock();
		if (e.getBlock().getType() == Material.MOB_SPAWNER) {
			if (p.getInventory().firstEmpty() == -1) {
				e.setCancelled(true);
				p.sendMessage(Utilidades.prefixo + "§cSeu inventário está cheio e não pode receber spawners.");
				return;
			}
			for (int i = 0; i < Main.getInstance().geradorCache.size(); i++) {
				Gerador g = Main.getInstance().geradorCache.get(i);
				if (g.getLoc().equals(b.getLocation())) {
					if (!g.getDono().equals(p.getName())) {
						e.setCancelled(true);
						p.sendMessage(prefixo + "§cVocê não pode interagir com um spawner que não é seu.");
						return;
					}
					if (g.getStackados() == 1) {
						p.sendMessage(prefixo + "§aUltimo spawner removido com sucesso.");
						Main.getInstance().geradorCache.remove(g);
						for (CreaturasSpawners cs : CreaturasSpawners.values()) {
							BlockState blockState = b.getState();
							CreatureSpawner spawner = ((CreatureSpawner) blockState);
							if (cs.getTipo().equals(spawner.getSpawnedType())) {
								String displaynameSpawner = "§eGerador de monstros";
								String lore1Spawner = "§7Tipo:§f " + cs.getName();

								ItemStack spawners = new ItemStack(Material.MOB_SPAWNER, 1);
								ItemMeta spawnersMeta = spawners.getItemMeta();
								spawnersMeta.setDisplayName(displaynameSpawner);
								spawnersMeta.setLore(Arrays.asList(lore1Spawner));
								spawners.setItemMeta(spawnersMeta);

								p.getInventory().addItem(spawners);
							}
						}
					} else {
						e.setCancelled(true);
						g.setStackados(g.getStackados() - 1);
						p.sendMessage(prefixo + "§aSpawner quebrado com sucesso! Agora você tem §f" + g.getStackados()
								+ " §aspawners stackados.");
						for (CreaturasSpawners cs : CreaturasSpawners.values()) {
							BlockState blockState = b.getState();
							CreatureSpawner spawner = ((CreatureSpawner) blockState);
							if (cs.getTipo().equals(spawner.getSpawnedType())) {
								String displaynameSpawner = "§eGerador de monstros";
								String lore1Spawner = "§7Tipo:§f " + cs.getName();

								ItemStack spawners = new ItemStack(Material.MOB_SPAWNER, 1);
								ItemMeta spawnersMeta = spawners.getItemMeta();
								spawnersMeta.setDisplayName(displaynameSpawner);
								spawnersMeta.setLore(Arrays.asList(lore1Spawner));
								spawners.setItemMeta(spawnersMeta);

								p.getInventory().addItem(spawners);
							}
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void aoNascer(SpawnerSpawnEvent evento) {
		final Block bloco = evento.getSpawner().getBlock();
		final Location location = bloco.getLocation();
		for (Gerador g : Main.getInstance().geradorCache) {
			if (g.getLoc().equals(location)) {
				for (int i = 0; i < g.getStackados(); i++) {
					location.getWorld().spawnEntity(evento.getEntity().getLocation(), evento.getEntityType());
				}
			}
		}
	}

	@EventHandler
	public void spawn(CreatureSpawnEvent e) {

		double r = 16;

		LivingEntity entidade = e.getEntity();
		List<Entity> entidadesProximas = entidade.getNearbyEntities(r, r, r);

		if (e.getSpawnReason() == SpawnReason.NATURAL) {
			return;
		}

		if (e.getSpawnReason() == SpawnReason.DISPENSE_EGG) {
			return;
		}

		if (e.getEntityType().equals(EntityType.VILLAGER))
			return;
		if (e.getEntityType().equals(EntityType.ARMOR_STAND))
			return;
		if (entidadesProximas.size() >= 1) {
			@SuppressWarnings("rawtypes")
			Iterator arg7 = entidadesProximas.iterator();
			while (arg7.hasNext()) {
				Entity ent = (Entity) arg7.next();
				if (ent.getType() == entidade.getType()) {
					if (ent.hasMetadata("Quantidade")) {
						if (ent.getMetadata("Quantidade") != null) {
							if (!ent.getMetadata("Quantidade").isEmpty()) {
								if (ent.getMetadata("Quantidade").get(0) != null) {

									int arg9 = ((MetadataValue) ent.getMetadata("Quantidade").get(0)).asInt();

									((LivingEntity) ent).addPotionEffect(
											new PotionEffect(PotionEffectType.SLOW, 99 * 99 * 99, 127));

									arg9++;

									String format = "§e" + arg9 + "§fx §7stackados";
									ent.setMetadata("Quantidade",
											new FixedMetadataValue(Main.getInstance(), Integer.valueOf(arg9)));
									ent.setCustomName(format);
									e.setCancelled(true);
									return;
								}
							}
						}
					}
					byte entQuantidade = 2;
					ent.setMetadata("Quantidade",
							new FixedMetadataValue(Main.getInstance(), Integer.valueOf(entQuantidade)));
					e.setCancelled(true);

					return;
				}
			}

			entidade.setMetadata("Quantidade", new FixedMetadataValue(Main.getInstance(), Integer.valueOf(1)));

		}

	}

	public Double getLooting(Player p) {
		for (int i = 0; i < 100; i++) {
			if (p.getItemInHand().containsEnchantment(Enchantment.LOOT_BONUS_MOBS)) {
				if (p.getItemInHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS) == i) {
					return 1.1 + i;
				}
			}
		}
		return 1.0;
	}

	@EventHandler
	public void hit(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player) {
			Player jogador = (Player) e.getDamager();

			Entity entidade = e.getEntity();
			if (e.getEntity().hasMetadata("Quantidade")) {
				if (getNearSpawners(entidade.getLocation(), 10, e.getEntityType()).size() > 0) {
					Location loc = getNearSpawners(entidade.getLocation(), 10, e.getEntityType()).get(0).getLocation();
					for (int i = 0; i < Main.getInstance().geradorCache.size(); i++) {
						Gerador g = Main.getInstance().geradorCache.get(i);
						if (g.getLoc().equals(loc)) {
							if (!g.isPublico()) {
								e.setCancelled(true);
								jogador.sendMessage(prefixo
										+ "§cEste mob foi spawnado por um gerador que não se encontra público.");
								return;
							}
						}
					}
				}
			}
		}
	}

	@SuppressWarnings({ "unused" })
	@EventHandler
	public void Drop(EntityDeathEvent e) {
		Player jogador = e.getEntity().getKiller();
		Entity entidade = e.getEntity();
		int raioMultiplicador = 10;

		List<ItemStack> drops = e.getDrops();

		if (e.getEntity().getKiller() instanceof Player) {
			if (e.getEntity().hasMetadata("Quantidade")) {
				if (getNearSpawners(entidade.getLocation(), 10, e.getEntityType()).size() > 0) {
					int resultadoFinal = (((MetadataValue) e.getEntity().getMetadata("Quantidade").get(0)).asInt());

					Location loc = getNearSpawners(entidade.getLocation(), 10, e.getEntityType()).get(0).getLocation();
					if (loc == null) {
						return;
					}
					for (Gerador g : Main.getInstance().geradorCache) {
						if (g.getLoc().equals(loc)) {
							if (g.isDropardrops()) {
								int rf = (int) (resultadoFinal * getLooting(jogador) + g.getStackados());
								ItemStack item = null;
								if (e.getEntityType().equals(EntityType.COW)) {
									item = new ItemStack(Material.LEATHER, rf);
								}
								if (e.getEntityType().equals(EntityType.BLAZE)) {
									item = new ItemStack(Material.BLAZE_ROD, rf);
								}
								if (e.getEntityType().equals(EntityType.IRON_GOLEM)) {
									item = new ItemStack(Material.IRON_INGOT, rf);
								}
								if (e.getEntityType().equals(EntityType.PIG_ZOMBIE)) {
									item = new ItemStack(Material.GOLD_INGOT, rf);
								}
								if (e.getEntityType().equals(EntityType.ZOMBIE)) {
									item = new ItemStack(Material.ROTTEN_FLESH, rf);
								}
								if (e.getEntityType().equals(EntityType.PIG)) {
									item = new ItemStack(Material.RAW_BEEF, rf);
								}
								if (e.getEntityType().equals(EntityType.SKELETON)) {
									item = new ItemStack(Material.BONE, rf);
								}
								entidade.getLocation().getWorld().dropItemNaturally(entidade.getLocation(), item);
							} else {

								ItemStack item = null;
								if (e.getEntityType().equals(EntityType.COW)) {
									item = new ItemStack(Material.LEATHER);
								}
								if (e.getEntityType().equals(EntityType.BLAZE)) {
									item = new ItemStack(Material.BLAZE_ROD);
								}
								if (e.getEntityType().equals(EntityType.IRON_GOLEM)) {
									item = new ItemStack(Material.IRON_INGOT);
								}
								if (e.getEntityType().equals(EntityType.PIG_ZOMBIE)) {
									item = new ItemStack(Material.GOLD_INGOT);
								}
								if (e.getEntityType().equals(EntityType.ZOMBIE)) {
									item = new ItemStack(Material.ROTTEN_FLESH);
								}
								if (e.getEntityType().equals(EntityType.PIG)) {
									item = new ItemStack(Material.RAW_BEEF);
								}
								if (e.getEntityType().equals(EntityType.SKELETON)) {
									item = new ItemStack(Material.BONE);
								}

								int rf = (int) (resultadoFinal * getLooting(jogador) + g.getStackados());
								e.getDrops().clear();
								jogador.sendMessage(prefixo + "§aDrop adicionado no container do spawner!");
								g.getDrops().get(0).setQuantia(g.getDrops().get(0).getQuantia() + rf);
							}
						}
					}
				} else {
					jogador.sendMessage(prefixo
							+ "§cPelo visto não há nenhum gerador desse tipo no raio de 10 blocos, o drop por tanto foi padrão.");
				}
			}
		}
	}

}