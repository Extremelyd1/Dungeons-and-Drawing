#version 330

const int MAX_POINT_LIGHTS = 5;
const int MAX_SPOT_LIGHTS = 5;

in vec2 outTexCoord;
in vec4 color;
in vec3 mvVertexNormal;
in vec3 mvVertexPos;
in vec4 mlightviewVertexPos;

out vec4 fragColor;

struct Attenuation
{
    float constant;
    float linear;
    float exponent;
};

struct PointLight
{
    vec3 colour;
    // Light position is assumed to be in view coordinates
    vec3 position;
    float intensity;
    Attenuation att;
};

struct SpotLight
{
    PointLight pl;
    vec3 conedir;
    float cutoff;
};

struct DirectionalLight
{
    vec3 colour;
    vec3 direction;
    float intensity;
};

struct Material
{
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    int hasTexture;
    int isColored;
    float reflectance;
};

uniform sampler2D texture_sampler;
uniform vec3 ambientLight;
uniform float specularPower;
uniform Material material;
uniform PointLight pointLights[MAX_POINT_LIGHTS];
uniform SpotLight spotLights[MAX_SPOT_LIGHTS];
uniform DirectionalLight directionalLight;
uniform sampler2D shadowMap;

vec4 ambientC;
vec4 diffuseC;
vec4 speculrC;

void setupColours(Material material, vec2 textCoord)
{
    if (material.hasTexture == 1)
    {
        ambientC = texture(texture_sampler, textCoord);
        diffuseC = ambientC;
        speculrC = ambientC;
    }
    else if (material.isColored == 0)
    {
        ambientC = color;
        diffuseC = color;
        speculrC = color;
    }
    else
    {
        ambientC = material.ambient;
        diffuseC = material.diffuse;
        speculrC = material.specular;
    }
}

vec4 calcLightColour(vec3 light_colour, float light_intensity, vec3 position, vec3 to_light_dir, vec3 normal)
{
    vec4 diffuseColour = vec4(0, 0, 0, 0);
    vec4 specColour = vec4(0, 0, 0, 0);

    // Diffuse Light
    float diffuseFactor = max(dot(normal, to_light_dir), 0.0);
    diffuseColour = diffuseC * vec4(light_colour, 1.0) * light_intensity * diffuseFactor;

    // Specular Light
    vec3 camera_direction = normalize(-position);
    vec3 from_light_dir = -to_light_dir;
    vec3 reflected_light = normalize(reflect(from_light_dir , normal));
    float specularFactor = max( dot(camera_direction, reflected_light), 0.0);
    specularFactor = pow(specularFactor, specularPower);
    specColour = speculrC * light_intensity  * specularFactor * material.reflectance * vec4(light_colour, 1.0);

    return (diffuseColour + specColour);
}

vec4 calcPointLight(PointLight light, vec3 position, vec3 normal)
{
    vec3 light_direction = light.position - position;
    vec3 to_light_dir  = normalize(light_direction);
    vec4 light_colour = calcLightColour(light.colour, light.intensity, position, to_light_dir, normal);

    // Apply Attenuation
    float distance = length(light_direction);
    float attenuationInv = light.att.constant + light.att.linear * distance +
        light.att.exponent * distance * distance;
    return light_colour / attenuationInv;
}


vec4 calcSpotLight(SpotLight light, vec3 position, vec3 normal)
{
    vec3 light_direction = light.pl.position - position;
    vec3 to_light_dir  = normalize(light_direction);
    vec3 from_light_dir  = -to_light_dir;
    float spot_alfa = dot(from_light_dir, normalize(light.conedir));
    
    vec4 colour = vec4(0, 0, 0, 0);
    
    if ( spot_alfa > light.cutoff ) 
    {
        colour = calcPointLight(light.pl, position, normal);
        colour *= (1.0 - (1.0 - spot_alfa)/(1.0 - light.cutoff));
    }
    return colour;    
}

vec4 calcDirectionalLight(DirectionalLight light, vec3 position, vec3 normal)
{
    return calcLightColour(light.colour, light.intensity, position, normalize(light.direction), normal);
}

float calcShadow(vec4 position)
{
    float bias = max(0.05 * (1.0 - dot(mvVertexNormal, directionalLight.direction)), 0.01);

    vec3 projCoords = position.xyz / position.w;
    // Transform from screen coordinates to texture coordinates
    projCoords = projCoords * 0.5 + 0.5f;


    if (projCoords.z > 1.0){
        return 0.0f;
    } else {
        float closestDepth = texture(shadowMap, projCoords.xy).r;
        float currentDepth = projCoords.z;

        float shadow = 0.0;
        vec2 texelSize = 1.0 / textureSize(shadowMap, 0);
        for(int x = -2; x <= 2; ++x)
        {
            for(int y = -2; y <= 2; ++y)
            {
                float pcfDepth = texture(shadowMap, projCoords.xy + vec2(x, y) * texelSize).r;
                shadow += currentDepth - bias > pcfDepth ? 1.0 : 0.0;
            }
        }
        shadow /= 25.0;

        return 1.0f - shadow;
    }

    //float bias = 0.05;

    //float shadowFactor = 0.0;
    //vec2 inc = 1.0 / textureSize(shadowMap, 0);
    //for(int row = -1; row <= 1; ++row)
    //{
    //    for(int col = -1; col <= 1; ++col)
    //    {
    //        float textDepth = texture(shadowMap, projCoords.xy + vec2(row, col) * inc).r;
    //        shadowFactor += projCoords.z - bias > textDepth ? 1.0 : 0.0;
    //    }
    //}
    //shadowFactor /= 9.0;

    //if(projCoords.z > 1.0)
    //{
    //    shadowFactor = 1.0;
    //}

    //return 1 - shadowFactor;
}

void main()
{
    setupColours(material, outTexCoord);

    vec4 diffuseSpecularComp = calcDirectionalLight(directionalLight, mvVertexPos, mvVertexNormal);

    for (int i=0; i<MAX_POINT_LIGHTS; i++)
    {
        if ( pointLights[i].intensity > 0 )
        {
            diffuseSpecularComp += calcPointLight(pointLights[i], mvVertexPos, mvVertexNormal); 
        }
    }

    for (int i=0; i<MAX_SPOT_LIGHTS; i++)
    {
        if ( spotLights[i].pl.intensity > 0 )
        {
            diffuseSpecularComp += calcSpotLight(spotLights[i], mvVertexPos, mvVertexNormal);
        }
    }
    
    float shadow = calcShadow(mlightviewVertexPos);
    //if (shadow < 1){
    //    fragColor = vec4(0,0,0,0);
    //} else {
        fragColor = clamp(ambientC * vec4(ambientLight, 1) + diffuseSpecularComp * shadow, 0, 1);
    //}
}