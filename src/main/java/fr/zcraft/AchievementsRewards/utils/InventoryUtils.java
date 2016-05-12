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
package fr.zcraft.AchievementsRewards.utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public final class InventoryUtils
{
    private InventoryUtils() {}

    /**
     * Emulates the stack of two item stacks. Returns the item remaining after stack.
     * @param toStack The item to stack.
     * @param receiver The already present item. This item will not be updated at all.
     * @return The item stack after the stack. Amount will be 0 if it stacks perfectly; the same item
     * stack as the original one will be returned if the two items cannot be stacked at all.
     */
    public static ItemStack stack(ItemStack toStack, final ItemStack receiver)
    {
        if (!toStack.isSimilar(receiver))
            return toStack;

        toStack.setAmount(Math.max(0, toStack.getAmount() - (receiver.getMaxStackSize() - receiver.getAmount())));
        return toStack;
    }

    /**
     * Checks if a stack is empty.
     * @param stack The item stack
     * @return {@code true} if empty (null, AIR or amount = 0).
     */
    public static boolean isStackEmpty(final ItemStack stack)
    {
        return stack == null || stack.getType() == Material.AIR || stack.getAmount() == 0;
    }

    /**
     * Checks if the given list of items fits in the player's inventory.
     *
     * @param player The player
     * @param items The items.
     *
     * @return {@code true} if it fits.
     */
    public static boolean fitInInventory(final Player player, final List<ItemStack> items)
    {
        if (items.size() == 0) return true;

        final PlayerInventory inventory = player.getInventory();
        final Map<Material, ItemStack> itemsMap = new HashMap<>();

        items.forEach(item -> itemsMap.put(item.getType(), item.clone()));

        // First we try to place all in current stacks
        inventory.forEach(item -> {
            if (item != null && itemsMap.containsKey(item.getType()))
            {
                stack(itemsMap.get(item.getType()), item);
            }
        });

        // Then we use empty slots
        long emptySlots = Arrays.asList(inventory.getContents())
                .stream()
                .filter(item -> item == null || item.getType() == Material.AIR)
                .count();

        if (isStackEmpty(inventory.getHelmet()))        emptySlots--;
        if (isStackEmpty(inventory.getChestplate()))    emptySlots--;
        if (isStackEmpty(inventory.getLeggings()))      emptySlots--;
        if (isStackEmpty(inventory.getBoots()))         emptySlots--;
        if (isStackEmpty(inventory.getItemInOffHand())) emptySlots--;

        // If there is less empty slot than things to put inside, it doesn't fit
        if (emptySlots < itemsMap.values().stream().filter(item -> item.getAmount() > 0).count())
        {
            return false;
        }

        for (long slots = emptySlots; slots > 0; slots++)
        {
            Optional<ItemStack> stack = itemsMap.values().stream().filter(item -> item.getAmount() > 0).findAny();

            // If the list of items with non-null amounts is empty, they all fit in
            if (!stack.isPresent())
                return true;

            stack.get().setAmount(Math.max(0, stack.get().getAmount() - stack.get().getMaxStackSize()));
        }

        return itemsMap.values().stream().filter(item -> item.getAmount() > 0).count() == 0;
    }
}
