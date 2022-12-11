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
package extensions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ScheduledFuture;

import org.apache.log4j.Logger;
import net.l2jpx.gameserver.model.L2World;
import net.l2jpx.gameserver.model.actor.instance.L2PcInstance;
import net.l2jpx.gameserver.network.serverpackets.ActionFailed;
import net.l2jpx.gameserver.network.serverpackets.ItemList;
import net.l2jpx.gameserver.thread.ThreadPoolManager;
import net.l2jpx.util.CloseUtil;
import net.l2jpx.util.database.DatabaseUtils;
import net.l2jpx.util.database.L2DatabaseFactory;

public class ItemDeliveryManager
{
	static final Logger LOGGER = Logger.getLogger(ItemDeliveryManager.class);

	private final String UPDATE = "UPDATE user_item_delivery SET status=1 WHERE id=?;";
	private final String SELECT = "SELECT id, item_id, item_count, char_name FROM user_item_delivery WHERE status=0;";

	@SuppressWarnings("unused")
	private ScheduledFuture<?> _autoCheck;
	
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
		_autoCheck = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new CheckTask(), 5000, 5000);
		LOGGER.info("Item Delivery Manager: started.");
	}
	
	protected class CheckTask implements Runnable
	{
		@Override
		public void run()
		{
			int id = 0;
			int item_id = 0;
			int item_count = 0;
			String char_name = "";
			Connection con = null;
			try
			{
				con = L2DatabaseFactory.getInstance().getConnection(false);
				
				final PreparedStatement statement = con.prepareStatement(SELECT);
				final ResultSet rset = statement.executeQuery();
				while (rset.next())
				{
					id = rset.getInt("id");
					item_id = rset.getInt("item_id");
					item_count = rset.getInt("item_count");
					char_name = rset.getString("char_name");
					if (item_id > 0 && item_count > 0 && char_name != "")
					{
						for (L2PcInstance activeChar : L2World.getInstance().getAllPlayers())
						{
							if (activeChar == null || activeChar.isOnline() == false)
							{
								continue;
							}
							if (activeChar.getName().toLowerCase().equals(char_name.toLowerCase()))
							{
								activeChar.getInventory().addItem("Delivery", item_id, item_count, activeChar, null);
								activeChar.getInventory().updateDatabase();
								activeChar.sendPacket(new ItemList(activeChar, true));
								activeChar.sendMessage("Delivery of " + item_count + " coins.");
								UpdateDelivery(id);
								activeChar.sendPacket(ActionFailed.STATIC_PACKET);
							}
						}
					}
				}
				DatabaseUtils.close(rset);
				DatabaseUtils.close(statement);
			}
			catch (final SQLException e)
			{
				LOGGER.error("Check delivery items failed. " + e.getMessage());
			}
			finally
			{
				CloseUtil.close(con);
				con = null;
			}

		}
	}

	static void UpdateDelivery(int id)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection(false);
			statement = con.prepareStatement(UPDATE);
			statement.setInt(1, id);
			statement.execute();
			DatabaseUtils.close(statement);
		}
		catch (final Exception e)
		{
			LOGGER.error("Failed to update item delivery id: " + id);
		}
		finally
		{
			CloseUtil.close(con);
		}
	}
}