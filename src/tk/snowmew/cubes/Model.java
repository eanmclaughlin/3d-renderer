package tk.snowmew.cubes;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import java.io.File;
import java.nio.FloatBuffer;
import java.util.ArrayList;

public class Model implements IMatrix
{
    Matrix4f modelMatrix = new Matrix4f();
    int VAO, vertexVBO, indVBO, texVBO;
    ShaderProgram shaderProgram;
    Vector3f rotation = new Vector3f(); Vector3f translation = new Vector3f(); Vector3f scale = new Vector3f(1,1,1);
    String textureName = "creeper";
    ArrayList<Mesh> meshes;
    private ObjFileParser parser;
/*
    Vertex[] verts = {
            //top
            new Vertex(new float[]{-0.5F, 0.5F, 0.5F, 1f}, new float[]{0.25f,0.5f}),//0
            new Vertex(new float[]{0.5F, 0.5F, 0.5F, 1f}, new float[]{0.125f,0.5f}),//1
            new Vertex(new float[]{0.5F, 0.5F, -0.5F, 1f}, new float[]{0.125f,0.25f}),//2
            new Vertex(new float[]{-0.5F, 0.5F, -0.5F, 1f}, new float[]{0.25f,0.25f}),//3

            new Vertex(new float[]{-0.5F, -0.5F, -0.5F, 1}, new float[]{0,0}),//4
            new Vertex(new float[]{-0.5F, -0.5F, 0.5F, 1}, new float[]{1,0}),//5
            new Vertex(new float[]{0.5F, -0.5F, 0.5F, 1}, new float[]{1,1}),//6
            new Vertex(new float[]{0.5F, -0.5F, -0.5F, 1}, new float[]{0,1})//7
    };
*/

/*
    byte[] inds = {
            //top
            0, 1, 2,
            0, 2, 3,

            0, 5, 6,
            0, 1, 6,

            0, 3, 4,
            0, 4, 5,


            7, 5, 6,
            7, 4, 5,

            7, 1, 6,
            7, 1, 2,

            7, 4, 3,
            7, 3, 2
    };
*/

    String[] vertAttribs = {
            "position",
            "in_tex"};

    String[] uniformAttribs= {
            "texture"
    };

    public Model(File file) {
        this(file,"assets/shaders/basic_block_vert.glsl","assets/shaders/basic_block_frag.glsl");
    }

    public Model(String file){
        this(file,"assets/shaders/basic_block_vert.glsl","assets/shaders/basic_block_frag.glsl");
    }

    public Model(String file, String vert, String frag){
        parser = new ObjFileParser(file);
        meshes = parser.getMeshes();
        shaderProgram = new ShaderProgram(vert, frag, vertAttribs, uniformAttribs);
        genIDs();
        buffer();
        update();
    }

    public Model(File file, String vert, String frag){
        parser = new ObjFileParser(file);
        meshes = parser.getMeshes();
        shaderProgram = new ShaderProgram(vert, frag, vertAttribs, uniformAttribs);
        genIDs();
        buffer();
        update();
    }

    public void update() {
        modelMatrix = new Matrix4f();
        modelMatrix.translate(new Vector3f(translation.x, translation.y, -translation.z));
        modelMatrix.rotate(rotation.x, new Vector3f(1.0F, 0.0F, 0.0F));
        modelMatrix.rotate(rotation.y, new Vector3f(0.0F, 1.0F, 0.0F));
        modelMatrix.rotate(rotation.z, new Vector3f(0.0F, 0.0F, 1.0F));
        modelMatrix.scale(scale);
    }

    private int getSizeOfModel(){
        int size = 0;
        for(Mesh mesh : meshes)
            size += mesh.sizeOfMesh();
        return size;
    }

    private int getSizeOfModelVertexCoords(){
        int size = 0;
        for(Mesh mesh : meshes)
            size += mesh.sizeOfVertCoords();
        return size;
    }

    private int getSizeOfModelTextureCoords(){
        int size = 0;
        for(Mesh mesh : meshes)
            size += mesh.sizeOfTexCoords();
        return size;
    }

    private int getSizeOfModelNormals(){
        int size = 0;
        for(Mesh mesh : meshes)
            size += mesh.sizeOfNormals();
        return size;
    }

    public void buffer() {
        FloatBuffer vBuf = BufferUtils.createFloatBuffer(getSizeOfModelVertexCoords());
        FloatBuffer tBuf = BufferUtils.createFloatBuffer(getSizeOfModelTextureCoords());

        for(Mesh mesh : meshes){
            vBuf.put(mesh.getMeshVertexesAsFloatBuffer());
            tBuf.put(mesh.getMeshTexturesAsFloatBuffer());
        }
        System.out.println(vBuf.toString());
        vBuf.flip();
        tBuf.flip();

        GL30.glBindVertexArray(VAO);

        for(int i = 0; i<vertAttribs.length; i++)
            GL20.glEnableVertexAttribArray(shaderProgram.getAttribLocation(vertAttribs[i]));

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexVBO);
        GL20.glVertexAttribPointer(shaderProgram.getAttribLocation("position"), 4, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vBuf, GL15.GL_STATIC_DRAW);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, texVBO);
        GL20.glVertexAttribPointer(shaderProgram.getAttribLocation("in_tex"), 2, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, tBuf, GL15.GL_STATIC_DRAW);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        GL30.glBindVertexArray(0);
    }

    public void render() {
        GL30.glBindVertexArray(VAO);
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, getSizeOfModelVertexCoords());
        GL30.glBindVertexArray(0);
    }

    public void genIDs() {
        VAO = GL30.glGenVertexArrays();
        vertexVBO = GL15.glGenBuffers();
        indVBO = GL15.glGenBuffers();
        texVBO = GL15.glGenBuffers();
    }

    public void bufferUniforms() {
        FloatBuffer buf = BufferUtils.createFloatBuffer(16);
        modelMatrix.store(buf);
        buf.flip();
        GL20.glUniformMatrix4(shaderProgram.getModelMatrixLocation(), false, buf);
//        Texture temp = TextureManager.getInstance().getTexture(textureName);
//        GL20.glUniform1i(shaderProgram.getUniformLocation("texture"),temp.getTexUnit());
    }

    public void translate(float x, float y, float z){
        translation = new Vector3f(x,y,z);
        update();
    }

    public void scale(float x, float y, float z){
        scale = new Vector3f(x,y,z);
        update();
    }

    public void rotateX(float angle){
        rotation.x += Renderer.degreesToRadians(angle);
        update();
    }

    public void rotateY(float angle){
        rotation.y += Renderer.degreesToRadians(angle);
        update();
    }

    public void rotateZ(float angle){
        rotation.z += Renderer.degreesToRadians(angle);
        update();
    }
}
