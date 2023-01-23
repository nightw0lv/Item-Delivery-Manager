Some mythras packs have other versions if one not work try this code for ItemDeliveryManager.java

gameserver:
ThreadPoolManager.getInstance().scheduleAtFixedRate(new ItemDeliveryManager(), 5000L, 5000L);

and DonateGiverTaskManager:

package l2f.gameserver.taskmanager;

import l2f.commons.threading.RunnableImpl;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.World;
import l2f.gameserver.network.serverpackets.SystemMessage;
import l2f.gameserver.network.serverpackets.SystemMessage2;
import l2f.gameserver.network.serverpackets.components.ChatType;
import l2f.gameserver.utils.ItemFunctions;
import org.slf4j.LoggerFactory;

public class ItemDeliveryManager extends RunnableImpl
{
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(ItemDeliveryManager.class);

    private final String UPDATE = "UPDATE user_item_delivery SET status=1 WHERE id=?;";
    private final String SELECT = "SELECT id, item_id, item_count, char_name FROM user_item_delivery WHERE status=0;";

    @Override
    public void runImpl() throws Exception
    {
        int obj_id = 0;
        int id = 0;
        int item_id = 0;
        int item_count = 0;
        String char_name = null;

        try (Connection con = DatabaseFactory.getInstance().getConnection();
               PreparedStatement statement = con.prepareStatement(SELECT);
               ResultSet rset = statement.executeQuery();)
        {
            while (rset.next())
            {
                id = rset.getInt("id");
                item_id = rset.getInt("item_id");
                item_count = rset.getInt("item_count");
                char_name = rset.getString("char_name");
                Player activeChar;
                if (item_id > 0 && item_count > 0 && char_name != null)
                {
                    obj_id = selectPlayer(char_name);
                    if (obj_id == 0)
                        return;

                    activeChar = World.getPlayer(obj_id);
                    if (activeChar == null)
                        return;
                    if (activeChar.isOnline())
                    {
                        if (activeChar.getName().toLowerCase().equals(char_name.toLowerCase())) {
                            ItemFunctions.addItem(activeChar, item_id, item_count, false, "Delivery");
                            activeChar.getInventory().store();
                            activeChar.sendItemList(false);
                            // if item function set to false you can notify the user with one of these
                            // activeChar.sendPacket(SystemMessage2.obtainItems(item_id, item_count, 0));
                            // activeChar.sendChatMessage(activeChar.getObjectId(), 2, activeChar.getName(), "Received " + item_count + " coins.");

                            //activeChar.sendMessage("Received " + item_count + " Coin(s).");
                            //activeChar.sendMessage("Thank you for supporting our server!");

                            //activeChar.sendPacket(SystemMessage2.obtainItems(item_id, item_count, 0));
                            activeChar.sendChatMessage(activeChar.getObjectId(), 2, activeChar.getName(), "Received " + item_count + " Coin(s).");
                            activeChar.sendChatMessage(activeChar.getObjectId(), 2, activeChar.getName(), "Thank you for supporting our server!");
                            UpdateDelivery(id);
                            activeChar.sendActionFailed();
                        }
                    }
                }
            }
        }
        catch (SQLException e)
        {
            LOG.error("Check delivery items failed. " + e.getMessage());
        }
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
            LOG.error("Failed to update item delivery id: " + id);
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
            LOG.warn("Failed to remove donation from database char: " + playername);
            LOG.warn(e.getMessage());
        }
        return charId;
    }
}