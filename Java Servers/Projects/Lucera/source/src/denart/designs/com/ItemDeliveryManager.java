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

import l2.gameserver.ThreadPoolManager;
import l2.gameserver.scripts.ScriptFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class ItemDeliveryManager implements ScriptFile
{
	// logger
	private static final Logger _log = LoggerFactory.getLogger(ItemDeliveryManager.class);

	@Override
	public void onLoad()
	{
		_log.info("------------------ Item Delivery Manager ------------------");

		ThreadPoolManager.getInstance().scheduleAtFixedRate(new ItemDeliveryTask(), 100, 5000);

		_log.info(ItemDeliveryTask.class.getSimpleName() + ": started.");

		_log.info("-----------------------------------------------------------");
	}

	@Override
	public void onReload()
	{

	}

	@Override
	public void onShutdown()
	{

	}
}