package com.mygdx.game;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.utils.Align;

import datastructure.ReorderableArrayList;
import io.reactivex.rxjava3.core.Single;

public class FurnitureActor extends DepthOrderableActor{


    public FurnitureActor(AssetManager assetManager, ReorderableArrayList<DepthOrderableActor> siblings) {
        super(assetManager, siblings);
        createDirectionGroups();
        setOriginX(getWidth()/2);
    }
    private void createDirectionGroups(){
        MyActor top, bottom;
        DirectionCharacterActor topRight, bottomRight;
        top = new MyActor(assetManager);
        bottom = new MyActor(assetManager);
        top.setName("top");
        bottom.setName("bottom");
        top.setTexture(assetManager.get("t/lLegTemplate.png"), true);
        bottom.setTexture(assetManager.get("t/rLegTemplate.png"), true);
        initializeConfigs();
        
        //top
        this.addActor(top);
        top.setSiblings(children);
        directions.put(Align.top, top);

        //bottom
        this.addActor(bottom);
        bottom.setSiblings(children);
        directions.put(Align.bottom, bottom);
        
        
        Map<String, ActorConfig> directionConfig = directionConfigs.get(direction);
        setHeight(directionConfig.get("top").height);
        setWidth(directionConfig.get("top").width);
        
        this.setName("FurnitureActor");
    }
    public void initializeConfigs() {
        directionConfigs = new HashMap<>();
        
        // Top
        Map<String, ActorConfig> directionConfig = new HashMap<>();//TODO: actually position them 
        directionConfig.put("top", new ActorConfig(27, 0, 12, 20, 6, 17, 1));
        directionConfigs.put(Align.top, directionConfig);
        
        // Bottom
        directionConfig = new HashMap<>();//TODO: actually position them 
        directionConfig.put("bottom", new ActorConfig(0, 0, 50, 50, 25, 25, 1));
        directionConfigs.put(Align.bottom, directionConfig);
        
    }
    
    @Override
    public void act(float delta) {
        if (hasActions()) {
            for(Iterator<Action> iter = this.getActions().iterator(); iter.hasNext();){
                iter.next().act(delta);
            }
            changePlace();
        }
        directions.get(direction).act(delta);
    }
    
    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (isTransform()) applyTransform(batch, computeTransform());
        directions.get(direction).draw(batch, parentAlpha);
        if (isTransform()) resetTransform(batch);
    }
    public int getDirection() {
        return direction;
    }
    public void setDirection(int direction) {
        
        //TODO: clear actions of previous direction
        directions.get(direction).clearActions();
        this.direction = direction;
    }    




    @Override
    public String toString() {
        return "FurnitureActor [direction=" + direction + "]";
    }
    
}
