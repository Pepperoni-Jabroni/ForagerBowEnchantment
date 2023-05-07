package pepjebs.forager_bow_ench.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import pepjebs.forager_bow_ench.ForagerBowEnchantmentMod;

@Config(name = ForagerBowEnchantmentMod.MOD_ID)
public class ForagerBowEnchantConfig implements ConfigData {

    @ConfigEntry.Gui.Tooltip()
    @Comment("If true, Forager I will teleport XP. If false, teleport Items instead.")
    public boolean doExperienceAsLevelOne = true;

    @ConfigEntry.Gui.Tooltip()
    @Comment("If true, Forager will be possible to get from Enchanting Tables. If false, it will not.")
    public boolean isForagerAvailableForRandomSelection = false;

    @ConfigEntry.Gui.Tooltip()
    @Comment("If true, Forager will be possible to get from Enchanted Book Offers. If false, it will not.")
    public boolean isForagerAvailableForEnchantedBookOffer = false;

    @ConfigEntry.Gui.Tooltip()
    @Comment("If true, Forager will be possible to get from End City Chests. If false, it will not.")
    public boolean isForagerAvailableInEndCities = true;

    @ConfigEntry.Gui.Tooltip()
    @Comment("If true, the mod will print important debug events to the MC Log.")
    public boolean enableLifecycleDebugLogging = false;
}
