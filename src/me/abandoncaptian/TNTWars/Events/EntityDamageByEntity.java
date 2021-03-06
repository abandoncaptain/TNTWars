package me.abandoncaptian.TNTWars.Events;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import me.abandoncaptian.TNTWars.CountDowns;
import me.abandoncaptian.TNTWars.Main;

public class EntityDamageByEntity implements Listener {
	Main pl;
	CountDowns cd;

	public EntityDamageByEntity(Main plugin) {
		pl = plugin;
		cd = new CountDowns(plugin);
	}

	@EventHandler
	public void tntDamage(EntityDamageByEntityEvent e) {
		for(String map : pl.arenas.values()){
			if (pl.cd.active.get(map)) {
				if (e.getEntity() instanceof Player) {
					Player p = (Player) e.getEntity();
					if (e.getDamager() instanceof TNTPrimed) {
						for(int i: pl.teams.get(map).keySet()){
							List<String> players = pl.teams.get(map).get(i);
							if(players.contains(p.getName())){
								String cName = e.getDamager().getCustomName();
								cName = ChatColor.stripColor(cName);
								String killer = null;
								if(pl.cName.size()>=1){
									for(String play : pl.cName.keySet()){
										if(pl.cName.get(play).contains(cName)){
											killer = play;
											break;
										}
									}
									if(killer == null)killer = e.getDamager().getCustomName();
								}else{
									killer = e.getDamager().getCustomName();
								}
								if(p.getName() != killer){
									if(players.contains(killer)){
										e.setCancelled(true);
										return;
									}
								}
							}
						}
						if (pl.allInGame.contains(p.getName())) {
							if (pl.selectedKit.get(e.getDamager().getCustomName()) == "Vampire") {
								double dam = p.getLastDamage();
								if (dam > 4) {
									Player damager = Bukkit.getPlayer(e.getDamager().getCustomName());
									if (damager.getHealth() <= 18) {
										damager.setHealth(damager.getHealth() + 2);
									}
								}
							}
							if (p.getName() == e.getDamager().getCustomName()) {
								if (pl.selectedKit.get(p.getName()) == "Suicide Bomber") {
									double dam = p.getLastDamage();
									e.setDamage(dam / 2);
								}
							}
							if (pl.selectedKit.get(p.getName()) == "Tank") {
								double dam = p.getLastDamage();
								e.setDamage(dam / 1.5);
							}
							Bukkit.getScheduler().runTaskLater(pl, new Runnable() {
								@Override
								public void run() {
									if (pl.dead.get(map).contains(p.getName())) {
										pl.dead.get(map).remove(p.getName());
										int game = pl.inGame.get(map).size();
										String cName = e.getDamager().getCustomName();
										cName = ChatColor.stripColor(cName);
										String killer = null;
										if(pl.cName.size()>=1){
											for(String play : pl.cName.keySet()){
												if(pl.cName.get(play).contains(cName)){
													killer = play;
													break;
												}
											}
											if(killer == null)killer = e.getDamager().getCustomName();
										}else{
											killer = e.getDamager().getCustomName();
										}
										if (e.getEntity().getName() == killer){
											Bukkit.broadcastMessage("�7�l[�c�lTNT Wars�7�l] �b" + e.getEntity().getName() + " �6 killed themself �7- �b" + game + " remain!");	
										}else{
											if(killer == e.getDamager().getCustomName()){
												Bukkit.broadcastMessage("�b" + e.getEntity().getName() + " �6was killed by �c" + killer + " �7- �b" + game + " remain!");
											}else{
												Bukkit.broadcastMessage("�b" + e.getEntity().getName() + " �6was killed by �c" + killer);
												Bukkit.broadcastMessage("�bUsing �7[" + e.getDamager().getCustomName() + "�7] �cTNT �7- �b" + game + " remain!");
											}
											pl.econ.depositBalance(Bukkit.getPlayer(killer), 5);
											Bukkit.getPlayer(killer).sendMessage("�7�l[�c�lTNT Wars�7�l] �a+�75 Points");
										}
									}
								}
							}, 1);
						} else {
							e.setCancelled(true);
						}
					}else {
						e.setCancelled(true);
					}
				}
			}
		}
	}
}