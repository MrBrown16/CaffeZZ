package com.mygdx.game;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;

import datastructure.ReorderableArrayList;

public class MyStage extends Stage{
    Batch batch;
    ReorderableArrayList<DepthOrderableActor> characters;
    public InputMultiplexer plexer;
    
    public MyStage(Viewport viewport, SpriteBatch batch, ReorderableArrayList<DepthOrderableActor> characters) {
        super(viewport, batch);
        this.batch = batch;
        this.characters = characters;
        plexer = new InputMultiplexer(this);
        this.addListener(new InputListener() {
			public boolean keyDown(InputEvent event, int keycode) {
				if (keycode == Keys.SPACE) {
                    System.out.println("KeyBoardEvent in "+toString());
				}
				return false;
			}
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("TouchDownEvent in "+toString());
                characters.forEach(a->{
                    if (a.getClass().isAssignableFrom(CharacterActor.class)) {
                        ((CharacterActor)a).goTo(x, y, 100f);
                        
                    }
                });
                return false;
            }
            
            
		});
    }
    
    @Override
    protected void actorRemoved(Actor actor) {
        characters.remove(actor);
        super.actorRemoved(actor);
    }
    
    @Override
    public void addActor(Actor actor) {
        if (FurnitureActor.class.isAssignableFrom(actor.getClass())) {
            System.out.println("characteractor added to characters: "+actor);
            characters.add((FurnitureActor)actor);
        }
        super.addActor(actor);
    }
    
    @Override
    public void act(float delta) {
        super.act(delta);
    }
    
    @Override
    public void draw() {
        // Camera camera = getViewport().getCamera();
		// camera.update();
        
		if (!getRoot().isVisible()) return;
        
		// batch.setProjectionMatrix(camera.combined);
		batch.begin();
        for(DepthOrderableActor actor : characters){
            actor.draw(batch, 1);
        }
		batch.end();
        
    }

    
    public ReorderableArrayList<DepthOrderableActor> getCharacters() {
        return characters;
    }
    
    public void setCharacters(ReorderableArrayList<DepthOrderableActor> characters) {
        this.characters = characters;
    }
}
