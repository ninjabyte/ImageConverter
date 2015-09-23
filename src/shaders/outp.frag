#version 130

uniform sampler2D uData;
uniform sampler2D uPal;
uniform vec2 uResolution;

void main()
{
	vec2 uv = gl_FragCoord.xy/uResolution;
	vec2 xy = texture2D(uData, vec2(uv.x, 1-uv.y)).zw;
	gl_FragColor = texture2D(uPal, xy);
}