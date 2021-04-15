
import java.util.List;

public class Scene {
    Camera cam;
    Settings set;
    List<Material> materials;
    List<Primitive> Primitives;
    /*List<Plane> planes;
    List<Sphere> spheres;
    List<Triangle> triangles;
    */
    List<Light> lights;

    boolean bCam=false;
    boolean bSet=false;
    boolean bPrimitives =false;
    boolean bMaterials=false;
    /*boolean Planes=false;
    boolean Spheres=false;
    boolean Triangles=false;*/
    boolean bLights=false;


    // ******** recommended checking valid *****************************
    public boolean checkValid() {
        if(bCam && bSet&& bMaterials && bPrimitives  && bLights) {
            return true;
        }
        return false;
    }
    void setCam(Camera cam) {
        this.cam =cam;
        bCam= true;

    }

    public void setSet(Settings set) {
        this.set =set;
        bSet =true;

    }

    public void setMaterials(List<Material> materials) {
        this.materials = materials;
        bMaterials =true;
    }

    /*public void setPlanes(List<Plane> planes) {
        this.planes = planes;
        Planes =true;

    }
    public void setSpheres(List<Sphere> spheres) {
        this.spheres = spheres;
        Spheres =true;
    }
    public void setTriangles(List<Triangle> triangles) {
        this.triangles = triangles;
        Triangles =true;
    }
*/
    public void setLights(List<Light> lights) {
        this.lights = lights;
        bLights =true;
    }
    public void setPrimitives(List<Primitive> primitives) {
        this.Primitives=primitives;
        bPrimitives =true;
    }



}

