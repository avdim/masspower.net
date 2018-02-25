package com.riseofcat;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.n8cats.lib_gwt.LibAllGwt;
import com.n8cats.share.Logic;
import com.riseofcat.redundant.ShapeRenderer2;
import com.riseofcat.reflect.Conf;

public class Core extends ApplicationAdapter {
private SpriteBatch batch;
private SpriteBatch backgroundBatch;
private ShapeRenderer2 shapeRenderer;
private Model model;
private Viewport viewport1;
private Viewport viewport2;
private Stage stage;
private static final Color[] colors = {Color.BLUE, Color.GOLD, Color.PINK, Color.RED, Color.GREEN, Color.VIOLET, Color.LIME, Color.TEAL, Color.YELLOW};
private static final boolean MULTIPLE_VIEWPORTS = false;
private static final boolean BACKGROUND_BATCH = false;
private static final boolean BACKGROUND_MESH = true;
private static final boolean DRAW_GRID = true;
private ShaderProgram backgroundBatchShader;
private ShaderProgram batchShader;
private Mesh mesh;
private ShaderProgram meshShader;
public Core(App.Context context) {
	App.context = context;
}
public void create() {
	final FileHandle defaultVertex = Gdx.files.internal("shader/default_vertex_shader.vert");
	ShaderProgram.pedantic = false;
	App.create();//todo
	batch = new SpriteBatch();
	if(BACKGROUND_BATCH) {
		backgroundBatchShader = new ShaderProgram(defaultVertex, Gdx.files.internal("shader/background/stars.frag"));
		if(!backgroundBatchShader.isCompiled()) App.log.error(backgroundBatchShader.getLog());
		backgroundBatch = new SpriteBatch();
		backgroundBatch.setShader(backgroundBatchShader);
	}
	if(BACKGROUND_MESH) {
		mesh = new Mesh(true, 4, 6, new VertexAttribute(VertexAttributes.Usage.Position, 2, "aVertexPosition"));
		mesh.setVertices(new float[] {-1.0f,1.0f, -1.0f,-1.0f, 1.0f,-1.0f, 1.0f,1.0f});
		mesh.setIndices(new short[]{0, 1, 2, 2, 3, 0});
		meshShader = new ShaderProgram(Gdx.files.internal("shader/mesh/default.vert"),
				Gdx.files.internal("shader/background/stars.frag"));
		if(!meshShader.isCompiled()) App.log.error(meshShader.getLog());
	}
	viewport1 = new ExtendViewport(1000f, 1000f, new OrthographicCamera());//todo 1000f
	if(MULTIPLE_VIEWPORTS) viewport2 = new ExtendViewport(500, 500, new OrthographicCamera());
	else viewport2 = viewport1;
	stage = new Stage(viewport2/*, batch*/);
	stage.addActor(new GradientShapeRect(200, 50));
	stage.addActor(new Image(Resources.Textures.tank));
	Json json = new Json();
	model = new Model(json, json.fromJson(Conf.class, Gdx.files.internal("conf.json")));
	batchShader = new ShaderProgram(defaultVertex, Gdx.files.internal("shader/good_blur.frag"));
	if(!batchShader.isCompiled()) App.log.error(batchShader.getLog());
	if(false) batch.setShader(batchShader);
	shapeRenderer = new ShapeRenderer2(10000, null);
	shapeRenderer.setAutoShapeType(false);
	Gdx.input.setInputProcessor(new InputMultiplexer(stage, new InputAdapter() {
		public boolean touchDown(int screenX, int screenY, int pointer, int button) {
			model.touch(new GdxXY(viewport1.unproject(new Vector2(screenX, screenY))));
			return true;
		}
	}));
}
public void resize(int width, int height) {
	if(MULTIPLE_VIEWPORTS) {
		viewport1.update(width / 2, height, true);
		viewport1.setScreenX(width / 2);
		viewport2.update(width / 2, height, true);
	} else viewport1.update(width, height, true);
	batch.setProjectionMatrix(viewport2.getCamera().combined);
	shapeRenderer.setProjectionMatrix(viewport1.getCamera().combined);
}
Logic.XY backgroundOffset = new GdxXY(new Logic.XY());
public void render() {
	final boolean TEST_TEXTURE = LibAllGwt.FALSE();
	model.update(Gdx.graphics.getDeltaTime());
	Logic.State state = model.getDisplayState();
	if(state != null) {
		for(Logic.Car car : state.cars) {
			if(car.owner.equals(model.playerId)) {
				Logic.XY previous = new GdxXY(viewport1.getCamera().position);
				viewport1.getCamera().position.x = car.pos.x;
				viewport1.getCamera().position.y = car.pos.y;
				viewport1.getCamera().update();
				shapeRenderer.setProjectionMatrix(viewport1.getCamera().combined);
				Logic.XY change = new GdxXY(viewport1.getCamera().position).sub(previous);
				if(change.x > state.width()/2) change.x -= state.width();
				else if(change.x < -state.width()/2) change.x += state.width();
				if(change.y > state.height()/2) change.y -= state.height();
				else if(change.y < -state.height()/2) change.y += state.height();
				backgroundOffset = backgroundOffset.add(new GdxXY(change), 0.0001f);
				break;
			}
		}
	}
	Gdx.gl.glClearColor(0.f, 0.f, 0.f, 1);
	Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	if(BACKGROUND_MESH) {
		meshShader.begin();
		applyUniform(meshShader);
		mesh.render(meshShader, GL20.GL_TRIANGLES);
		checkForGlError();
		meshShader.end();
		if(false) Gdx.gl.glFlush();//save fps
		if(false) Gdx.gl.glFinish();//save fps
	}
	if(TEST_TEXTURE) {
		if(LibAllGwt.FALSE())stage.getViewport().apply();
		stage.act(/*Gdx.graphics.getDeltaTime()*/);
		stage.draw();
	}
	if(BACKGROUND_BATCH) {
		viewport2.apply();
//		backgroundBatchShader.setUniformf(), viewport2.getWorldWidth(), viewport2.getWorldHeight());
		backgroundBatch.begin();
		applyUniform(backgroundBatchShader);
		backgroundBatch.draw(Resources.Textures.tank, 0, 0, viewport2.getWorldWidth(), viewport2.getWorldHeight());//todo change to mesh https://github.com/mc-imperial/libgdx-get-image
		backgroundBatch.end();
	}
	viewport1.apply();
	if(state != null) {
		if(DRAW_GRID) {
			shapeRenderer.begin(ShapeRenderer2.ShapeType.Line);
			shapeRenderer.setColor(Color.WHITE);
			float gridSize = 100;
			for(int x = 0; x*gridSize <= state.width(); x++) shapeRenderer.line(x*gridSize, 0, 0, x*gridSize, state.height(), 0);
			for(int y = 0; y*gridSize < state.height(); y++) shapeRenderer.line(0, y*gridSize, 0, state.width(), y*gridSize, 0);
			shapeRenderer.end();
		}
		shapeRenderer.begin(ShapeRenderer2.ShapeType.Filled);
		shapeRenderer.setColor(Color.GRAY);
		for(Logic.Food food : state.foods) {
			Logic.XY r = calcRenderXY(state, food.pos);
			shapeRenderer.circle(r.x, r.y, food.radius());
		}
		for(Logic.Reactive react : state.reactive) {
			Logic.XY r = calcRenderXY(state, react.pos);
			Color color = colors[react.owner.id % (colors.length - 1)];
			shapeRenderer.setColor(color);
			shapeRenderer.circle(r.x, r.y, react.radius());
		}
		for(Logic.Car car : state.cars) {
			Logic.XY r = calcRenderXY(state, car.pos);
			Color color = colors[car.owner.id % (colors.length - 1)];
			shapeRenderer.setColor(color);
			shapeRenderer.circle(r.x, r.y, car.radius()/*, 20*/);
		}
		shapeRenderer.end();
	}
	if(MULTIPLE_VIEWPORTS) viewport2.apply();
	batch.begin();
	float width = Gdx.graphics.getWidth();
	float height = Gdx.graphics.getHeight();
	if(batchShader != null) {
		batchShader.setUniformf("u_viewportInverse", new Vector2(1f / width, 1f / height));
		batchShader.setUniformf("u_offset",2f);
		batchShader.setUniformf("u_step", Math.min(1f, width / 70f));
		batchShader.setUniformf("u_color", new Vector3(0, 1, 1));
	}
	Resources.Font.loadedFont().draw(batch, "fps: " + Gdx.graphics.getFramesPerSecond(), 0, 150);
	Resources.Font.loadedFont().draw(batch, model.getPlayerName(), 0, 200);
	Resources.Font.loadedFont().draw(batch, "latency: " + (int) (model.client.latencyS * LibAllGwt.MILLIS_IN_SECCOND), 0, 250);
	Resources.Font.loadedFont().draw(batch, "copyTime: " + model.copyTime, 0, 300);
	Resources.Font.loadedFont().draw(batch, "tickTime: " + model.tickTime, 0, 350);
	if(false)Resources.Font.loadedFont().draw(batch, "smart latency: " + (int) (model.client.smartLatencyS * LibAllGwt.MILLIS_IN_SECCOND), 0, 300);
	if(TEST_TEXTURE) {
		batch.draw(Resources.Textures.tank, viewport2.getWorldWidth()/2, viewport2.getWorldHeight()/2);
		batch.draw(Resources.Textures.red, viewport2.getWorldWidth()/3, viewport2.getWorldHeight()/2);
		batch.draw(Resources.Textures.green, viewport2.getWorldWidth()/2, viewport2.getWorldHeight()/3);
		batch.draw(Resources.Textures.blue, viewport2.getWorldWidth()/3, viewport2.getWorldHeight()/3);
		batch.draw(Resources.Textures.yellow, viewport2.getWorldWidth()*2/3, viewport2.getWorldHeight()/2);
	}
	batch.end();
}
private void applyUniform(ShaderProgram program) {
	int width = Gdx.graphics.getWidth();
	int height = Gdx.graphics.getHeight();
	if(height > width) {//todo check landscape
		int temp = width;
		width = height;
		height = temp;
	}
	program.setUniformf(program.fetchUniformLocation("resolution", false), width, height);
	program.setUniformf("time", App.sinceStartS());//30f
	program.setUniformf("mouse", backgroundOffset.x, backgroundOffset.y);
}
private Logic.XY calcRenderXY(Logic.State state, Logic.XY pos) {
	float x = pos.x;
	float dx = viewport1.getCamera().position.x - x;
	if(dx > state.width()/2) x += state.width();
	else if(dx < -state.width()/2) x -= state.width();
	float y = pos.y;
	float dy = viewport1.getCamera().position.y - y;
	if(dy > state.height()/2) y += state.height();
	else if(dy < -state.height()/2) y -= state.height();
	return new Logic.XY(x, y);
}
private void checkForGlError() {
	int error = Gdx.gl.glGetError();
	if(error != GL20.GL_NO_ERROR) {
		App.log.error("GL Error: " + error);
	}
}
public void dispose() {
	model.dispose();
	batch.dispose();
	shapeRenderer.dispose();
	//todo dispose shaders
	Resources.dispose();
}
}