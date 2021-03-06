package link.snowcat.cubes.lights;

import link.snowcat.cubes.generated.ShaderProgram;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Vector3f;

/**
 * User: Pepper
 * Date: 4/25/13
 * Time: 9:34 PM
 * Project: Cubes
 */

public class DirectionalLight extends Light {

    public DirectionalLight(Vector3f dir, Vector3f col, float intensity){
        super(dir, col, intensity);
    }

    public Vector3f getDirection(){
        return position;
    }

    public void setDirection(Vector3f direction){
        super.setPosition(direction);
    }

    public void buffer(ShaderProgram program){
        GL20.glUniform3(program.getUniformAttributes().get("dirLight.color"), getColorAsFBuffer());
        GL20.glUniform3(program.getUniformAttributes().get("dirLight.position"), getPositionAsFBuffer());
        GL20.glUniform1f(program.getUniformAttributes().get("dirLight.intensity"), getIntensity());
    }
}
