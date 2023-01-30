/**
 * @Author    DenArt Designs
 * @Developer Nightwolf
 *
 * Documentation: https://shop.denart-designs.com/documentation/
 * Purchased: https://shop.denart-designs.com/
 * License: https://shop.denart-designs.com/activate.php?license
 * License Activation: https://shop.denart-designs.com/activate.php ( ^ first you must purchase the license)
 *
 * Created for DenArt Designs that holds the ownership of this files
 * Removing this constitutes violation of the agreement.
 */
package l2r.gameserver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import l2r.L2DatabaseFactory;
import l2r.gameserver.model.L2World;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.network.serverpackets.ActionFailed;
import l2r.gameserver.network.serverpackets.InventoryUpdate;

public class ItemDeliveryManager
{
	private static Logger _log = Logger.getLogger(ItemDeliveryManager.class.getName());

	private final static String UPDATE = "UPDATE user_item_delivery SET status=1 WHERE id=?;";
	private final static String SELECT = "SELECT id, item_id, item_count, char_name FROM user_item_delivery WHERE status=0;";

	public static ItemDeliveryManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final ItemDeliveryManager _instance = new ItemDeliveryManager();
	}
	
	protected ItemDeliveryManager()
	{
		_log.info("Item Delivery Manager: started.");
		
		ThreadPoolManager.getInstance().scheduleGeneral(() ->
		{
			Logger __log = Logger.getLogger(ItemDeliveryManager.class.getName());
			String charName = null;
			int id = 0;
			int item_id = 0;
			int item_count = 0;
			String char_name = null;
			try (Connection con = L2DatabaseFactory.getInstance().getConnection();
				PreparedStatement statement = con.prepareStatement(SELECT))
			{
				try (ResultSet rset = statement.executeQuery())
				{
					while (rset.next())
					{
						id = rset.getInt("id");
						item_id = rset.getInt("item_id");
						item_count = rset.getInt("item_count");
						char_name = rset.getString("char_name");
						if ((item_id > 0) && (item_count > 0) && (char_name != null))
						{
							int obj_id = selectPlayer(char_name);
							for (L2PcInstance activeChar : L2World.getInstance().getPlayers())
							{
								if ((activeChar == null) || (activeChar.isOnline() == false))
								{
									continue;
								}
								if (activeChar.getObjectId() == obj_id)
								{
									if (activeChar.getName().toLowerCase().equals(char_name.toLowerCase()))
									{
										charName = activeChar.getName();
										activeChar.getInventory().addItem("Delivery", item_id, item_count, activeChar, null);
										activeChar.sendItemList(false);
										activeChar.sendMessage("Delivery of " + item_count + " coins.");
										UpdateDelivery(id);
										
										InventoryUpdate iu = new InventoryUpdate();
										activeChar.sendInventoryUpdate(iu);
										
										activeChar.sendPacket(ActionFailed.STATIC_PACKET);
									}
								}
							}
						}
					}
				}
			}
			catch (Exception e)
			{
				_log.warning("Check delivery items failed. " + e.getMessage());
			}
		}, 5000L);
	}

	private static void UpdateDelivery(int id)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(UPDATE))
		{
			statement.setInt(1, id);
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.warning("Failed to update item delivery id: " + id);
		}
	}
	
	private static int selectPlayer(String playername)
	{
		int charId = 0;
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT * FROM `characters` WHERE `char_name`=? LIMIT 1;"))
		{
			statement.setString(1, playername);
			try (ResultSet rset3 = statement.executeQuery())
			{
				while (rset3.next())
				{
					charId = rset3.getInt("charId");
				}
			}
		}
		catch (SQLException e)
		{
			_log.warning("Failed to get char id from: " + playername + " " + e.getMessage());
		}
		return charId;
	}
}