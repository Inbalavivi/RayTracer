import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Scene {
    Camera cam;
    Settings settings;
    List<Surface> surfaces;
    List<Light> lights;
    List<Material> materials;
    double epsilon = 0.005;

    public Scene(Camera cam, Settings settings, List<Surface> surfaces, List<Light> lights, List<Material> materials){
        this.cam = cam;
        this.settings = settings;
        this.surfaces = surfaces;
        this.lights = lights;
        this.materials = materials;
    }
    public Vector getColor(Surface firstSurface,double min_t, Ray ray, int recDepth) {
        if (recDepth == 0) { //return the background
            return  new Vector(this.settings.backgroundCol.x, this.settings.backgroundCol.y, this.settings.backgroundCol.z);
        }
        Vector intersection = ray.p0.add(ray.v.scalarMult(min_t)); //p0 +tV

        Vector normal;
        Vector N = new Vector(0,0,0);
        if( firstSurface instanceof Sphere){
            //N=(P0-P)/|P0-P|
            normal = intersection.add(((Sphere)firstSurface).center.scalarMult(-1)); /// N = P0 - P (P=looAt)
            normal.normalize();
            N = normal;
        }

        if( firstSurface instanceof Box){
            if((intersection.x >= ((Box) firstSurface).center.x + 0.5*((Box)firstSurface).edgeLength)){
                N.x = 1;
            }
            else if((intersection.x < ((Box) firstSurface).center.x - 0.5*((Box)firstSurface).edgeLength)){
                N.x = -1;
            }
            else if((intersection.y <= ((Box) firstSurface).center.y + 0.5*((Box)firstSurface).edgeLength)){
                N.y = 1;
            }
            else if((intersection.y > ((Box) firstSurface).center.y - 0.5*((Box)firstSurface).edgeLength)){
                N.y = -1;
            }
            else if((intersection.z <= ((Box) firstSurface).center.z + 0.5*((Box)firstSurface).edgeLength)){
                N.z = 1;
            }
            else if((intersection.z > ((Box) firstSurface).center.z - 0.5*((Box)firstSurface).edgeLength)){
                N.z = -1;
            }
        }
        if( firstSurface instanceof Plane){
            ((Plane) firstSurface).normal.normalize();
            N = ((Plane) firstSurface).normal;
        }

        if (N.dotProduct(ray.v) > 0) {
            N = N.scalarMult(-1);
        }
        N.normalize();
        Vector col = new Vector(0,0,0);

        int index = 0;
        if(firstSurface instanceof Sphere){index = ((Sphere) firstSurface).materialIndex;}
        if(firstSurface instanceof Plane){index = ((Plane) firstSurface).materialIndex;}
        if(firstSurface instanceof Box){index = ((Box) firstSurface).materialIndex;}
        Material mat = materials.get(index - 1);

        for (Light light : this.lights) {
            Vector L = light.position.add(intersection.scalarMult(-1));
            L.normalize();
            double N_dot_L = N.dotProduct(L);
            if (N_dot_L >= 0) {
                Vector tmp;
                tmp = mat.diffusion.vecsMult(light.color);
                tmp = tmp.scalarMult(N_dot_L);
                Vector R = N.scalarMult((L.scalarMult(2).dotProduct(N))).add(L.scalarMult(-1)); //R = (2L*N)N - L (the highlight vector)
                double Value = Math.pow(R.dotProduct(ray.v.scalarMult(-1)), mat.shininess);
                tmp = tmp.add(mat.specular.vecsMult(light.color).scalarMult(Value).scalarMult(light.specularIntensity));
                double softShadowIntensity = softShadow(light, L.scalarMult(-1), intersection);
                Vector shadowVec1 = new Vector(light.shadowIntensity, light.shadowIntensity, light.shadowIntensity);
                Vector shadowVec2 = new Vector(1 - light.shadowIntensity, 1 - light.shadowIntensity, 1 - light.shadowIntensity);
                col = col.add(tmp.vecsMult((shadowVec2.add(shadowVec1.scalarMult(softShadowIntensity)))));
            }
        }
        Vector reflectionColor = new Vector(0,0,0);
        if (mat.reflection.x > 0 || mat.reflection.y > 0 || mat.reflection.z > 0) {
            reflectionColor = ReflectionColor(ray, N, intersection, mat, recDepth-1);
        }

        Vector transfCol = new Vector(0,0,0);

        if (mat.transparency > 0) {
            transfCol = TransparencyColors( ray, intersection, recDepth-1);
        }
        //output color = (background color) * transparency + (diffuse + specular) * (1 - transparency) + (reflection color)
        col=(transfCol.scalarMult(mat.transparency)).add((col.scalarMult(1 - mat.transparency))).add(reflectionColor);   /// formula
        col.checkBound();
        return col;
    }

    public Vector reflectVector(Ray ray, Vector normal ){
        Vector R = ray.v.add(normal.scalarMult(-2 * normal.dotProduct(ray.v)));   /// formula
        R.normalize();
        return R;
    }

    public Vector ReflectionColor(Ray ray, Vector normal, Vector IntersectionP, Material mat, int recDepth) {
        Vector color;
        double min_t;
        Surface firstSurface;
        Vector R = reflectVector( ray, normal);
        Ray reflectionRay = new Ray(IntersectionP.add(R.scalarMult(epsilon)), R);
        Intersection intersection = Intersection.getMinIntersection(reflectionRay,this.surfaces);

        min_t = intersection.min_t;
        firstSurface = intersection.firstSurface;

        if (min_t == Double.MAX_VALUE) {
            color=this.settings.backgroundCol.vecsMult(mat.reflection);

        } else {
            Vector tempCol = getColor(firstSurface,min_t, reflectionRay, recDepth - 1);
            color=tempCol.vecsMult(mat.reflection);

        }
        color.checkBound();
        return color;
    }

    public Vector TransparencyColors( Ray ray, Vector intersectionPoint, int recDepth) {
        Vector col = new Vector(0,0,0);
        double t;
        Surface surface;
        Ray transRay = new Ray(intersectionPoint.add(ray.v.scalarMult(epsilon)), ray.v);
        List<Intersection> inters_list = Intersection.getAllIntersections(transRay, surfaces);

        for (Intersection inter : inters_list ){
            t = inter.min_t;
            surface = inter.firstSurface;
            if (t == Double.MAX_VALUE) {
                col =this.settings.backgroundCol;
            } else {
                Material mat = materials.get(inter.firstSurface.getMatIndex() - 1);
                col = col.add(getColor(surface, t, transRay, recDepth - 1).scalarMult(1-mat.transparency));
            }

        }
        col.checkBound();
        Vector one  = new Vector(1,1, 1);
        return (col.scalarMult(-1)).add(one);

    }

    public double softShadow(Light light, Vector planeNormal, Vector intersectionPoint) {
        //1. Find a plane which is perpendicular to the ray.
        Plane plane = new Plane(planeNormal,planeNormal.dotProduct(light.position),-1);
        //2. Define a rectangle on that plane, centered at the light source and as wide as the defined light radius.
        Vector uOnPlane = plane.findVec(light.position);
        uOnPlane.normalize();
        Vector vOnPlane = planeNormal.crossProduct(uOnPlane);
        vOnPlane.normalize();
        Vector zOnPlane = (light.position.add(vOnPlane.scalarMult(-0.5 * light.radius))).add(uOnPlane.scalarMult(-0.5 * light.radius));

        Vector V = (zOnPlane.add(vOnPlane.scalarMult(light.radius))).add(zOnPlane.scalarMult(-1));
        Vector U = (zOnPlane.add(uOnPlane.scalarMult(light.radius))).add(zOnPlane.scalarMult(-1));
        double scalar = 1.0 / this.settings.numShadowRays;
        Vector v = V.scalarMult(scalar);
        Vector u = U.scalarMult(scalar);

        //3. Divide the rectangle into a grid of N*N cells, where N is the number of shadow
        //rays from the scene parameters.
        double sum = 0;
        for (int i = 0; i < this.settings.numShadowRays; i++) {
            for (int j = 0; j < this.settings.numShadowRays; j++) {
//                5. Aggregate the values of all rays that were cast and count how many of them hit the required point on the surface.
                sum += shootRay( zOnPlane, v, u, i, j, intersectionPoint);
            }
        }
        //this light source will be multiplied by the number of rays that hit the surface divided by the total number of rays we sent.
        return (sum / Math.pow(this.settings.numShadowRays , 2));

    }

    public int shootRay( Vector edge, Vector v, Vector u, int i, int j, Vector intersection) {
        //4. we select a random point in each cell, and shoot the ray from the selected random point to the light.
        Random rand = new Random();
        double x = rand.nextDouble();
        double y = rand.nextDouble();
        Vector point = edge.add(v.scalarMult(i + x).add(u.scalarMult(j + y)));
        Vector pointDirection = point.add(intersection.scalarMult(-1));
        double directionMagnitude = Math.sqrt(pointDirection.dotProduct(pointDirection)); // ||pointDirection||
        pointDirection.normalize();
        Ray lightRay = new Ray(intersection.add(pointDirection.scalarMult(epsilon)), pointDirection);
        //check if there is an intersection
        double t;
        for (Surface s : this.surfaces) {
            t = s.intersect(lightRay);
            if (t < directionMagnitude && t > 0) {
                return 0;
            }
        }
        return 1;

    }

}

