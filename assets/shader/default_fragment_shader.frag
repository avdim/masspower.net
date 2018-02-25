//#ifdef позволяет коду работать на слабых телефонах, и мощных пк.Если шейдер используется на телефоне(GL_ES) то
//используется низкая разрядность (точность) данных.(highp  высокая точность; mediump  средняя точность; lowp  низкая точность)
#ifdef GL_ES
    #define LOWP lowp
    precision mediump float;
#else
    #define LOWP
#endif
varying LOWP vec4 v_color;
varying vec2 v_texCoords;
// sampler2D это специальный формат данных в  glsl для доступа к текстуре
uniform sampler2D u_texture;
void main(){
//как и в стандартном шейдере получаем итоговый цвет пикселя
gl_FragColor = v_color * texture2D(u_texture, v_texCoords);
//после получения итогового цвета, меняем его на противоположный
gl_FragColor.rgb=1.0-gl_FragColor.rgb;
//    gl_FragColor = vec4(1,0,0,1);//v_color * texture2D(u_texture, v_texCoords);// итоговый цвет пикселя
}
