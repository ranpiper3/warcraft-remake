/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.warcraft.action;

import java.util.concurrent.atomic.AtomicReference;

import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.core.drawable.Drawable;
import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.FeaturableModel;
import com.b3dgs.lionengine.game.Service;
import com.b3dgs.lionengine.game.Setup;
import com.b3dgs.lionengine.game.feature.DisplayableModel;
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionengine.game.feature.LayerableModel;
import com.b3dgs.lionengine.game.feature.RefreshableModel;
import com.b3dgs.lionengine.game.feature.actionable.Action;
import com.b3dgs.lionengine.game.feature.actionable.Actionable;
import com.b3dgs.lionengine.game.feature.actionable.ActionableModel;
import com.b3dgs.lionengine.game.feature.assignable.Assign;
import com.b3dgs.lionengine.game.feature.assignable.Assignable;
import com.b3dgs.lionengine.game.feature.assignable.AssignableModel;
import com.b3dgs.lionengine.game.feature.selector.Selector;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.lionengine.graphic.SpriteTiled;
import com.b3dgs.lionengine.graphic.Text;
import com.b3dgs.lionengine.io.Mouse;
import com.b3dgs.warcraft.Constant;

/**
 * Move action.
 */
public class ActionModel extends FeaturableModel
{
    protected final Actionable actionable;
    protected final Assignable assignable;
    protected final AtomicReference<Updatable> state;

    @Service protected Text text;
    @Service protected Cursor cursor;
    @Service protected Handler handler;
    @Service protected Selector selector;

    /**
     * Create move action.
     * 
     * @param setup The setup reference.
     */
    public ActionModel(Setup setup)
    {
        super();

        addFeature(new LayerableModel(Constant.LAYER_SELECTION));

        actionable = addFeatureAndGet(new ActionableModel(setup));
        actionable.setClickAction(Mouse.LEFT);
        actionable.setAction(new Action()
        {
            @Override
            public void execute()
            {
                cursor.setSurfaceId(1);
                selector.setEnabled(false);
                state.set(assignable);
                ActionModel.this.action();
            }
        });
        state = new AtomicReference<Updatable>(actionable);

        assignable = addFeatureAndGet(new AssignableModel());
        assignable.setClickAssign(Mouse.LEFT);
        assignable.setAssign(new Assign()
        {
            @Override
            public void assign()
            {
                ActionModel.this.assign();
                cursor.setSurfaceId(0);
                selector.setEnabled(true);
                state.set(actionable);
            }
        });

        final SpriteTiled surface = Drawable.loadSpriteTiled(setup.getSurface(), 27, 19);
        surface.setLocation(actionable.getButton().getX(), actionable.getButton().getY());

        addFeature(new RefreshableModel(new Updatable()
        {
            @Override
            public void update(double extrp)
            {
                if (actionable.isOver())
                {
                    text.setText(actionable.getDescription());
                }
                state.get().update(extrp);
                ActionModel.this.update(extrp);
            }
        }));

        addFeature(new DisplayableModel(new Renderable()
        {
            @Override
            public void render(Graphic g)
            {
                surface.render(g);
                ActionModel.this.render(g);
            }
        }));
    }

    protected void action()
    {
        // Nothing by default
    }

    protected void assign()
    {
        // Nothing by default
    }

    protected void update(double extrp)
    {
        // Nothing by default
    }

    protected void render(Graphic g)
    {
        // Nothing by default
    }
}
