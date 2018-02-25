//Good with fonts: http://www.allpiper.com/2d-selection-outline-shader-in-libgdx/
#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

uniform sampler2D u_texture;

// The inverse of the viewport dimensions along X and Y
uniform vec2 u_viewportInverse;

// Color of the outline
uniform vec3 u_color;

// Thickness of the outline
uniform float u_offset;

// Step to check for neighbors
uniform float u_step;

varying vec4 v_color;
varying vec2 v_texCoord;

#define ALPHA_VALUE_BORDER 0.2

void main() {
   vec2 T = v_texCoord.xy;

   float alpha = 0.0;
   bool allin = true;
   for( float ix = -u_offset; ix < u_offset; ix += u_step )
   {
      for( float iy = -u_offset; iy < u_offset; iy += u_step )
       {
          float newAlpha = texture2D(u_texture, T + vec2(ix, iy) * u_viewportInverse).a;
          allin = allin && newAlpha > ALPHA_VALUE_BORDER;
          if (newAlpha > ALPHA_VALUE_BORDER && newAlpha >= alpha)
          {
             alpha = newAlpha;
          }
      }
   }
   if (allin)
   {
      alpha = 0.0;
   }
   vec4 previous = v_color * texture2D(u_texture, v_texCoord);
   //vec3 temp = vec3(0,0,0);
   //temp.r = 1. - (1.-previous.r)*(1. - u_color.r);
   //temp.g = 1. - (1.-previous.g)*(1. - u_color.g);
   //temp.b = 1. - (1.-previous.b)*(1. - u_color.b);
   //gl_FragColor = vec4(temp,1.);
   gl_FragColor = vec4(u_color, alpha);
}
