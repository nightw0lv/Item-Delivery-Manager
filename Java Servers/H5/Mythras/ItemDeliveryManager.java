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
package l2f.gameserver.taskmanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import l2f.commons.threading.RunnableImpl;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.World;
import l2f.gameserver.utils.ItemFunctions;

public class ItemDeliveryManager
{
	private static Logger _log = Logger.getLogger(ItemDeliveryManager.class.getName());

	private final String UPDATE = "UPDATE user_item_delivery SET status=1 WHERE id=?;";
	private final String SELECT = "SELECT id, item_id, item_count, char_name FROM user_item_delivery WHERE status=0;";

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
		
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new RunnableImpl()
		{
			@Override
			public void runImpl()
			{
				Logger __log = Logger.getLogger(ItemDeliveryManager.class.getName());
				String charName = null;
				int id = 0;
				int item_id = 0;
				int item_count = 0;
				String char_name = null;
				try (Connection con = DatabaseFactory.getInstance().getConnection();
						PreparedStatement statement = con.prepareStatement(SELECT))
				{
					try (ResultSet rset = statement.executeQuery())
					{
						while (rset.next())
						{
							id = rset.getInt("id");
							item_id = rset.getInt("item_id");
							item_count = rset.getInt("count");
							char_name = rset.getString("char_name");
							if (item_id > 0 && item_count > 0 && char_name != null)
							{
								int obj_id = selectPlayer(char_name);
								for (Player activeChar : World.getPlayer(obj_id))
								{
									if (activeChar == null || activeChar.isOnline() == false)
									{
										continue;
									}
									if (activeChar.getName().toLowerCase().equals(char_name.toLowerCase()))
									{
										charName = activeChar.getName();
										ItemFunctions.addItem(activeChar, item_id, item_count, true, "Delivery");
										activeChar.getInventory().store();
										activeChar.sendItemList(false);
										activeChar.sendMessage("Delivery of " + item_count + " coins.");
										UpdateDelivery(id);
										activeChar.sendActionFailed();
									}
								}
							}
						}
					}
				}
				catch (Exception e)
				{
					__log.warning("Check delivery items failed. " + e.getMessage());
				}
			}
		}, 5000L, 5000L);
	}
	
	private static void UpdateDelivery(int id)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection();
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
		int charId=0;
		try (Connection con = DatabaseFactory.getInstance().getConnection();
				PreparedStatement statement = con.prepareStatement("SELECT * FROM `characters` WHERE `char_name`=? LIMIT 1;"))
		{
			statement.setString(1, playername);
			try (ResultSet rset3 = statement.executeQuery())
			{
				while (rset3.next())
				{
					charId = rset3.getInt("obj_Id");
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