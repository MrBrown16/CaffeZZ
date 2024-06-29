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

public class CharacterActor extends DepthOrderableActor{


    public CharacterActor(AssetManager assetManager, ReorderableArrayList<DepthOrderableActor> siblings) {
        super(assetManager, siblings);
        createDirectionGroups();
        setOriginX(getWidth()/2);
    }
    private void createDirectionGroups(){
        MyActor lLeg, rLeg, lArm, rArm, body, head;
        DirectionCharacterActor topRight, bottomRight;
        lLeg = new MyActor(assetManager);
        rLeg = new MyActor(assetManager);
        lArm = new MyActor(assetManager);
        rArm = new MyActor(assetManager);
        body = new MyActor(assetManager);
        head = new MyActor(assetManager);
        lLeg.setName("lLeg");
        rLeg.setName("rLeg");
        lArm.setName("lArm");
        rArm.setName("rArm");
        head.setName("head");
        body.setName("body");
        lLeg.setTexture(assetManager.get("t/lLegTemplate.png"), true);
        rLeg.setTexture(assetManager.get("t/rLegTemplate.png"), true);
        lArm.setTexture(assetManager.get("t/lArmTemplate.png"), true);
        rArm.setTexture(assetManager.get("t/rArmTemplate.png"), true);
        head.setTexture(assetManager.get("t/headTemplate.png"), true);
        body.setTexture(assetManager.get("t/bodyTemplate.png"), true);
        initializeConfigs();

        //top
        topRight = new DirectionCharacterActor(assetManager, direction, directionConfigs.get(direction));
        this.addActor(topRight);
        topRight.setSiblings(children);
        ReorderableArrayList<MyActor> grandChildren = topRight.getChildren2();
        lLeg.setSiblings(grandChildren);
        rLeg.setSiblings(grandChildren);
        lArm.setSiblings(grandChildren);
        rArm.setSiblings(grandChildren);
        body.setSiblings(grandChildren);
        head.setSiblings(grandChildren);
        topRight.addBodyParts(head, body, lArm, rArm, lLeg, rLeg);
        directions.put(Align.top, topRight);

        //bottom
        bottomRight = new DirectionCharacterActor(assetManager, direction, directionConfigs.get(direction));
        this.addActor(bottomRight);
        bottomRight.setSiblings(children);
        grandChildren = bottomRight.getChildren2();
        lLeg.setSiblings(grandChildren);
        rLeg.setSiblings(grandChildren);
        lArm.setSiblings(grandChildren);
        rArm.setSiblings(grandChildren);
        body.setSiblings(grandChildren);
        head.setSiblings(grandChildren);
        bottomRight.addBodyParts(head, body, lArm, rArm, lLeg, rLeg);
        directions.put(Align.bottom, bottomRight);
        
        
        Map<String, ActorConfig> directionConfig = directionConfigs.get(direction);
        setHeight(directionConfig.get("lLeg").originY+directionConfig.get("body").originY+directionConfig.get("head").height);
        setWidth(directionConfig.get("head").width);
        
        this.setName("CharacterActor");
    }
    public void initializeConfigs() {
        directionConfigs = new HashMap<>();
        
        // Top
        Map<String, ActorConfig> directionConfig = new HashMap<>();//TODO: actually position them 
        directionConfig.put("lLeg", new ActorConfig(27, 0, 12, 20, 6, 17, 1));
        directionConfig.put("rLeg", new ActorConfig(32, 0, 12, 20, 6, 17, 1));
        directionConfig.put("lArm", new ActorConfig(30, 18, 12, 25, 6, 17.5f, 1));
        directionConfig.put("rArm", new ActorConfig(30, 18, 12, 25, 6, 17.5f, 1));
        directionConfig.put("body", new ActorConfig(21, 18, 28, 32, 14, 16, 1));
        directionConfig.put("head", new ActorConfig(0, 46, 70, 56, 35, 8, 1));
        directionConfigs.put(Align.top, directionConfig);
        
        // Bottom
        directionConfig = new HashMap<>();//TODO: actually position them 
        directionConfig.put("lLeg", new ActorConfig(0, 0, 50, 50, 25, 25, 1));
        directionConfig.put("rLeg", new ActorConfig(10, 0, 50, 50, 25, 25, 1));
        directionConfig.put("lArm", new ActorConfig(0, 50, 50, 50, 25, 25, 1));
        directionConfig.put("rArm", new ActorConfig(30, 50, 50, 50, 25, 25, 1));
        directionConfig.put("body", new ActorConfig(10, 50, 50, 50, 25, 25, 1));
        directionConfig.put("head", new ActorConfig(20, 50, 50, 50, 25, 25, 1));
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
    
    
    public Single<Boolean> goToObservable(float x, float y, float speed) {
        if(this.hasActions()){ //TODO: remove later if there are multiple actions applicable at once
            this.clearActions();
        }
        directions.get(direction).applyWalkingAnimation(60, 1); //TODO: handle case when direction changed between apply and cancel
        return Single.create(emitter -> {
            float x2 = x-getWidth()/2;
            float duration = (Float)((Double)Math.sqrt(Math.pow((x2-getX()), 2)+Math.pow((y-getY()), 2))).floatValue()/speed;
            MoveToAction moveToAction = new MoveToAction() {
                @Override
                protected void end() {
                    super.end();
                    emitter.onSuccess(true);
                }
            };
            moveToAction.setDuration(duration);
            moveToAction.setPosition(x2, y);

            this.addAction(moveToAction);
        });
    }
    
    public void goTo(float x, float y, float speed) {
        goToObservable(x, y, speed).subscribe((res)->{
            directions.get(direction).cancelWalkingAnimation();
            clearActions();
        });
    }






    @Override
    public String toString() {
        return "CharacterActor [direction=" + direction + "]";
    }
    
}
