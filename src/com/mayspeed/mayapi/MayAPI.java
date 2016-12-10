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
	 * �������ã�����һ����ҵ�Tab�б�
	 * @param player ���
	 * @param head Tab����
	 * @param foot Tab�ײ�
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
	 * �������ã�������ת��ΪUUID
	 * @param name Ҫ��д������
	 */
	public UUID translateNameToUUID(String name) {
		UUID uuid = null;
	    uuid = Bukkit.getPlayer(name).getUniqueId();
	    return uuid;
	}
	
	/**
	 * �������ã�����һ��Actionbar�����
	 * @param player ���
	 * @param message ��Ϣ
	 */
	public static void sendActionbar(Player player,String message) {
		IChatBaseComponent icbc = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + message + "\"}");
		PacketPlayOutChat ppoc = new PacketPlayOutChat(icbc, (byte)2);
		MayAPI.sendPacket(player, ppoc);
	}

	/**
	 * �������ã�ȡһ���б���ĳ��lore���ڵ�����
	 * @param lores �б�(List<String>)
	 * @param lore Ҫ�жϵ�����
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
	 * �������ã�����ĳ��Ʒָ��������Lore
	 * @param is ��Ʒ(ItemStack����)
	 * @param Line ����
	 * @param lore Ҫ���õ�
	 */
	public static void setLore(ItemStack is,int Line,String lore) {
		List <String> lores = new ArrayList<String>();
		if(is == null || is.getType() == Material.AIR) {
			throw new NullPointerException();
		}
		if(is.getItemMeta().hasLore()) {
			lores.addAll(is.getItemMeta().getLore());
			lores.set((Line - 1), lore.replaceAll("&", "��"));
			is.getItemMeta().setLore(lores);
			is.setItemMeta(is.getItemMeta());
		}else {
			return;
		}
	}
	
	/**
     * �������ã����Lore
     * @param is ��Ҫ���õ���Ʒ
     * @param lore ����ӵ�String
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
     * �������ã��滻ָ����Lore
     * @param is ��Ҫ�滻����Ʒ
     * @param old ԭLore
     * @param newString ��Lore
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
	 * �������ã��������Ƿ�ӵ��ĳ��Ʒ
	 * @param player ���
	 * @param is ��Ʒ(ItemStack����)
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
	 * �������ã�ȡ�������������
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
	 * �������ã������������Ʒ����
	 * @param player ���
	 * @param amount ����
	 */
	public static void setItemInHandAmount(Player player,int amount) {
		if(player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR) {
			return;
		}
		ItemStack is = player.getItemInHand();
		is.setAmount(amount);
	}
	/**
	 * �������ã��������Ʒ������
	 * @param player ���
	 * @param is ��Ʒ(ItemStack����)
	 * @param amount ����
	 */
	public static void giveItemAndAmount(Player player,ItemStack is,int amount) {
		if(is == null || is.getType() == Material.AIR) {
			return;
		}
		is.setAmount(amount);
		player.getInventory().addItem(is);
	}
	
	/**
	 * �������ã��������������ƷΪ��
	 * @param player ���
	 */
	public static void setPlayerItemNull(Player player) {
		if(player.getItemInHand() != null || player.getItemInHand().getType() != Material.AIR) {
			ItemStack air = new ItemStack(Material.AIR);
			player.setItemInHand(air);
		}
	}
	
	/**
	 * �������ã���һ����ҷ���Title��Ϣ 1.8+
	 * @param player ���͵����
	 * @param fadeIn ����ʱ��
	 * @param stay ͣ��ʱ��
	 * @param fadeOut ����ʱ��
	 * @param title ������
	 * @param subtitle ������
	 */
	@SuppressWarnings("rawtypes")
	public static void sendTitle(Player player, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle) {
		try {
			if (title != null) { //Ҫ���͵�title
				title = ChatColor.translateAlternateColorCodes('&', title); //֧��&��ɫ����
                title = title.replaceAll("%player%", player.getDisplayName());
                Object enumTitle = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get(null);
                Object chatTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke(null, new Object[] { "{\"text\":\"" + title + "\"}" });
                Constructor titleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(new Class[] { getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE });
                Object titlePacket = titleConstructor.newInstance(new Object[] { enumTitle, chatTitle, fadeIn, stay, fadeOut });
                sendPacket(player, titlePacket);
            }
			if (subtitle != null) { //Ҫ���͵���title
				subtitle = ChatColor.translateAlternateColorCodes('&', subtitle); //֧��&��ɫ����
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
	 * �������ã�ȡNMS��
	 * @param name ����
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
	 * �������ã���һ����ҷ������ݰ�
	 * @param player ���
	 * @param packet ���ݰ�
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
