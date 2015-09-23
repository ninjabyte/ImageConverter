#version 130

uniform sampler2D uData;
uniform sampler2D uImg;
uniform vec2 uResolution;
uniform vec3 uColor;
uniform vec2 uColorUV;
uniform float uColorID;

vec3 get(vec2 uv)
{
	return texture2D(uImg, uv).rgb;
}

void main()
{
	vec2 uv = gl_FragCoord.xy/uResolution;
	
	vec4 data = texture2D(uData, uv);
	float dst = distance(get(uv), uColor);
	if (dst < data.x)
	{
		data.x = dst;
		data.y = uColorID;
		data.zw = uColorUV;
	}
	
	gl_FragColor = data;
}