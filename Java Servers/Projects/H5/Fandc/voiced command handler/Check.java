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
package l2f.gameserver.handler.voicecommands.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import l2f.commons.dbutils.DbUtils;
import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.World;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.utils.ItemFunctions;

public class Check extends Functions implements IVoicedCommandHandler
{
	// for manual players use .check in order to get their items
	// dont forget to register the handler to IVoicedCommandHandler
	private static Logger _log = Logger.getLogger(Check.class.getName());
	
	private final static String UPDATE = "UPDATE user_item_delivery SET status=1 WHERE id=?;";
	private final static String SELECT = "SELECT id, item_id, item_count, char_name FROM user_item_delivery WHERE status=0;";
	
	private static final String[] COMMANDS =
	{
		"check"
	};

	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String target)
	{
		if(activeChar == null)
			return false;
		if (command.startsWith("check"))
		{
			int id = 0;
			int item_id = 0;
			int item_count = 0;
			String char_name = "";
			Connection con = null;
			PreparedStatement statement = null;
			try 
			{
				con = DatabaseFactory.getInstance().getConnection();
				statement = con.prepareStatement(SELECT);
				statement.setString(1, activeChar.getName());
				ResultSet rset = statement.executeQuery();
				if (rset.next())
				{
					id = rset.getInt("id");
					item_id = rset.getInt("item_id");
					item_count = rset.getInt("item_count");
					char_name = rset.getString("char_name");
					for (Player player : World.getPlayer(activeChar.getName()))
					{
						if (player == null || player.isOnline() == false)
						{
							continue;
						}
						if (player.getName().toLowerCase().equals(char_name.toLowerCase()))
						{
							ItemFunctions.addItem(player, item_id, item_count, true, "Delivery");
							player.getInventory().store();
							player.sendItemList(false);
							player.sendMessage("Delivery of " + item_count + " coins.");
							UpdateDelivery(id);
							player.sendActionFailed();
						}
					}
					return true;
				}
				else activeChar.sendMessage("We could not find your donation.");
			}
			catch (Exception e)
			{
				_log.warning("Check delivery items failed. " + e.getMessage());
			}
			finally
			{
				DbUtils.closeQuietly(con, statement);
			}
		}
		return true;
	}
	
	private static void UpdateDelivery(int id)
	{
		Connection con = null;
		PreparedStatement statement = null;
		
		try 
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(UPDATE);
			statement.setInt(1, id);
			statement.executeUpdate();
		}
		catch (SQLException e)
		{
			_log.warning("Failed to update item delivery id: " + id);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return COMMANDS;
	}
}