package com.riseofcat

import com.badlogic.gdx.*
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.*
import com.badlogic.gdx.graphics.glutils.*
import com.badlogic.gdx.math.*
import com.badlogic.gdx.scenes.scene2d.*
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.viewport.*
import com.riseofcat.client.Conf
import com.riseofcat.client.Model
import com.riseofcat.common.*
import com.riseofcat.lib.*
import com.riseofcat.redundant.*
import com.riseofcat.share.data.*

const val MULTIPLE_VIEWPORTS = false
const val BACKGROUND_BATCH = false
const val BACKGROUND_MESH = true
const val DRAW_GRID = true
val colors = arrayOf(Color.BLUE,Color.GOLD,Color.PINK,Color.RED,Color.GREEN,Color.VIOLET,Color.LIME,Color.TEAL,Color.YELLOW)
val Vector2.xy get() = XY(x,y)
val Vector3.xy get() = XY(x,y)

class BatchWithShader(val b:SpriteBatch, val s:ShaderProgram) {
  init { b.shader = s }
}

class Core:ApplicationAdapter() {
  private lateinit var model:Model
  private var backgroundOffset = XY(0f,0f)
  private var background:BatchWithShader? = null;
  private lateinit var shapeRenderer:ShapeRenderer2
  private lateinit var viewport1:Viewport
  private lateinit var viewport2:Viewport
  private var stage:Stage? = null
  private lateinit var batch:SpriteBatch
  private lateinit var batchShader:ShaderProgram
  private var mesh:Mesh? = null
  private lateinit var meshShader:ShaderProgram
  override fun create() {
    val defaultVertex = Gdx.files.internal("shader/default_vertex_shader.vert")
    ShaderProgram.pedantic = false
    batch = SpriteBatch()
    if(BACKGROUND_BATCH) {
      background = BatchWithShader(
        SpriteBatch(),
        ShaderProgram(defaultVertex,Gdx.files.internal("shader/background/stars.frag"))
          .apply {if(!isCompiled) Lib.Log.error(log)}
      )
    }
    if(BACKGROUND_MESH) {
      mesh = Mesh(true,4,6,VertexAttribute(VertexAttributes.Usage.Position,2,"aVertexPosition")).apply {
        setVertices(floatArrayOf(-1.0f,1.0f,-1.0f,-1.0f,1.0f,-1.0f,1.0f,1.0f))
        setIndices(shortArrayOf(0,1,2,2,3,0))
      }
      meshShader = ShaderProgram(Gdx.files.internal("shader/mesh/default.vert"),
        Gdx.files.internal("shader/background/stars.frag"))
        .apply {
          if(!isCompiled) Lib.Log.error(meshShader.log)
        }
    }
    viewport1 = ExtendViewport(1000f,1000f,OrthographicCamera())//todo 1000f
    viewport2 = if(MULTIPLE_VIEWPORTS) ExtendViewport(500f,500f,OrthographicCamera()) else viewport1
    stage = Stage(viewport2/*, batch*/).apply {
      addActor(GradientShapeRect(200,50))
      addActor(Image(Resources.Textures.tank))
    }
    val str = Gdx.files.internal("conf.json").readString()
    val conf = Common.fromJson(str,Conf::class)
    model = Model(conf)
    batchShader = ShaderProgram(defaultVertex,Gdx.files.internal("shader/good_blur.frag"))
    if(!batchShader.isCompiled) Lib.Log.error(batchShader.log)
    if(false) batch.shader = batchShader
    shapeRenderer = ShapeRenderer2(10000,null)
    shapeRenderer.setAutoShapeType(false)
    Gdx.input.inputProcessor = InputMultiplexer(stage,object:InputAdapter() {
      override fun touchDown(screenX:Int,screenY:Int,pointer:Int,button:Int):Boolean {
        model.touch(viewport1.unproject(Vector2(screenX.toFloat(),screenY.toFloat())).xy)
        return true
      }
    })
  }

  override fun resize(width:Int,height:Int) {
    //todo new OrthographicCamera().setToOrtho(false, 100, 100);
    if(MULTIPLE_VIEWPORTS) {
      viewport1.update(width/2,height,true)
      viewport1.screenX = width/2
      viewport2.update(width/2,height,true)
    } else viewport1.update(width,height,true)

    batch.projectionMatrix = viewport2.camera.combined
    shapeRenderer.projectionMatrix = viewport1.camera.combined
  }

  override fun render() {
    val TEST_TEXTURE = false
    model.update(Gdx.graphics.deltaTime)
    val state = model.displayState
    if(state!=null) {
      for((owner,_,_,pos) in state.cars) {
        if(owner==model.playerId) {
          val previous = viewport1.camera.position.xy
          viewport1.camera.position.x = pos.x
          viewport1.camera.position.y = pos.y
          viewport1.camera.update()
          shapeRenderer.projectionMatrix = viewport1.camera.combined
          val change = viewport1.camera.position.xy.sub(previous)
          if(change.x>state.width()/2)
            change.x = change.x-state.width()
          else if(change.x<-state.width()/2) change.x = change.x+state.width()
          if(change.y>state.height()/2)
            change.y = change.y-state.height()
          else if(change.y<-state.height()/2) change.y = change.y+state.height()
          backgroundOffset = backgroundOffset.add(change,0.0001f)
          break
        }
      }
    }
    Gdx.gl.glClearColor(0f,0f,0f,1f)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
    mesh?.apply {
      meshShader.begin()
      applyUniform(meshShader)
      render(meshShader,GL20.GL_TRIANGLES)
      checkForGlError()
      meshShader.end()
      if(false) Gdx.gl.glFlush()//save fps
      if(false) Gdx.gl.glFinish()//save fps
    }
    if(TEST_TEXTURE) {
      stage?.apply {
        if(false) viewport.apply()
        act(/*Gdx.graphics.getDeltaTime()*/)
        draw()
      }
    }
    if(BACKGROUND_BATCH) {
      viewport2.apply()
      //		backgroundBatchShader.setUniformf(), viewport2.getWorldWidth(), viewport2.getWorldHeight());
      background?.apply {
        b.begin()
        applyUniform(s)
        b.draw(Resources.Textures.tank,0f,0f,viewport2.worldWidth,viewport2.worldHeight)//todo change to mesh https://github.com/mc-imperial/libgdx-get-image
        b.end()
      }
    }
    viewport1.apply()
    if(state!=null) {
      if(DRAW_GRID) {
        shapeRenderer.begin(ShapeRenderer2.ShapeType.Line)
        shapeRenderer.color = Color.WHITE
        val gridSize = 100f
        var x = 0
        while(x*gridSize<=state.width()) {
          shapeRenderer.line(x*gridSize,0f,0f,x*gridSize,state.height(),0f)
          x++
        }
        var y = 0
        while(y*gridSize<state.height()) {
          shapeRenderer.line(0f,y*gridSize,0f,state.width(),y*gridSize,0f)
          y++
        }
        shapeRenderer.end()
      }
      shapeRenderer.begin(ShapeRenderer2.ShapeType.Filled)
      shapeRenderer.color = Color.GRAY
      for(food in state.foods) {
        val (x,y) = calcRenderXY(state,food.pos)
        shapeRenderer.circle(x,y,food.radius())
      }
      for(react in state.reactive) {
        val (x,y) = calcRenderXY(state,react.pos)
        val color = colors[react.owner.id%(colors.size-1)]
        shapeRenderer.color = color
        shapeRenderer.circle(x,y,react.radius())
      }
      for(car in state.cars) {
        val (x,y) = calcRenderXY(state,car.pos)
        val color = colors[car.owner.id%(colors.size-1)]
        shapeRenderer.color = color
        shapeRenderer.circle(x,y,car.radius()/*, 20*/)
      }
      shapeRenderer.end()
    }
    if(MULTIPLE_VIEWPORTS) viewport2.apply()
    batch.begin()
    val width = Gdx.graphics.width.toFloat()
    val height = Gdx.graphics.height.toFloat()
    batchShader.apply {
      setUniformf("u_viewportInverse",Vector2(1f/width,1f/height))
      setUniformf("u_offset",2f)
      setUniformf("u_step",Math.min(1f,width/70f))
      setUniformf("u_color",Vector3(0f,1f,1f))
    }
    Resources.Font.loadedFont().draw(batch,"fps: "+Gdx.graphics.framesPerSecond,0f,150f)
    Resources.Font.loadedFont().draw(batch,model.playerName,0f,200f)
    Resources.Font.loadedFont().draw(batch,"latency: "+(model.client.latencyS*Lib.Const.MILLIS_IN_SECOND).toInt(),0f,250f)
    Resources.Font.loadedFont().draw(batch,"copyTime: "+model.copyTime,0f,300f)
    Resources.Font.loadedFont().draw(batch,"tickTime: "+model.tickTime,0f,350f)
    if(false) Resources.Font.loadedFont().draw(batch,"smart latency: "+(model.client.smartLatencyS*Lib.Const.MILLIS_IN_SECOND).toInt(),0f,300f)
    if(TEST_TEXTURE) {
      batch.draw(Resources.Textures.tank,viewport2.worldWidth/2,viewport2.worldHeight/2)
      batch.draw(Resources.Textures.red,viewport2.worldWidth/3,viewport2.worldHeight/2)
      batch.draw(Resources.Textures.green,viewport2.worldWidth/2,viewport2.worldHeight/3)
      batch.draw(Resources.Textures.blue,viewport2.worldWidth/3,viewport2.worldHeight/3)
      batch.draw(Resources.Textures.yellow,viewport2.worldWidth*2/3,viewport2.worldHeight/2)
    }
    batch.end()
  }

  private fun applyUniform(program:ShaderProgram) {
    var width = Gdx.graphics.width
    var height = Gdx.graphics.height
    if(height>width) {//todo check landscape
      val temp = width
      width = height
      height = temp
    }
    program.setUniformf(program.fetchUniformLocation("resolution",false),width.toFloat(),height.toFloat())
    program.setUniformf("time",Lib.sinceStartS)//30f
    program.setUniformf("mouse",backgroundOffset.x,backgroundOffset.y)
  }

  private fun calcRenderXY(state:State,pos:XY):XY {
    var x = pos.x
    val dx = viewport1.camera.position.x-x
    if(dx>state.width()/2)
      x += state.width()
    else if(dx<-state.width()/2) x -= state.width()
    var y = pos.y
    val dy = viewport1.camera.position.y-y
    if(dy>state.height()/2)
      y += state.height()
    else if(dy<-state.height()/2) y -= state.height()
    return XY(x,y)
  }

  private fun checkForGlError() = Gdx.gl.glGetError().let {if(it!=GL20.GL_NO_ERROR) Lib.Log.error("GL Error: $it")}

  override fun dispose() {
    model.dispose()
    batch.dispose()
    shapeRenderer.dispose()
    //todo dispose shaders
    Resources.dispose()
  }
}