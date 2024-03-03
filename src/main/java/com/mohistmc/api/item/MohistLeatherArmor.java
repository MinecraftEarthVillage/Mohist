package com.mohistmc.api.item;

import java.util.Random;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class MohistLeatherArmor {

    private LeatherArmorMeta lm;
    private ItemStack item;

    public MohistLeatherArmor(ItemStack armor) {
        this.item = armor;
        this.lm = (LeatherArmorMeta)this.item.getItemMeta();
    }

    public MohistLeatherArmor setColor(Color color) {
        this.lm.setColor(color);
        return this;
    }

    public MohistLeatherArmor setRandomColor() {
        this.lm.setColor(Color.fromRGB(this.randomColor(255) + 1, this.randomColor(255) + 1, this.randomColor(255) + 1));
        return this;
    }

    private int randomColor(int max) {
        Random r = new Random();
        return r.nextInt(max);
    }

    public MohistLeatherArmor buildItemMeta() {
        this.item.setItemMeta((ItemMeta)this.lm);
        return this;
    }

    public ItemStack build() {
        return this.item;
    }
}
