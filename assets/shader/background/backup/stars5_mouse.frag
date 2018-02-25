#ifdef GL_ES
    precision highp float;
    precision highp int;
#endif
uniform float time;
uniform vec2 resolution;
uniform vec2 mouse;
//Качество:
#define ITERATIONS 6
#define VOLSTEPS 3
//От 0.2 до 1.0
#define FORMUPARAM 0.85
//Важный параметр
#define STEPSIZE 0.65
#define ZOOM   5.900
#define TILE   0.850
#define SPEED  0.150
#define BRIGHTNESS 0.0045
#define DISTFADING 1.130
#define SATURATION 0.750
//Может быть как положительный так и отрицательный целые значения увеличивают выпад пятен
#define INTERESTING1 2.2
void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
	//get coords and direction
	vec2 uv= fragCoord.xy/resolution.xy-.5;
	uv.y*=resolution.y/resolution.x;
	vec3 dir=vec3(uv*ZOOM,1.1);
	float time=time*SPEED+.25;
	float a1=.5+time/resolution.x*2.;
	float a2=.8+time/resolution.y*2.;
	mat2 rot1=mat2(cos(a1),sin(a1),-sin(a1),cos(a1));
	mat2 rot2=mat2(cos(a2),sin(a2),-sin(a2),cos(a2));
	dir.xz*=rot1;
	dir.xy*=rot2;
	vec3 from=vec3(0.9,.5,0.5);
	from+=vec3(mouse.x*5., mouse.y*5.,INTERESTING1);//Направление движения
	from.xz*=rot1;
	from.xy*=rot2;

	//volumetric rendering
	float s=0.1,fade=1.1;
	vec3 v=vec3(0.);
	for (int r=0; r<VOLSTEPS; r++) {
		vec3 p=from+s*dir*.5;
		p = abs(vec3(TILE)-mod(p,vec3(TILE*2.))); // tiling fold
		float a=0.;
		float pa = 6.0;
		float param = FORMUPARAM * (1.0 + sin(time*3.0)*0.02);
		for (int i=0; i<ITERATIONS; i++) {
			p=abs(p)/dot(p,p)-param; // the magic formula
			a+=abs(length(p)-pa); // absolute sum of average change
			pa*=0.65;//length(p);
		}
		a*=a*a; // add contrast
		v+=fade;
		v+=vec3(sin(s/0.01999)*0.5*exp(s),s,0.2*exp(s))*a*BRIGHTNESS*fade; // COLOR based on distance
		fade*=DISTFADING; // distance fading
		s+=STEPSIZE;
	}
	v=mix(vec3(length(v)),v,SATURATION); //color adjust
	fragColor = vec4(v*.009,1.0);
}
void main (void)
{
  vec4 color = vec4 (0.0, 0.0, 0.0, 1.0);
  mainImage (color, gl_FragCoord.xy);
  color.w = 1.0;
  gl_FragColor = color;
}
