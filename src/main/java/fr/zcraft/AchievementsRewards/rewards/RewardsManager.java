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
package fr.zcraft.AchievementsRewards.rewards;

import fr.zcraft.AchievementsRewards.commands.ARGetCommand;
import fr.zcraft.zlib.components.rawtext.RawText;
import fr.zcraft.zlib.tools.text.RawMessage;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Achievement;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


public class RewardsManager
{
    private static final String CHAT_SEPARATOR = ChatColor.GRAY + "-----------------------------------------------------";


    private Map<Achievement, Reward> rewards = new HashMap<>();
    private Map<UUID, Set<Achievement>> rewarded = new HashMap<>();


    public RewardsManager register(Achievement achievement, Reward reward)
    {
        rewards.put(achievement, reward);
        return this;
    }

    public Reward getForAchievement(Achievement achievement)
    {
        return rewards.get(achievement);
    }

    /**
     * Gives the XP and health rewards to the given player, and sends him an infobox with his rewards and a link to
     * get them.
     *
     * If the player does not have the achievement, or if he already received the rewards, does nothing.
     *
     * This should be called when the player earned the achievement (not directly in the event).
     *
     * @param achievement The achievement.
     * @param player The player.
     */
    public void preGive(Achievement achievement, Player player)
    {
        Reward reward = getForAchievement(achievement);

        if (reward == null
                || !player.hasAchievement(achievement)
                || rewarded.containsKey(player.getUniqueId()) && rewarded.get(player.getUniqueId()).contains(achievement))
        {
            return;
        }

        if (reward.getHealth() > 0)
        {
            player.setHealth(player.getHealth() + Math.min(player.getMaxHealth() - player.getHealth(), reward.getHealth()));
        }

        if (reward.getXpLevels() > 0)
        {
            player.giveExpLevels(reward.getXpLevels());
        }

        if (reward.getHealth() > 0 || reward.getXpLevels() > 0 || !reward.getItems().isEmpty())
        {
            player.sendMessage(CHAT_SEPARATOR);

            RawMessage.send(player,
                    new RawText("")
                        .then("Félicitations pour ce succès ! ")
                            .style(ChatColor.BOLD, ChatColor.DARK_GREEN)
                            .hover(achievement)
                        .then("Vous avez obtenu...")
                            .color(ChatColor.GREEN)
                    .build()
            );

            if (reward.getHealth() > 0)
            {
                RawMessage.send(player,
                        new RawText("- ")
                                .color(ChatColor.DARK_GRAY)
                            .then(String.valueOf((int) reward.getHealth()))
                                .color(ChatColor.DARK_AQUA)
                            .then(" point" + (reward.getHealth() > 1 ? "s" : "") + " de vie")
                                .color(ChatColor.AQUA)
                        .build()
                );
            }

            if (reward.getXpLevels() > 0)
            {
                RawMessage.send(player,
                        new RawText("- ")
                                .color(ChatColor.DARK_GRAY)
                            .then(String.valueOf(reward.getXpLevels()))
                                .color(ChatColor.DARK_AQUA)
                            .then(" niveau" + (reward.getHealth() > 1 ? "x" : "") + " d'XP")
                                .color(ChatColor.AQUA)
                        .build()
                );
            }

            for (ItemStack item : reward.getItems())
            {
                RawMessage.send(player,
                        new RawText("- ")
                                .color(ChatColor.DARK_GRAY)
                            .then(String.valueOf(item.getAmount()) + " × ")
                                .color(ChatColor.DARK_AQUA)
                            .then(StringUtils.capitalize(item.getType().toString().toLowerCase().replace("_", " ")))
                                .color(ChatColor.AQUA)
                                .hover(item)
                        .build()
                );
            }

            RawMessage.send(player,
                    new RawText("")
                        .then("»» ")
                            .style(ChatColor.DARK_GREEN, ChatColor.BOLD)
                        .then("Cliquez ici pour récupérer votre récompense")
                            .style(ChatColor.GREEN, ChatColor.BOLD)
                            .hover(new RawText("Vous recevrez vos gains dans l'inventaire, ou au sol"))
                            .command(ARGetCommand.class, achievement.name())
                        .then(" ««")
                            .style(ChatColor.DARK_GREEN, ChatColor.BOLD)
                    .build()
            );

            player.sendMessage(CHAT_SEPARATOR);
        }
    }

    /**
     * Gives the reward items to a player.
     *
     * @param achievement The achievement.
     * @param player The player.
     */
    public boolean give(Achievement achievement, Player player)
    {
        final Reward reward = getForAchievement(achievement);
        final UUID uuid = player.getUniqueId();

        if (reward == null
                || !player.hasAchievement(achievement)
                || rewarded.containsKey(uuid) && rewarded.get(uuid).contains(achievement))
        {
            return false;
        }

        if (!rewarded.containsKey(uuid))
            rewarded.put(uuid, new HashSet<>());

        rewarded.get(uuid).add(achievement);

        Map<Integer, ItemStack> notAdded = player.getInventory().addItem(reward.getItems().toArray(new ItemStack[reward.getItems().size()]));
        for (ItemStack item : notAdded.values())
            player.getWorld().dropItem(player.getLocation(), item);

        return true;
    }
}
