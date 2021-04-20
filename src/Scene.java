import java.util.List;
import java.util.Random;

public class Scene {
    Camera cam;
    Settings settings;
    List<Surface> surfaces;
    List<Light> lights;
    List<Material> materials;
    List<Surface> newSurfaces;

    public Scene(Camera cam, Settings settings, List<Surface> surfaces, List<Light> lights, List<Material> materials, List<Surface> newSurfaces){
        this.cam = cam;
        this.settings = settings;
        this.surfaces = surfaces;
        this.lights = lights;
        this.materials = materials;
        this.newSurfaces = newSurfaces;
    }}

