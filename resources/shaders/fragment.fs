#version 460

const int MAX_POINT_LIGHTS = 15;
const int MAX_SPOT_LIGHTS = 15;

out vec4 fragColor;

in VS_OUT {
    vec3 FragPos;
    vec3 Normal;
    vec2 TexCoords;
    vec4 Color;
} fs_in;

struct Attenuation
{
    float constant;
    float linear;
    float exponent;
};

struct PointLight
{
    vec3 colour;
    vec3 position;
    float intensity;
    Attenuation att;
    vec2 plane;
    //Shadow maps
    samplerCube staticShadowMap;
    samplerCube dynamicShadowMap;
};

struct SpotLight
{
    vec3 colour;
    vec3 position;
    float intensity;
    Attenuation att;
    // Spotlight specific parameters
    vec3 conedir;
    float cutoff;
    float outerCutoff;
    //Matrix
    mat4 lightSpaceMatrix;
    //Shadow maps
    sampler2D staticShadowMap;
    sampler2D dynamicShadowMap;
};

struct DirectionalLight
{
    vec3 colour;
    vec3 direction;
    float intensity;
    mat4 lightSpaceMatrix;
    // Shadow Maps
    bool shadowEnable;
    sampler2D staticShadowMap;
    sampler2D dynamicShadowMap;
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

uniform vec3 viewPos;
uniform mat4 view;
uniform bool shadowEnable;

vec4 ambientC;
vec4 diffuseC;
vec4 speculrC;

// Sampling Array for shadowMaps
vec3 shadowSamplingGrid[20] = vec3[]
(
   vec3(1, 1,  1), vec3( 1, -1,  1), vec3(-1, -1,  1), vec3(-1, 1,  1),
   vec3(1, 1, -1), vec3( 1, -1, -1), vec3(-1, -1, -1), vec3(-1, 1, -1),
   vec3(1, 1,  0), vec3( 1, -1,  0), vec3(-1, -1,  0), vec3(-1, 1,  0),
   vec3(1, 0,  1), vec3(-1,  0,  1), vec3( 1,  0, -1), vec3(-1, 0, -1),
   vec3(0, 1,  1), vec3( 0, -1,  1), vec3( 0, -1, -1), vec3( 0, 1, -1)
);

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
        ambientC = fs_in.Color;
        diffuseC = fs_in.Color;
        speculrC = fs_in.Color;
    }
    else
    {
        ambientC = fs_in.Color * material.ambient;
        diffuseC = fs_in.Color * material.diffuse;
        speculrC = fs_in.Color * material.specular;
    }
}

// Blinn-Phong lighting
vec4 calcBlinnPhong(vec3 light_color, float light_intensity, vec3 position, vec3 light_direction, vec3 normal){
    // Diffuse component
    float diff = max(dot(light_direction, normal), 0.0);
    if (diff != 0 ) {
        vec4 diffuse = diffuseC * vec4(light_color, 1.0) * light_intensity * diff;
        // Specular component
        vec3 viewDir = normalize(viewPos - position);
        vec3 halfwayDir = normalize(light_direction + viewDir);
        float spec = pow(max(dot(normal, halfwayDir), 0.0), specularPower);
        vec4 specular = speculrC * light_intensity * spec * material.reflectance * vec4(light_color, 1.0);
        return (diffuse + specular);
    } else {
        return vec4(0, 0, 0, 0);
    }
}

// Calculate Direction Light
vec4 calcDirectionalLight(DirectionalLight light, vec3 position, vec3 normal)
{
    return calcBlinnPhong(light.colour, light.intensity, position, normalize(light.direction), normal);
}

// Calculate Point Light
vec4 calcPointLight(PointLight light, vec3 position, vec3 normal)
{
    vec3 light_direction = normalize(light.position - position);

    vec4 light_colour = calcBlinnPhong(light.colour, light.intensity, position, light_direction, normal);

    // Apply Attenuation
    float distance = length(light_direction);
    float attenuationInv = light.att.constant + light.att.linear * distance +
        light.att.exponent * distance * distance;
    return light_colour / attenuationInv;
}

// Calculate Spot Light
vec4 calcSpotLight(SpotLight light, vec3 position, vec3 normal)
{
    vec3 light_direction = normalize(light.position - position);

    float theta = dot(light_direction, normalize(-light.conedir));
    float epsilon = light.cutoff - light.outerCutoff;
    float intensity = clamp((theta - light.outerCutoff) / epsilon, 0.0, 1.0);

    vec4 light_colour = calcBlinnPhong(light.colour, intensity, light.position, light_direction, normal);

    // Apply Attenuation
    float distance = length(light_direction);
    float attenuationInv = light.att.constant + light.att.linear * distance +
        light.att.exponent * distance * distance;
    return light_colour / attenuationInv;
}

float calcShadow(vec3 position, vec3 light_position, samplerCube shadowMap, vec2 plane, bool shadows)
{
    if (shadows) {
        vec3 fragToLight = position - light_position;
        // use the light to fragment vector to sample from the depth map
        float closestDepth = texture(shadowMap, fragToLight).r;
        // it is currently in linear range between [0,1]. Re-transform back to original value
        closestDepth *= plane.y;
        // now get current linear depth as the length between the fragment and light position
        float currentDepth = length(fragToLight);
        // now test for shadows
        float bias = 0.005;
        float shadow = currentDepth -  bias > closestDepth ? 0.0 : 1.0;

        return shadow;
    } else {
        return 1.0f;
    }
}

float calcShadow2D(mat4 matrix, vec3 position, sampler2D shadowMap, bool shadows)
{
    if (shadows) {
        vec4 coord = matrix * vec4(position, 1.0);
        vec3 projCoords = coord.xyz / coord.w;
        projCoords = projCoords * 0.5 + 0.5;

        float currentDepth = projCoords.z;
        float bias = 0.0001f; //0.001f

        float shadow = 0.0;
        vec2 texelSize = 1.0 / textureSize(shadowMap, 0);
        for(int x = -1; x <= 1; ++x)
        {
            for(int y = -1; y <= 1; ++y)
            {
                float pcfDepth = texture(shadowMap, projCoords.xy + vec2(x, y) * texelSize).r;
                shadow += currentDepth - bias < pcfDepth ? 1.0 : 0.0;
            }
        }
        shadow /= 9.0;

        return shadow;
    } else {
        return 1.0f;
    }
}

void main()
{
    // Setup Material
    setupColours(material, fs_in.TexCoords);

    // Variables
    vec4 diffuseSpecularComp = vec4(0,0,0,0);
    float staticShadow = 0, dynamicShadow = 0;
    vec4 component = vec4(0,0,0,0);

    // Calculate directional light
    if (directionalLight.intensity > 0) {
        component = calcDirectionalLight(directionalLight, fs_in.FragPos, fs_in.Normal);
        if (length(component) > 0 && directionalLight.shadowEnable) {
            staticShadow = calcShadow2D(directionalLight.lightSpaceMatrix, fs_in.FragPos, directionalLight.staticShadowMap, shadowEnable);
            if (staticShadow != 0) {
                dynamicShadow = calcShadow2D(directionalLight.lightSpaceMatrix, fs_in.FragPos, directionalLight.dynamicShadowMap, shadowEnable);
            }
            diffuseSpecularComp += component * staticShadow * dynamicShadow;
        } else {
            diffuseSpecularComp += component;
        }
    }

    // Calculate Point Lights
    for (int i=0; i<MAX_POINT_LIGHTS; i++)
    {
        if (pointLights[i].intensity > 0 )
        {
            component = calcPointLight(pointLights[i], fs_in.FragPos, fs_in.Normal);
            if (length(component) > 0) {
                staticShadow = calcShadow(fs_in.FragPos, pointLights[i].position, pointLights[i].staticShadowMap, pointLights[i].plane, shadowEnable);
                if (staticShadow != 0) {
                    dynamicShadow = calcShadow(fs_in.FragPos, pointLights[i].position, pointLights[i].dynamicShadowMap, pointLights[i].plane, shadowEnable);
                }
                diffuseSpecularComp += component * staticShadow * dynamicShadow;
            }
        }
    }
    // Calculate Spot Lights
    for (int i=0; i<MAX_SPOT_LIGHTS; i++)
    {
        if (spotLights[i].intensity > 0 )
        {
            component = calcSpotLight(spotLights[i], fs_in.FragPos, fs_in.Normal);
            if (length(component) > 0) {
                staticShadow = calcShadow2D(spotLights[i].lightSpaceMatrix, fs_in.FragPos, spotLights[i].staticShadowMap, shadowEnable);
                if (staticShadow != 0) {
                    dynamicShadow = calcShadow2D(spotLights[i].lightSpaceMatrix, fs_in.FragPos, spotLights[i].dynamicShadowMap, shadowEnable);
                }
                diffuseSpecularComp += component * staticShadow * dynamicShadow;
            }
        }
    }

    fragColor = clamp(ambientC * vec4(ambientLight, 1) + diffuseSpecularComp, 0, 1);
}