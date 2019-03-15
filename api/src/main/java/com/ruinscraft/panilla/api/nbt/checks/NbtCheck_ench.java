package com.ruinscraft.panilla.api.nbt.checks;

import org.bukkit.enchantments.Enchantment;

import com.ruinscraft.panilla.api.nbt.INbtTagCompound;
import com.ruinscraft.panilla.api.nbt.INbtTagList;
import com.ruinscraft.panilla.api.nbt.NbtDataType;

public class NbtCheck_ench implements NbtCheck {

	@Override
	public String getName() {
		return "ench";
	}

	@Override
	public String[] getAliases() {
		return new String[] {"Enchantments", "StoredEnchantments"};
	}

	@Override
	public boolean check(INbtTagCompound tag, String nmsItemClassName) {
		String using = null;

		if (tag.hasKey("ench")) {
			using = "ench";
		} else {
			for (String alias : getAliases()) {
				if (tag.hasKey(alias)) {
					using = alias;
				}
			}
		}

		if (using != null) {
			INbtTagList enchantments = tag.getList(using, NbtDataType.COMPOUND);

			for (int i = 0; i < enchantments.size(); i++) {
				Enchantment current = Enchantment.getById(enchantments.get(i).getShort("id"));	// TODO: does not exist in 1.13
				int lvl = 0xFFFF & enchantments.get(i).getShort("lvl");

				if (lvl > current.getMaxLevel()) {
					return false;
				}

				if (lvl < current.getStartLevel()) {
					return false;
				}

				for (int j = 0; j < enchantments.size(); j++) {
					Enchantment other = Enchantment.getById(enchantments.get(j).getShort("id")); // TODO: does not exist in 1.13

					if (current != other && current.conflictsWith(other)) {
						return false;
					}
				}
			}
		}

		return true;
	}

}
