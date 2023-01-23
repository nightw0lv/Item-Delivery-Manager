/*
 * Copyright (c) 2023
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package denart.designs.com;

import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.Player;
import l2.gameserver.scripts.Functions;
import l2.gameserver.templates.item.ItemTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

/**
 * @Author Nightwolf
 * iToPz Discord: https://discord.gg/KkPms6B5aE
 * <p>
 * User Item Delivery System
 * Pack Support: Lucera
 * <p>
 * Personal Donate, Vote, Referral Panels: https://www.denart-designs.com/
 * Free Donate panel: https://itopz.com/
 */
public class ItemDeliveryTask implements Runnable
{
    private static final Logger _log = LoggerFactory.getLogger(ItemDeliveryTask.class);

    private final String UPDATE = "UPDATE user_item_delivery SET status=1 WHERE id=?;";
    private final String SELECT = "SELECT id, item_id, item_count, char_name FROM user_item_delivery WHERE status=0;";

    @Override
    public void run()
    {
        start();
    }

    private void start()
    {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(SELECT);
             ResultSet rset = statement.executeQuery())
        {
            while (rset.next())
            {
                final Player player = GameObjectsStorage.getPlayer(rset.getString("char_name"));
                final int id = rset.getInt("id");
                final int item_id = rset.getInt("item_id");
                final int item_count = rset.getInt("item_count");

                Optional.ofNullable(player).ifPresent(s ->
                {
                    if (UpdateDelivery(id))
                    {
                        final ItemTemplate item = ItemHolder.getInstance().getTemplate(item_id);
                        if (Objects.nonNull(item))
                        {
                            Functions.addItem(player, item_id, (long)item_count);
                            player.sendActionFailed();
                        }
                    }
                });
            }
        }
        catch (final Exception e)
        {
            _log.warn("Check delivery items failed. " + e.getMessage());
        }
    }

    private boolean UpdateDelivery(int id)
    {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(UPDATE))
        {
            statement.setInt(1, id);
            statement.execute();
            return true;
        }
        catch (SQLException e)
        {
            _log.warn("Failed to update item delivery id: " + id);
        }

        return false;
    }
}