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

import net.sf.l2j.gameserver.network.serverpackets.L2GameServerPacket;

import ru.akumu.smartguard.core.network.packets.ISmartPacket;

public class OpenDonateWebsite extends L2GameServerPacket implements ISmartPacket
{
	private final String _url;

	public OpenDonateWebsite(String url)
	{
		_url = url;
	}

	@Override
	protected void writeImpl()
	{
		writeC(0xFF);
		writeC(0x03);
		writeS(_url);
	}
}