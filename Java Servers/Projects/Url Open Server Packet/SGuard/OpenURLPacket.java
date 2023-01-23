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
import l2s.gameserver.network.l2.s2c.L2GameServerPacket;

public class OpenURLPacket extends L2GameServerPacket implements IGuardPacket
{
	private final String url;
	public OpenURLPacket(String url)
	{
		this.url = url;
	}
	protected void writeImpl()
	{
		writeC(0xFF);
		writeC(0x03);
		writeS(this.url);
	}
}