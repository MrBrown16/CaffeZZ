package com.mygdx.game;

import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.actions.RotateByAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.Action;

import datastructure.ReorderableArrayList;

public class MyActor extends Group {
    AssetManager assetManager;
    public float isoDepth;
    public ReorderableArrayList<MyActor> children = new ReorderableArrayList<>();
    public ReorderableArrayList<MyActor> siblings;
    private Texture texture;

    public MyActor(AssetManager assetManager){
        this.assetManager = assetManager;
        texture = assetManager.get("badlogic.jpg");
    }
    public MyActor(AssetManager assetManager, float x, float y, float width, float height, float originX, float originY){
        this.assetManager = assetManager;
        texture = assetManager.get("badlogic.jpg");
        this.setPosition(x, y);
        this.setSize(width, height);
        this.setOrigin(originX, originY);
        calcIsoDepth(x, y);
    }
    @Override
    public void act(float delta) {
        for(Iterator<Action> iter = this.getActions().iterator(); iter.hasNext();){
            iter.next().act(delta);
        }
    }
    public void act (float delta, ArrayList<MyActor> children) {
        for (int i = 0, n = children.size(); i < n; i++){
            children.get(i).act(delta);
        }
	}


    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(new TextureRegion(texture), getX(), getY(), getOriginX(), getOriginY(),
                   getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
        super.draw(batch, parentAlpha);
    }

    public void drawChildren (Batch batch, float parentAlpha, ReorderableArrayList<MyActor> children) {
        if (isTransform()) applyTransform(batch, computeTransform());
		parentAlpha *= this.getColor().a;
		Rectangle cullingArea = this.getCullingArea();
		if (cullingArea != null) {
			// Draw children only if inside culling area.
			float cullLeft = cullingArea.x;
			float cullRight = cullLeft + cullingArea.width;
			float cullBottom = cullingArea.y;
			float cullTop = cullBottom + cullingArea.height;
			if (isTransform()) {
				for (int i = 0, n = children.size(); i < n; i++) {
					MyActor child = children.get(i);
					if (!child.isVisible()) continue;
					float cx = child.getX(), cy = child.getY();
					if (cx <= cullRight && cy <= cullTop && cx + child.getWidth() >= cullLeft && cy + child.getHeight() >= cullBottom)
						child.draw(batch, parentAlpha);
				}
			} else {
				// No transform for this group, offset each child.
				float offsetX = getX(), offsetY = getY();
				setPosition(0, 0);
				for (int i = 0, n = children.size(); i < n; i++) {
					MyActor child = children.get(i);
					if (!child.isVisible()) continue;
					float cx = child.getX(), cy = child.getY();
					if (cx <= cullRight && cy <= cullTop && cx + child.getWidth() >= cullLeft && cy + child.getHeight() >= cullBottom) {
                        child.setPosition(cx+offsetX, cy+offsetY);
						child.draw(batch, parentAlpha);
                        child.setPosition(cx, cy);
					}
				}
				setX(offsetX);
				setY(offsetY);
			}
		} else {
			// No culling, draw all children.
			if (isTransform()) {
				for (int i = 0, n = children.size(); i < n; i++) {
					MyActor child = children.get(i);
					if (!child.isVisible()) continue;
					child.draw(batch, parentAlpha);
				}
			} else {
				// No transform for this group, offset each child.
				float offsetX = getX(), offsetY = getY();
                setPosition(0, 0);
				for (int i = 0, n = children.size(); i < n; i++) {
					MyActor child = children.get(i);
					if (!child.isVisible()) continue;
					float cx = child.getX(), cy = child.getY();
                    child.setPosition(cx+offsetX, cy+offsetY);
					child.draw(batch, parentAlpha);
                    child.setPosition(cx, cy);
                }
				setX(offsetX);
				setY(offsetY);
			}
		}
		if (isTransform()) resetTransform(batch);
	}
    
    public float getIsometricDepth(){
        return isoDepth;
    }
    private void calcIsoDepth(float X, float Y){
        Vector2 iso = World.screenToMap(X, Y);
        isoDepth = World.calculateDepth(iso.x, iso.y);
    }
    private void calcIsoDepth(){
        Vector2 iso = World.screenToMap(getX(), getY());
        isoDepth = World.calculateDepth(iso.x, iso.y);
    }

    // public boolean contains(float X, float Y){
    //     // parts.contain(X,Y);
    //     return false;
    // }
    public void setChildren(ReorderableArrayList<MyActor> children){
        this.children = children;
    }
    public void setSiblings(ReorderableArrayList<MyActor> siblings){
        this.siblings = siblings;
    }
    protected void changePlace(){
        calcIsoDepth();
        // System.out.println("changePlace in: "+this.getName());
        children.moveToSortedPosition(this);
    }
    public void setTexture(Texture texture, Boolean setSize){
        this.texture = texture;
        if (setSize) {
            this.setSize(texture.getWidth(), texture.getHeight());
        }
    }

    public void resetRotation(){
        this.setRotation(0f);
    }

    //Animations:
    public void createWalkingAnimation(float amount, float timeMultiplier ) {
        // Rotate 30 degrees to the right
        RotateByAction rotateRight = new RotateByAction();
        rotateRight.setAmount(amount/2);
        rotateRight.setDuration(0.5f*timeMultiplier);

        // Rotate 60 degrees to the left (back to center and then 30 degrees left)
        RotateByAction rotateLeft = new RotateByAction();
        rotateLeft.setAmount(-amount);
        rotateLeft.setDuration(1f*timeMultiplier);

        // Rotate 30 degrees back to the center
        RotateByAction rotateBackToCenter = new RotateByAction();
        rotateBackToCenter.setAmount(amount/2);
        rotateBackToCenter.setDuration(0.5f*timeMultiplier);

        // Create a sequence action
        SequenceAction walkingSequence = new SequenceAction(rotateRight, rotateLeft, rotateBackToCenter);

        // Repeat the sequence indefinitely
        RepeatAction repeatWalking = new RepeatAction();
        repeatWalking.setAction(walkingSequence);
        repeatWalking.setCount(RepeatAction.FOREVER);

        // Add the repeating action to the actor
        System.out.println("walking action in: "+getName());
        this.addAction(repeatWalking);
        System.out.println("actionsNum: "+this.getActions().size);
    }
    public void applyWalkingAnimation(float amount, float timeMultiplier){
        createWalkingAnimation(amount, timeMultiplier);
    }
    public void cancelWalkingAnimation(){
        resetRotation();
        clearActions();
    }
    @Override
    public String toString() {
        if (this.getName() != null) {
            return "MyActor.name: "+this.getName();
        }
        return "MyActor []";
    }
    
}
