package com.mygdx.game;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.Align;

import datastructure.ReorderableArrayList;

public class DirectionCharacterActor extends MyActor {
    MyActor lLeg, rLeg, lArm, rArm, body, head;
    public MyActor[] parts = new MyActor[6];
    // Map<Integer,MyActor[]> directions = new HashMap<>(4);
    Map<String, ActorConfig> bodyPartConfig;
    public Map<String, MyActor> bodyPartMap;
    int direction;
    ReorderableArrayList<MyActor> children = new ReorderableArrayList<>();
    ReorderableArrayList<MyActor> siblings;

    public DirectionCharacterActor(AssetManager assetManager, int direction) {
        super(assetManager);
        this.direction = direction;
        bodyPartMap = new HashMap<>();
        this.addListener(new InputListener() {
			public boolean keyDown(InputEvent event, int keycode) {
				if (keycode == Keys.SPACE) {
                    System.out.println("event in "+toString());
				}
				return false;
			}
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("TouchDownEvent in "+toString());
                // if(bodyPartMap.get("lLeg").getActions().isEmpty()){
                //     bodyPartMap.get("lLeg").createWalkingAnimation(60, 1);
                // }else if (!bodyPartMap.get("lLeg").getActions().isEmpty()) {
                //     bodyPartMap.get("lLeg").resetRotation();
                //     bodyPartMap.get("lLeg").clearActions();
                // }
                // if(bodyPartMap.get("rLeg").getActions().isEmpty()){
                //     bodyPartMap.get("rLeg").createWalkingAnimation(-60, 1);
                // }else if(!bodyPartMap.get("rLeg").getActions().isEmpty()){
                //     bodyPartMap.get("rLeg").resetRotation();
                //     bodyPartMap.get("rLeg").clearActions();
                // }
                // if(bodyPartMap.get("lArm").getActions().isEmpty()){
                //     bodyPartMap.get("lArm").createWalkingAnimation(60, 1);
                // }else if (!bodyPartMap.get("lArm").getActions().isEmpty()) {
                //     bodyPartMap.get("lArm").resetRotation();
                //     bodyPartMap.get("lArm").clearActions();
                // }
                // if(bodyPartMap.get("rArm").getActions().isEmpty()){
                //     bodyPartMap.get("rArm").createWalkingAnimation(-60, 1);
                // }else if(!bodyPartMap.get("rArm").getActions().isEmpty()){
                //     bodyPartMap.get("rArm").resetRotation();
                //     bodyPartMap.get("rArm").clearActions();
                // }
                return true;
            }
		});
    }
    public DirectionCharacterActor(AssetManager assetManager, int direction,Map<String, ActorConfig> config) {
        super(assetManager);
        this.bodyPartConfig = config;
        this.direction = direction;
        bodyPartMap = new HashMap<>();
        
        this.addListener(new InputListener() {
			public boolean keyDown(InputEvent event, int keycode) {
				if (keycode == Keys.SPACE) {
                    System.out.println("event in "+toString());
				}
				return false;
			}
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("TouchDownEvent in "+toString());
                // if(bodyPartMap.get("lLeg").getActions().isEmpty()){
                //     bodyPartMap.get("lLeg").createWalkingAnimation(60, 1);
                // }else if (!bodyPartMap.get("lLeg").getActions().isEmpty()) {
                //     bodyPartMap.get("lLeg").resetRotation();
                //     bodyPartMap.get("lLeg").clearActions();
                // }
                return true;
            }
		});
    }
    public void addBodyParts(MyActor head, MyActor body, MyActor lArm, MyActor rArm, MyActor lLeg, MyActor rLeg){
        if(!this.getChildren().isEmpty()){
            this.clearChildren();
        }
        applyBodyPartConfig(head, "head");
        applyBodyPartConfig(body, "body");
        applyBodyPartConfig(lArm, "lArm");
        applyBodyPartConfig(rArm, "rArm");
        applyBodyPartConfig(lLeg, "lLeg");
        applyBodyPartConfig(rLeg, "rLeg");
        bodyPartMap.put("head", head);
        bodyPartMap.put("body", body);
        bodyPartMap.put("lArm", lArm);
        bodyPartMap.put("rArm", rArm);
        bodyPartMap.put("lLeg", lLeg);
        bodyPartMap.put("rLeg", rLeg);
        switch (direction) {
            case Align.top:
                parts[0]=lArm;
                parts[1]=lLeg;
                parts[2]=rLeg;
                parts[3]=body;
                parts[4]=head;
                parts[5]=rArm;
                this.setName("TopDirectionCharacterActor");
            break;
            
            case Align.bottom:
                parts[0]=rArm;
                parts[1]=rLeg;
                parts[2]=lLeg;
                parts[3]=body;
                parts[4]=head;
                parts[5]=lArm;
                this.setName("BottomDirectionCharacterActor");
                break;
                
            default:

            break;
        }

        for(MyActor actor : parts){
                this.addActor(actor);
                children.add(actor);
        }
    }
    private void applyBodyPartConfig(MyActor actor, String bodyPart){
        ActorConfig config = bodyPartConfig.get(bodyPart);
        actor.setPosition(config.x, config.y);
        actor.setSize(config.width, config.height);
        actor.setOrigin(config.originX, config.originY);    
        actor.setScale(config.scale);
    }
    
    
    @Override
    public void act(float delta) {
        act(delta, children);
    }
        
    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        drawChildren(batch, parentAlpha, children);
    }
    
    @Override
    public void applyWalkingAnimation(float amount, float timeMultiplier){
        bodyPartMap.get("lLeg").applyWalkingAnimation(amount, timeMultiplier);
        bodyPartMap.get("rLeg").applyWalkingAnimation(-amount, timeMultiplier);
        bodyPartMap.get("lArm").applyWalkingAnimation(amount, timeMultiplier);
        bodyPartMap.get("rArm").applyWalkingAnimation(-amount, timeMultiplier);
    }
    @Override
    public void cancelWalkingAnimation(){
        bodyPartMap.get("lLeg").cancelWalkingAnimation();
        bodyPartMap.get("rLeg").cancelWalkingAnimation();
        bodyPartMap.get("lArm").cancelWalkingAnimation();
        bodyPartMap.get("rArm").cancelWalkingAnimation();
    }

    public ReorderableArrayList<MyActor> getChildren2() {
        return children;
    }
    public int getDirection() {
        return direction;
    }
    public void setDirection(int direction) {
        this.direction = direction;
    }
    public void flipX() {
        this.setScaleX(this.getScaleX()*-1);
    }
    public ReorderableArrayList<MyActor> getSiblings() {
        return siblings;
    }
    public void setSiblings(ReorderableArrayList<MyActor> siblings) {
        this.siblings = siblings;
    }
    
}
