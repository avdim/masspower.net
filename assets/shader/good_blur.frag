#ifdef GL_ES
precision mediump float;
precision mediump int;
#else
#define highp;
#endif


varying vec4 v_color;
varying vec2 v_texCoord;

uniform sampler2D u_texture;
/*uniform*/ float resolution = 800.0;//width
/*uniform*/ float radius = 5.0;
/*uniform*/ vec2 dir = vec2(1.0, 0.0);//Этот шейдер расчитан на то чтобы сначала рендерить в один frame buffer с блюром по горизонтально например (dir = vec2(1.0,0.0)), а потом этот frame buffer рендерить вертикально dir=vec2(0.0,1.0)

void main() {
	vec4 sum = vec4(0.0);
	vec2 tc = v_texCoord;

	// Number of pixels off the central pixel to sample from
	float blur = radius/resolution;

	// Blur direction
	float hstep = dir.x;
	float vstep = dir.y;

	// Apply blur using 9 samples and predefined gaussian weights
	//Сумма коэффициентов наверное равна 1
	sum += texture2D(u_texture, vec2(tc.x - 4.0*blur*hstep, tc.y - 4.0*blur*vstep)) * 0.006;
	sum += texture2D(u_texture, vec2(tc.x - 3.0*blur*hstep, tc.y - 3.0*blur*vstep)) * 0.044;
	sum += texture2D(u_texture, vec2(tc.x - 2.0*blur*hstep, tc.y - 2.0*blur*vstep)) * 0.121;
	sum += texture2D(u_texture, vec2(tc.x - 1.0*blur*hstep, tc.y - 1.0*blur*vstep)) * 0.194;

	sum += texture2D(u_texture, vec2(tc.x, tc.y)) * 0.27;

	sum += texture2D(u_texture, vec2(tc.x + 1.0*blur*hstep, tc.y + 1.0*blur*vstep)) * 0.194;
	sum += texture2D(u_texture, vec2(tc.x + 2.0*blur*hstep, tc.y + 2.0*blur*vstep)) * 0.121;
	sum += texture2D(u_texture, vec2(tc.x + 3.0*blur*hstep, tc.y + 3.0*blur*vstep)) * 0.044;
	sum += texture2D(u_texture, vec2(tc.x + 4.0*blur*hstep, tc.y + 4.0*blur*vstep)) * 0.006;

	gl_FragColor = sum;
}