/**
 * @Author    DenArt Designs
 * @Developer Nightwolf
 *
 * Documentation: https://shop.denart-designs.com/documentation/
 * Purchased: https://shop.denart-designs.com/
 * License: https://shop.denart-designs.com/activate.php?license
 * License Activation: https://shop.denart-designs.com/activate.php ( ^ first you must purchase the license)
 * Created for DenArt Designs that holds the ownership of these files,
 * Removing this constitutes violation of the agreement.
 */
package services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import l2e.gameserver.ThreadPoolManager;
import l2e.gameserver.database.DatabaseFactory;
import l2e.gameserver.model.GameObjectsStorage;
import l2e.gameserver.model.actor.Player;

/**
 * @Author Nightwolf
 * Adapted by LordWinter for l2jeternity
 *
 * https://l2jeternity.com/
 */
public class ItemDeliveryManager
{
	private static final Logger _log = Logger.getLogger(ItemDeliveryManager.class.getName());

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

	public ItemDeliveryManager()
	{
		_log.info("Item Delivery Manager: started.");

		ThreadPoolManager.getInstance().scheduleAtFixedRate(() ->
		{
			final Logger __log = Logger.getLogger(ItemDeliveryManager.class.getName());
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
						item_count = rset.getInt("item_count");
						char_name = rset.getString("char_name");
						if ((item_id > 0) && (item_count > 0) && (char_name != null))
						{
							final Player player = GameObjectsStorage.getPlayer(char_name);
							if (player != null && player.isOnline())
							{
								player.addItem("Delivery", item_id, item_count, player, true);
								player.sendMessage("Delivery of " + item_count + " Donate Coins.");
								UpdateDelivery(id);
							}
						}
					}
				}
			}
			catch (final Exception e)
			{
				_log.warning("Check delivery items failed. " + e.getMessage());
			}
		}, 5000L, 5000L);
	}

	private void UpdateDelivery(int id)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection();
				PreparedStatement statement = con.prepareStatement(UPDATE))
		{
			statement.setInt(1, id);
			statement.execute();
		}
		catch (final SQLException e)
		{
			_log.warning("Failed to update item delivery id: " + id);
		}
	}

	public static void main(String[] args)
	{
		new ItemDeliveryManager();
	}
}