package com.mayspeed.mayapi;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;

public class MayAPI {
	
	/**
	 * 方法作用：更改一个玩家的Tab列表
	 * @param player 玩家
	 * @param head Tab顶部
	 * @param foot Tab底部
	 */
	public static void setTab(Player player, String head, String foot) {
		if (head == null) {
			head = "";
		}
		head = ChatColor.translateAlternateColorCodes('&', head);
		if (foot == null) {
			foot = "";
		}
		foot = ChatColor.translateAlternateColorCodes('&', foot);
		try {
			Object tabHeader = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke(null, new Object[] { "{\"text\":\"" + head + "\"}" });
		    Object tabFooter = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke(null, new Object[] { "{\"text\":\"" + foot + "\"}" });
		    Constructor<?> titleConstructor = getNMSClass("PacketPlayOutPlayerListHeaderFooter").getConstructor(new Class[] { getNMSClass("IChatBaseComponent") });
		    Object packet = titleConstructor.newInstance(new Object[] { tabHeader });
		    Field field = packet.getClass().getDeclaredField("b");
		    field.setAccessible(true);
		    field.set(packet, tabFooter);
		    MayAPI.sendPacket(player, packet);
		 }catch (Exception e) {
			 e.printStackTrace();
		 }
	}
	
	/**
	 * 方法作用：将名字转换为UUID
	 * @param name 要填写的名字
	 */
	public UUID translateNameToUUID(String name) {
		UUID uuid = null;
	    uuid = Bukkit.getPlayer(name).getUniqueId();
	    return uuid;
	}
	
	/**
	 * 方法作用：发送一个Actionbar给玩家
	 * @param player 玩家
	 * @param message 信息
	 */
	public static void sendActionbar(Player player,String message) {
		IChatBaseComponent icbc = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + message + "\"}");
		PacketPlayOutChat ppoc = new PacketPlayOutChat(icbc, (byte)2);
		MayAPI.sendPacket(player, ppoc);
	}

	/**
	 * 方法作用：取一个列表中某个lore存在的行数
	 * @param lores 列表(List<String>)
	 * @param lore 要判断的数据
	 */
    public static int getLoreIndex(List<String> lores, String lore) {
    	int i = 0;
    	for (String a : lores) {
    		if (a.equalsIgnoreCase(lore)) {
    			return i;
    		}
    		i++;
    	}
    	return -1;
    } 
	
	/**
	 * 方法作用：设置某物品指定行数的Lore
	 * @param is 物品(ItemStack类型)
	 * @param Line 行数
	 * @param lore 要设置的
	 */
	public static void setLore(ItemStack is,int Line,String lore) {
		List <String> lores = new ArrayList<String>();
		if(is == null || is.getType() == Material.AIR) {
			throw new NullPointerException();
		}
		if(is.getItemMeta().hasLore()) {
			lores.addAll(is.getItemMeta().getLore());
			lores.set((Line - 1), lore.replaceAll("&", "§"));
			is.getItemMeta().setLore(lores);
			is.setItemMeta(is.getItemMeta());
		}else {
			return;
		}
	}
	
	/**
     * 方法作用：添加Lore
     * @param is 需要设置的物品
     * @param lore 待添加的String
     * @return ItemStack
     */
    public static ItemStack addLore(ItemStack is, String lore) {
        if (is != null) {
        	lore = ChatColor.translateAlternateColorCodes('&', lore);
            ItemMeta im = is.getItemMeta();
            if (im.hasLore()) {
                List<String> l = im.getLore();
                l.add(lore);
                im.setLore(l);
                is.setItemMeta(im);
                return is;
            }
            List<String> l = new ArrayList<>();
            l.add(lore);
            im.setLore(l);
            is.setItemMeta(im);
            return is;
        }
        throw new NullPointerException();
    }

    /**
     * 方法作用：替换指定的Lore
     * @param is 需要替换的物品
     * @param old 原Lore
     * @param newString 新Lore
     */
    public static ItemStack replaceLore(ItemStack is, String old, String newString) {
        if (is == null) {
            throw new NullPointerException();
        }
        ItemMeta im = is.getItemMeta();
        List<String> lore = im.getLore();
        if (!lore.contains(old)) {
            return is;
        }
        while (true) {
            if (!lore.contains(old)) {
                break;
            }
            lore.set(lore.indexOf(old), newString);
        }
        im.setLore(lore);
        is.setItemMeta(im);
        return is;
    }

	/**
	 * 方法作用：检查玩家是否拥有某物品
	 * @param player 玩家
	 * @param is 物品(ItemStack类型)
	 */
	 public static boolean hasItem(Player player, ItemStack is) {
		 Inventory inventory = player.getInventory();
		 ItemStack[] invItem = inventory.getContents();
		 int i = 0;
		 if (i < invItem.length) {
			 invItem[i].equals(is);
			 return true;
		 }
		 return false;
	 }
	
	/**
	 * 方法作用：取服务器在线玩家
	 */
	public static List<Player> getOnlinePlayers() {
		List <Player> players = new ArrayList<Player>();
		List <World> worlds = new ArrayList<World>();
		worlds.addAll(Bukkit.getWorlds());
		for(int i = 0;i < worlds.size();i++) {
			if(worlds.get(i).getPlayers().isEmpty()) {
				continue;
			}else {
				players.addAll(worlds.get(i).getPlayers());
			}
		}
		return players;
	}
	
	/**
	 * 方法作用：给玩家手中物品数量
	 * @param player 玩家
	 * @param amount 数量
	 */
	public static void setItemInHandAmount(Player player,int amount) {
		if(player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR) {
			return;
		}
		ItemStack is = player.getItemInHand();
		is.setAmount(amount);
	}
	/**
	 * 方法作用：给玩家物品和数量
	 * @param player 玩家
	 * @param is 物品(ItemStack类型)
	 * @param amount 数量
	 */
	public static void giveItemAndAmount(Player player,ItemStack is,int amount) {
		if(is == null || is.getType() == Material.AIR) {
			return;
		}
		is.setAmount(amount);
		player.getInventory().addItem(is);
	}
	
	/**
	 * 方法作用：设置玩家手中物品为空
	 * @param player 玩家
	 */
	public static void setPlayerItemNull(Player player) {
		if(player.getItemInHand() != null || player.getItemInHand().getType() != Material.AIR) {
			ItemStack air = new ItemStack(Material.AIR);
			player.setItemInHand(air);
		}
	}
	
	/**
	 * 方法作用：给一个玩家发送Title信息 1.8+
	 * @param player 发送的玩家
	 * @param fadeIn 淡入时间
	 * @param stay 停留时间
	 * @param fadeOut 淡出时间
	 * @param title 主标题
	 * @param subtitle 副标题
	 */
	@SuppressWarnings("rawtypes")
	public static void sendTitle(Player player, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle) {
		try {
			if (title != null) { //要发送的title
				title = ChatColor.translateAlternateColorCodes('&', title); //支持&颜色代码
                title = title.replaceAll("%player%", player.getDisplayName());
                Object enumTitle = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get(null);
                Object chatTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke(null, new Object[] { "{\"text\":\"" + title + "\"}" });
                Constructor titleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(new Class[] { getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE });
                Object titlePacket = titleConstructor.newInstance(new Object[] { enumTitle, chatTitle, fadeIn, stay, fadeOut });
                sendPacket(player, titlePacket);
            }
			if (subtitle != null) { //要发送的子title
				subtitle = ChatColor.translateAlternateColorCodes('&', subtitle); //支持&颜色代码
                subtitle = subtitle.replaceAll("%player%", player.getDisplayName());
                Object enumSubtitle = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("SUBTITLE").get(null);
                Object chatSubtitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke(null, new Object[] { "{\"text\":\"" + subtitle + "\"}" });
                Constructor subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(new Class[] { getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE });
                Object subtitlePacket = subtitleConstructor.newInstance(new Object[] { enumSubtitle, chatSubtitle, fadeIn, stay, fadeOut });
                sendPacket(player, subtitlePacket);
            }
			} catch (Exception e) {
				e.printStackTrace();
			}
    }
	
	/**
	 * 方法作用：取NMS类
	 * @param name 名字
	 */
	public static Class<?> getNMSClass(String name) {
		String version = org.bukkit.Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		try {
			return Class.forName("net.minecraft.server." + version + "." + name);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}return null;
	}
	
	/**
	 * 方法作用：向一个玩家发送数据包
	 * @param player 玩家
	 * @param packet 数据包
	 */
	public static void sendPacket(Player player, Object packet) {
		try {
			Object handle = player.getClass().getMethod("getHandle", new Class[0]).invoke(player, new Object[0]);
			Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
			playerConnection.getClass().getMethod("sendPacket", new Class[] { getNMSClass("Packet") }).invoke(playerConnection, new Object[] { packet });
		} catch (Exception e) {
			e.printStackTrace();
        }
	}
}
