/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.warcraft.object.feature;

import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.extractable.Extractable;
import com.b3dgs.lionengine.game.feature.tile.map.extractable.Extractor;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.MapTilePath;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.Pathfindable;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.TilePath;
import com.b3dgs.warcraft.Resources;
import com.b3dgs.warcraft.Sfx;
import com.b3dgs.warcraft.constant.Constant;

/**
 * Right click extraction implementation.
 */
@FeatureInterface
public class RightClickExtract extends FeatureModel implements RightClickHandler
{
    private final Cursor cursor;
    private final Handler handler;
    private final MapTile map;
    private final MapTilePath mapPath;

    @FeatureGet private Extractor extractor;
    @FeatureGet private Pathfindable pathfindable;

    /**
     * Create action.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public RightClickExtract(Services services, Setup setup)
    {
        super();

        cursor = services.get(Cursor.class);
        handler = services.get(Handler.class);
        map = services.get(MapTile.class);
        mapPath = map.getFeature(MapTilePath.class);
    }

    @Override
    public void execute()
    {
        final int tx = map.getInTileX(cursor);
        final int ty = map.getInTileY(cursor);
        pathfindable.setDestination(tx, ty);

        final Tile tree = map.getTile(tx, ty);
        if (Constant.CATEGORY_TREE.equals(tree.getFeature(TilePath.class).getCategory()))
        {
            extractor.setResource(Resources.TYPE_WOOD, tree);
            extractor.startExtraction();
        }
        else
        {
            for (final Integer id : mapPath.getObjectsId(tx, ty))
            {
                final Featurable featurable = handler.get(id);
                if (featurable.hasFeature(Extractable.class))
                {
                    final Extractable extractable = featurable.getFeature(Extractable.class);
                    extractor.setResource(extractable);
                    pathfindable.setDestination(extractable);
                    extractor.startExtraction();
                }
            }
        }
        Sfx.playRandomOrcConfirm();
    }
}