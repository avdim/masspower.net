package com.riseofcat;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class GradientShapeRect extends Actor {
private final int width;
private final int height;
private ShapeRenderer shapeRenderer = new ShapeRenderer(20);
private int bold = 3;
public GradientShapeRect(int width, int height) {
	this.width = width;
	this.height = height;
}
public void draw(Batch batch, float parentAlpha) {
	float x = getX();
	float y = getY();
	batch.end();//todo bad
	shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
	shapeRenderer.setColor(Color.GREEN);
	shapeRenderer.rect(x - bold, y - bold, width + bold * 2, height + bold * 2);
	shapeRenderer.rect(x, y, width, height, Color.BLUE, Color.GREEN, Color.RED, Color.ORANGE);
	shapeRenderer.end();
	batch.begin();
	super.draw(batch, parentAlpha);
}
}
