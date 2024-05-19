package org.fireflyest.essential.world;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.fireflyest.craftmap.MapCanvasDrawer;

public class DomainMapRenderer extends MapRenderer {

    private final Dimension dimension;

    public DomainMapRenderer(Dimension dimension) {
        this.dimension = dimension;
    }

    @Override
    public void render(MapView map, MapCanvas canvas, Player player) {
        MapCanvasDrawer drawer = new MapCanvasDrawer(canvas);
        int x = map.getCenterX();
        int z = map.getCenterZ();
        for (int i = x - 64; i < x + 64; i += 16) {
            for (int j = z - 64; j < z + 64; j += 16) {
                String loc = i/16 + ":" + j/16;
                Plot plot = dimension.getPlot(loc);
                if (plot != null) {
                    byte color = getColor(plot.getDomain().getType());
                    for (int index = 0; index < 15; index+=2) {
                        int mx = plot.getX() * 16 + 64 - x;
                        int mz = plot.getZ() * 16 + 64 - z;
                        drawer.drawLine(mx + index, mz, mx - 1, mz + index + 1, color);
                        drawer.drawLine(mx + 15 - index, mz + 15, mx + 15 + 1, mz + 15 - index -1, color);
                    }
                }
            }
        }
    }

    /**
     * 获取区域颜色
     */
    private byte getColor(int type) {
        switch (type) {
            case Dimension.LEAGUE:
                return (byte)9;
            case Dimension.SERVER:
                return (byte)82;
            case Dimension.PLAYER:
            default:
                return (byte)126;
        }
    }

}
