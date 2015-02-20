package link.snowcat.cubes.render;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import link.snowcat.cubes.Cubes;

import java.nio.FloatBuffer;

public class Renderer{
    private static Renderer instance = new Renderer();
    private int projUniformLoc = -1;
    Matrix4f projectionMatrix = new Matrix4f();
    Camera camera;
    ShaderProgramManager shaderProgramManager;
    public Cubes cubeInstance;

    private Renderer(){ }

    public void setCubeInstance(Cubes cube){
        cubeInstance = cube;
    }

    public void setShaderProgramManager(ShaderProgramManager manager){
        shaderProgramManager = manager;
    }

    public void setCamera(Camera cam) {
        camera = cam;
    }

    public  void createProjectionMatrix(){
        projectionMatrix = new Matrix4f();
        projectionMatrix.setIdentity();
        float fieldOfView = 90.0F;
        float aspectRatio = (float)cubeInstance.getWidth()/(float)cubeInstance.getHeight();
        float near_plane = 0.1F;
        float far_plane = 500;

        float y_scale = coTangent(degreesToRadians(fieldOfView * 0.5f));
        float x_scale = y_scale / aspectRatio;
        float frustum_length = far_plane - near_plane;

        projectionMatrix.m00 = x_scale;
        projectionMatrix.m11 = y_scale;
        projectionMatrix.m22 = -((far_plane) / frustum_length);
        projectionMatrix.m23 = -1;
        projectionMatrix.m32 = -((far_plane * near_plane)/ frustum_length);
        projectionMatrix.m33 = 0;
    }

    public static Renderer getInstance(){
        return instance;
    }

    public void render(Model model) {
        shaderProgramManager.bindProgram(model.getProgramName());
        projUniformLoc = shaderProgramManager.getShaderProgram(model.getProgramName()).getProjectionMatrixLocation();
        model.bufferUniforms();
        bufferUniforms();
        cubeInstance.sun.buffer(shaderProgramManager.getShaderProgram(model.getProgramName()));
        camera.bufferUniforms(shaderProgramManager.getShaderProgram(model.getProgramName()).getViewMatrixLocation());
        model.render();
    }

    public void bufferUniforms() {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        projectionMatrix.store(buffer);
        buffer.flip();
        GL20.glUniformMatrix4(projUniformLoc, false, buffer);
    }

    public static float degreesToRadians(float degrees) {
        return (float)(Math.PI/180) * degrees;
    }

    public static float coTangent(float tan){
        return (float)(1.0D / Math.tan(tan));
    }
}