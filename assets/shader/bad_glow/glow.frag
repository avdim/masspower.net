#ifdef GL_ES
precision mediump float;
#endif
varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
const float outlineDistance = 0.2;
const vec4 outlineColor = vec4(0,0,0,1);
const float smoothing = 1.0/16.0;
void main()
{
  float distance = texture2D(u_texture, v_texCoords).a;
  float outlineFactor = smoothstep(0.5 - smoothing, 0.5 + smoothing, distance);
  vec4 color = mix(outlineColor, v_color, outlineFactor);
  float alpha = smoothstep(outlineDistance - smoothing, outlineDistance + smoothing, distance);
  gl_FragColor = vec4(color.rgb, color.a * alpha);
}