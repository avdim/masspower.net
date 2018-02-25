package com.riseofcat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Resources {
public static class Textures {
	public static final Texture tank = new Texture("green_tank.png");
	public static final Texture green = new Texture("green.png");
	public static final Texture blue = new Texture("blue.png");
	public static final Texture red = new Texture("red.png");
	public static final Texture yellow = new Texture("yellow.png");
	public static final Texture font = new Texture(Gdx.files.internal("dejavu_sans_mono.png"));
	public static Texture background;
	static {
		font.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		if(false) {
			background.setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.MirroredRepeat);
		}
	}
}
public static class Font {
	private static BitmapFont defaultFont = new BitmapFont();
	private static BitmapFont loadedFont = new BitmapFont(Gdx.files.internal("dejavu_sans_mono.fnt"), new TextureRegion(Textures.font));
	static {
		if(true) {
			defaultFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		}
	}
	public static BitmapFont defaultFont() {
		return defaultFont;
	}
	public static BitmapFont loadedFont() {
		loadedFont.setColor(Color.WHITE);
		loadedFont.getData().setScale(2);
		return loadedFont;
	}
}
public static void dispose() {
	Font.defaultFont.dispose();
	Font.loadedFont.dispose();
	Textures.tank.dispose();
	Textures.font.dispose();
	if(false) {
		Textures.background.dispose();
	}
}

}
