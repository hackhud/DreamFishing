package ua.hackhud.dreamFishing.hologram;

import ua.hackhud.dreamFishing.entities.Lake;
import ua.hackhud.dreamFishing.util.TextUtils;
import ua.hackhud.dreamholohandler.HoloManager;
import ua.hackhud.dreamholohandler.Hologram;


import java.util.HashMap;
import java.util.Map;

public class HologramInitializer {

    private final HoloManager holoManager;
    private final HashMap<String, Hologram> holograms;

    public HologramInitializer(HoloManager holoManager) {
        this.holoManager = holoManager;
        this.holograms = holoManager.getHolograms();
    }

    public void initLakeHolograms(Map<String, Lake> lakes) {
        for (Lake lake : lakes.values()) {
            Hologram template = lake.getHologram();
            if (template == null) continue;

            String renderedText = TextUtils.replacePlaceholders(template.text, lake);
            Hologram active = holograms.get(lake.getName());
            if (active != null) {
                holoManager.updateHologram(lake.getName(), active, active.scale, renderedText);
                continue;
            }

            Hologram clone = new Hologram(template.x, template.y, template.z, template.scale, renderedText, template.dimensionId, -1);
            holoManager.createHologram(lake.getName(), clone);
        }
    }

}
