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

public class DepthOrderableActor extends MyActor{
    AssetManager assetManager;
    Map<Integer,MyActor> directions = new HashMap<>(4);
    int direction;
    Map<Integer,Map<String, ActorConfig>> directionConfigs;
    ReorderableArrayList<DepthOrderableActor> siblings;


    public DepthOrderableActor(AssetManager assetManager, ReorderableArrayList<DepthOrderableActor> siblings) {
        super(assetManager);
        this.assetManager = assetManager;
        this.siblings = siblings;
        direction = Align.top;
        createDirectionGroups();
        setOriginX(getWidth()/2);
        this.setName("DepthOrderableActor");
    }
    private void createDirectionGroups(){
        
    }
    public void initializeConfigs() {
        
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
        return "DepthOrderableActor [direction=" + direction + "]";
    }
    
}
