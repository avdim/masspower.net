package com.riseofcat

import com.badlogic.gdx.*
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.*
import com.badlogic.gdx.graphics.glutils.*
import com.badlogic.gdx.math.*
import com.badlogic.gdx.scenes.scene2d.*
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.viewport.*
import com.riseofcat.client.*
import com.riseofcat.lib.*
import com.riseofcat.redundant.*
import com.riseofcat.share.mass.*

const val MULTIPLE_VIEWPORTS = false
const val BACKGROUND_BATCH = false
const val BACKGROUND_MESH = true
const val DRAW_GRID = false
val colors = arrayOf(Color.RED,Color.GREEN,Color.BLUE,Color.YELLOW,Color.VIOLET)

class BatchWithShader(val b:SpriteBatch, val s:ShaderProgram) {
  init { b.shader = s }
}

class Core:ApplicationAdapter() {
  private lateinit var model:ClientModel
  private var backgroundOffset = XY()
  private var background:BatchWithShader? = null
  private lateinit var shapeRenderer:ShapeRenderer2
  private lateinit var viewport1:Viewport
  private lateinit var viewport2:Viewport
  private var stage:Stage? = null
  private lateinit var batch:SpriteBatch
  private lateinit var batchShader:ShaderProgram
  private var mesh:Mesh? = null
  private lateinit var meshShader:ShaderProgram
  override fun create() {
    val defaultVertex = shader_default_vertex_shader_vert
    ShaderProgram.pedantic = false
    batch = SpriteBatch()
    if(BACKGROUND_BATCH) {
      background = BatchWithShader(
        SpriteBatch(),
        ShaderProgram(defaultVertex,shader_background_stars_frag)
          .apply {if(!isCompiled) lib.log.error(log)}
      )
    }
    if(BACKGROUND_MESH) {
      mesh = Mesh(true,4,6,VertexAttribute(VertexAttributes.Usage.Position,2,"aVertexPosition")).apply {
        setVertices(floatArrayOf(-1.0f,1.0f,-1.0f,-1.0f,1.0f,-1.0f,1.0f,1.0f))
        setIndices(shortArrayOf(0,1,2,2,3,0))
      }
      meshShader = ShaderProgram(shader_mesh_default_vert,
        shader_background_stars_frag)
        .apply {
          if(!isCompiled) lib.log.error(log)
        }
    }
    viewport1 = ExtendViewport(1000f,1000f,OrthographicCamera())//todo 1000f
    viewport2 = if(MULTIPLE_VIEWPORTS) ExtendViewport(500f,500f,OrthographicCamera()) else viewport1
    stage = Stage(viewport2/*, batch*/).apply {
      addActor(GradientShapeRect(200,50))
      addActor(Image(Resources.Textures.tank))
    }
    val str = Gdx.files.internal("conf.json").readString()
    val conf:Conf = lib.json.parse(str)
    model = ClientModel(conf)
    batchShader = ShaderProgram(defaultVertex,shader_good_blur_frag)
    if(!batchShader.isCompiled) lib.log.error(batchShader.log)
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
    val state = model.calcDisplayState()
    if(state!=null) {
      for((owner,_,_,pos) in state.cars) {
        if(owner==model.welcome?.id) {
          val previous = viewport1.camera.position.xy
          viewport1.camera.position.x = pos.xf
          viewport1.camera.position.y = pos.yf
          viewport1.camera.update()
          shapeRenderer.projectionMatrix = viewport1.camera.combined
          val change = viewport1.camera.position.xy - previous
          if(change.x>state.width/2)
            change.x = change.x-state.width
          else if(change.x<-state.width/2) change.x = change.x+state.width
          if(change.y>state.height/2)
            change.y = change.y-state.height
          else if(change.y<-state.height/2) change.y = change.y+state.height
          backgroundOffset += change*0.0001
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
      checkForGlError()//todo не рендериться задник
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
        while(x*gridSize<=state.width) {
          shapeRenderer.line(x*gridSize,0f,0f,x*gridSize,state.height.toFloat(),0f)
          x++
        }
        var y = 0
        while(y*gridSize<state.height) {
          shapeRenderer.line(0f,y*gridSize,0f,state.width.toFloat(),y*gridSize,0f)
          y++
        }
        shapeRenderer.end()
      }
      shapeRenderer.begin(ShapeRenderer2.ShapeType.Filled)
      shapeRenderer.color = Color.GRAY
      for(food in state.foods) {
        calcRenderXY(state,food.pos).apply {
          shapeRenderer.circle(xf,yf,food.radius)
        }
      }
      for(react in state.reactive) {
        calcRenderXY(state,react.pos).apply {
          val color = colors[react.owner.id%(colors.size)]
          shapeRenderer.color = color
          shapeRenderer.circle(xf,yf,react.radius)
        }
      }
      for(car in state.cars) {
        calcRenderXY(state,car.pos).apply {
          val color = colors[car.owner.id%(colors.size)]
          shapeRenderer.color = color
          shapeRenderer.circle(xf,yf,car.radius/*, 20*/)
        }
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
//    Resources.Font.loadedFont().draw(batch,"tick average: %.2f".format(averageTickNanos/1000),0f,100f)
    Resources.Font.loadedFont().draw(batch,"fps: "+Gdx.graphics.framesPerSecond,0f,150f)
    Resources.Font.loadedFont().draw(batch,model.playerName,0f,200f)
    Resources.Font.loadedFont().draw(batch,"lastPingDelay: "+model.client.lastPingDelay?.ms,0f,250f)
    Resources.Font.loadedFont().draw(batch,"tick: "+model.calcDisplayState()?.tick,0f,300f)
    Resources.Font.loadedFont().draw(batch,"smart pingDelay: "+model.client.smartPingDelay.ms,0f,350f)
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
    var width = Gdx.graphics.width.toFloat()
    var height = Gdx.graphics.height.toFloat()
    if(height>width) {//todo check landscape
      val temp = width
      width = height
      height = temp
    }
    program.setUniformf(program.fetchUniformLocation("resolution",false),width,height)
    program.setUniformf("time",lib.pillarTimeS(10_000f).toFloat())//30f
    program.setUniformf("mouse",backgroundOffset.xf,backgroundOffset.yf)
  }

  private fun calcRenderXY(state:State,pos:XY):XY = calcRenderXY(state, pos, viewport1.camera.position.xy)
  private fun checkForGlError() = Gdx.gl.glGetError().let {if(it!=GL20.GL_NO_ERROR) lib.log.error("GL Error: $it")}

  override fun dispose() {
    model.dispose()
    batch.dispose()
    shapeRenderer.dispose()
    //todo dispose shaders
    Resources.dispose()
  }
}

val XY.xf get() = x.toFloat()
val XY.yf get() = y.toFloat()
val Vector2.xy get() = XY(x,y)
val Vector3.xy get() = XY(x,y)
val Angle.gdxTransformRotation get() = degrees