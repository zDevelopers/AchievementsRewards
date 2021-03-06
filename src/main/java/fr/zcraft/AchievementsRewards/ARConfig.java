/*
 * Copyright or © or Copr. AmauryCarrade (2015)
 * 
 * http://amaury.carrade.eu
 * 
 * This software is governed by the CeCILL-B license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
 * modify and/ or redistribute the software under the terms of the CeCILL-B
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info". 
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability. 
 * 
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or 
 * data to be ensured and,  more generally, to use and operate it in the 
 * same conditions as regards security. 
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-B license and that you accept its terms.
 */

package fr.zcraft.AchievementsRewards;

import fr.zcraft.zlib.components.configuration.Configuration;
import fr.zcraft.zlib.components.configuration.ConfigurationItem;
import fr.zcraft.zlib.components.configuration.ConfigurationList;
import fr.zcraft.zlib.components.configuration.ConfigurationMap;
import fr.zcraft.zlib.components.configuration.ConfigurationSection;
import org.bukkit.Achievement;
import org.bukkit.inventory.ItemStack;

import static fr.zcraft.zlib.components.configuration.ConfigurationItem.map;

public class ARConfig extends Configuration
{
    static public final ConfigurationMap<Achievement, RewardSection> REWARDS = map("rewards", Achievement.class, RewardSection.class);
            
    static public class RewardSection extends ConfigurationSection
    {
        public final ConfigurationList<ItemStack> ITEMS = list("items", ItemStack.class);
        public final ConfigurationItem<Integer> EXPERIENCE_LEVELS = item("levels", 0);
        public final ConfigurationItem<Double> HEALTH = item("health", .0);
        public final ConfigurationItem<String> MESSAGE = item("message", "");
    }
}
