
import java.awt.Transparency;
import java.awt.color.*;
import java.awt.image.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


import javax.imageio.ImageIO;

/**
 * Main class for ray tracing exercise.
 */
public class RayTracer {

	public int imageWidth;
	public int imageHeight;
	List<Surface> surfaces = new ArrayList<Surface>();
	List<Surface> newSurfaces = new ArrayList<Surface>();
	List<Material> materials = new ArrayList<Material>();
	List<Light> lights = new ArrayList<Light>();
	Camera camera=null;
	Settings set = null;
	Scene scene = null;

	/**
	 * Runs the ray tracer. Takes scene file, output image file and image size as
	 * input.
	 */
	public static void main(String[] args) {

		try {

			RayTracer tracer = new RayTracer();

			// Default values:
			tracer.imageWidth = 500;
			tracer.imageHeight = 500;

			if (args.length < 2)
				throw new RayTracerException(
						"Not enough arguments provided. Please specify an input scene file and an output image file for rendering.");

			String sceneFileName = args[0];
			String outputFileName = args[1];

			if (args.length > 3) {
				tracer.imageWidth = Integer.parseInt(args[2]);
				tracer.imageHeight = Integer.parseInt(args[3]);
			}

			// Parse scene file:
			tracer.parseScene(sceneFileName);

			// Render scene:
			tracer.renderScene(outputFileName);

			// } catch (IOException e) {
			// System.out.println(e.getMessage());
		} catch (RayTracerException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Parses the scene file and creates the scene. Change this function so it
	 * generates the required objects.
	 */
	public void parseScene(String sceneFileName) throws IOException, RayTracerException {
		FileReader fr = new FileReader(sceneFileName);

		BufferedReader r = new BufferedReader(fr);
		String line = null;
		int lineNum = 0;
		System.out.println("Started parsing scene file " + sceneFileName);

		while ((line = r.readLine()) != null) {
			line = line.trim();
			++lineNum;

			if (line.isEmpty() || (line.charAt(0) == '#')) { // This line in the scene file is a comment
				continue;
			} else {
				String code = line.substring(0, 3).toLowerCase();
				// Split according to white space characters:
				String[] params = line.substring(3).trim().toLowerCase().split("\\s+");

				if (code.equals("cam")) {
					Vector position = new Vector(Double.parseDouble(params[0]),Double.parseDouble(params[1]),Double.parseDouble(params[2]));
					Vector lookAt = new Vector(Double.parseDouble(params[3]),Double.parseDouble(params[4]),Double.parseDouble(params[5]));
					Vector upVec = new Vector(Double.parseDouble(params[6]),Double.parseDouble(params[7]),Double.parseDouble(params[8]));
					float distance=Float.parseFloat(params[9]);
					float width=Float.parseFloat(params[10]);
					boolean fisheye=Boolean.parseBoolean(params[11]);
					float fisheyeTransVal=Float.parseFloat(params[12]);
					camera =new Camera(position,lookAt,upVec,distance,width,fisheye,fisheyeTransVal);

					System.out.println(String.format("Parsed camera parameters (line %d)", lineNum));
				} else if (code.equals("set")) {
					Vector backgroundCol = new Vector (Double.parseDouble(params[0]), Double.parseDouble(params[1]),Double.parseDouble(params[2]));
					int numRays=Integer.parseInt(params[3]);
					int numRec=Integer.parseInt(params[4]);
					set = new Settings(backgroundCol ,numRays, numRec);
					System.out.println(String.format("Parsed general settings (line %d)", lineNum));
//					double[] backgroundColor = { Float.parseFloat(params[0]), Float.parseFloat(params[1]),
//							Float.parseFloat(params[2]) };
//					set.setBackGroundColor(backgroundColor);
//
//					set.setNumberOfShadowRays(Integer.parseInt(params[3]));
//
//					set.setMaxNumberOfRecursion(Integer.parseInt(params[4]));
//
//					set.setSuperSamplingLevel(1);
//
//					System.out.println(String.format("Parsed general settings (line %d)", lineNum));
				} else if (code.equals("mtl")) {
					Vector diffuseColor = new Vector(Double.parseDouble(params[0]), Double.parseDouble(params[1]),Double.parseDouble(params[2]) );
					Vector specularColor = new Vector( Double.parseDouble(params[3]), Double.parseDouble(params[4]),Double.parseDouble(params[5]) );
					Vector reflectionColor = new Vector( Double.parseDouble(params[6]), Double.parseDouble(params[7]),Double.parseDouble(params[8]) );
					float shininess=Float.parseFloat(params[9]);
					float transparency=Float.parseFloat(params[10]);
					Material material = new Material(diffuseColor,specularColor,reflectionColor,shininess,transparency);
					materials.add(material);
					System.out.println(String.format("Parsed material (line %d)", lineNum));
				} else if (code.equals("sph")) {
					Vector center = new Vector(Double.parseDouble(params[0]), Double.parseDouble(params[1]),Double.parseDouble(params[2]));
					Sphere sphere = new Sphere(center, Double.parseDouble(params[3]),Integer.parseInt(params[4]));
					surfaces.add(sphere);
					System.out.println(String.format("Parsed sphere (line %d)", lineNum));
				} else if (code.equals("pln")) {
					Vector normal=new Vector(Double.parseDouble(params[0]), Double.parseDouble(params[1]),Double.parseDouble(params[2]));
					double offset=Double.parseDouble(params[3]);
					int index=Integer.parseInt(params[4]);
					Plane plane = new Plane(normal,offset,index);
					surfaces.add(plane);
					System.out.println(String.format("Parsed plane (line %d)", lineNum));

//					Plane plane = new Plane();
//
//					Vector normal = new Vector(Double.parseDouble(params[0]), Double.parseDouble(params[1]), Double.parseDouble(params[2]));
//					plane.setNormal(normal);
//					plane.setOffset(Float.parseFloat(params[3]));
//					plane.setMaterialIndex(Integer.parseInt(params[4]));
//					surfaces.add(plane);
//
//					System.out.println(String.format("Parsed plane (line %d)", lineNum));
				} else if (code.equals("trg")) {


					Vector vector1 = new Vector(Double.parseDouble(params[0]), Double.parseDouble(params[1]),
							Double.parseDouble(params[2]));

					Vector vector2 = new Vector(Double.parseDouble(params[3]), Double.parseDouble(params[4]),
							Double.parseDouble(params[5]));

					Vector vector3 = new Vector(Double.parseDouble(params[6]), Double.parseDouble(params[7]),
							Double.parseDouble(params[8]));

					int MaterialIndex = Integer.parseInt(params[9]);



					System.out.println(String.format("Parsed plane (line %d)", lineNum));
				} else if (code.equals("lgt")) {

					Vector LightPosition = new Vector(Double.parseDouble(params[0]), Double.parseDouble(params[1]),
							Double.parseDouble(params[2]));

					Vector LightColor = new Vector ( Double.parseDouble(params[3]), Double.parseDouble(params[4]),
							Double.parseDouble(params[5]) );
					float specularIntensity = Float.parseFloat(params[6]);
					float shadowIntensity = Float.parseFloat(params[7]);
					float lightRadius = Float.parseFloat(params[8]);

					Light light = new Light(LightPosition, LightColor, specularIntensity, shadowIntensity, lightRadius);

					lights.add(light);

					System.out.println(String.format("Parsed light (line %d)", lineNum));
				} else {
					System.out.println(String.format("ERROR: Did not recognize object: %s (line %d)", code, lineNum));
				}

			}
		}
		if ((camera == null) || (set == null) || (surfaces.size() == 0) || (lights.size() == 0) || (materials.size() == 0)){
			System.out.println("Scene is not valid");
		}
		scene = new Scene(camera, set, surfaces, lights, materials , newSurfaces);
		System.out.println("Finished parsing scene file " + sceneFileName);
	}

	/**
	 * Renders the loaded scene and saves it to the specified file location.
	 */
	public void renderScene(String outputFileName) {
		long startTime = System.currentTimeMillis();

		// Create a byte array to hold the pixel data:
		byte[] rgbData = new byte[imageWidth * imageHeight * 3];

		Vector VecX = camera.lookAt.crossProduct(camera.upVector);
		VecX.normalize();
		camera.upVector = VecX.crossProduct(camera.lookAt);
		camera.upVector.normalize();
		Vector V_x = new Vector(VecX.x, VecX.y, VecX.z);
		V_x.normalize();
		Vector V_y = new Vector(camera.upVector.x, camera.upVector.y, camera.upVector.z);
		V_y.normalize();
		Vector V_z = new Vector(camera.lookAt.x, camera.lookAt.y, camera.lookAt.z);
		V_z.normalize();

		Vector P = (V_z.scalarMult(camera.screenDistance)).add(camera.position);
		double screenHeight = (imageHeight * camera.screenWidth)/ imageWidth ;

		Vector P0 = ((V_y.scalarMult(-1 * screenHeight / 2)).add(V_x.scalarMult(-1 * camera.screenWidth / 2))).add(P);

		for (int y = 0; y < imageHeight; y++) {
			P = P0;
			for (int x = 0; x < imageWidth; x++) {
				Vector finalcolor = new Vector(0.0, 0.0, 0.0);
				Vector ssP = P;
				double heightOffset = 0;
				double widthOffset = 0;
				ssP = (P.add(V_y.scalarMult(heightOffset))).add(V_x.scalarMult(widthOffset));
				Ray ray = new Ray(camera.position, ssP.add(camera.position.scalarMult(-1)));
				ray.v.normalize();
				Intersection hit = Intersection.getIntersction(ray, surfaces);
				if (hit.min_t == Double.MAX_VALUE) {
					finalcolor.x += set.backgroundCol.x;
					finalcolor.y += set.backgroundCol.y;
					finalcolor.z += set.backgroundCol.z;
				} else {
					newSurfaces = new ArrayList<Surface>(surfaces);
					newSurfaces.remove(hit.firstSurface);
					Vector col = color(hit, ray, set.maxNumRec);
					finalcolor.x += col.x;
					finalcolor.y += col.y;
					finalcolor.z += col.z;
				}
				// Write pixel color values in RGB format to rgbData:
				// Pixel [x, y] red component is in rgbData[(y * this.imageWidth + x) * 3]
				// green component is in rgbData[(y * this.imageWidth + x) * 3 + 1]
				// blue component is in rgbData[(y * this.imageWidth + x) * 3 + 2]
				rgbData[(imageWidth * (imageHeight - y - 1) + imageWidth - x - 1) * 3]    = (byte) (finalcolor.x * 255 );
				rgbData[(imageWidth * (imageHeight - y - 1) + imageWidth - x - 1) * 3 + 1] = (byte) (finalcolor.y * 255);
				rgbData[(imageWidth * (imageHeight - y - 1) + imageWidth - x - 1) * 3 + 2] = (byte) (finalcolor.z * 255);

				P = P.add(V_x.scalarMult(camera.screenWidth / imageWidth));

			}
			P0 = P0.add(V_y.scalarMult(screenHeight / imageHeight));
		}

		//
		// Each of the red, green and blue components should be a byte, i.e. 0-255

		long endTime = System.currentTimeMillis();
		Long renderTime = endTime - startTime;

		// The time is measured for your own conveniece, rendering speed will not affect
		// your score
		// unless it is exceptionally slow (more than a couple of minutes)
		System.out.println("Finished rendering scene in " + renderTime.toString() + " milliseconds.");

		// This is already implemented, and should work without adding any code.
		saveImage(this.imageWidth, rgbData, outputFileName);

		System.out.println("Saved file " + outputFileName);

	}

	private Vector color(Intersection hit, Ray ray, int recDepth) {
		if (recDepth == 0) {
			Vector col = new Vector(set.backgroundCol.x, set.backgroundCol.y, set.backgroundCol.z);
			return col;
		}
		Vector intersectionPoint = ray.p0.add(ray.v.scalarMult(hit.min_t));
		Vector N = hit.firstSurface.getNormal(intersectionPoint);
		if (N.dotProduct(ray.v) > 0) {
			N = N.scalarMult(-1);
		}
		N.normalize();
		Vector col = new Vector(0,0,0);
		Material mat = materials.get(hit.firstSurface.getMaterialIndex() - 1);

		for (Light light : scene.lights) {
			Vector L = light.position.add(intersectionPoint.scalarMult(-1));
			double rTemp = 0;
			double gTemp = 0;
			double bTemp = 0;
			L.normalize();
			double cTeta = N.dotProduct(L);
			if (cTeta < 0) {
				continue;
			}
			rTemp += mat.diffusion.x * light.color.x * cTeta;
			gTemp += mat.diffusion.y * light.color.y * cTeta;
			bTemp += mat.diffusion.z * light.color.z * cTeta;
			Vector R = N.scalarMult((L.scalarMult(2).dotProduct(N))).add(L.scalarMult(-1));
			double sTeta = Math.pow(R.dotProduct(ray.v.scalarMult(-1)),
					mat.shininess);
			rTemp += mat.specular.x * light.color.x * sTeta * (light.specularIntensity);
			gTemp += mat.specular.y * light.color.y * sTeta * (light.specularIntensity);
			bTemp += mat.specular.z * light.color.z * sTeta * (light.specularIntensity);

			double SoftshadowIntensity = softShadow(light, L.scalarMult(-1), intersectionPoint);
			col.x += rTemp * ((1 - light.shadowIntensity) + light.shadowIntensity * SoftshadowIntensity);
			col.y += gTemp * ((1 - light.shadowIntensity) + light.shadowIntensity * SoftshadowIntensity);
			col.z += bTemp * ((1 - light.shadowIntensity) + light.shadowIntensity * SoftshadowIntensity);
		}
		Vector transfCol = new Vector(0,0,0);
		if (mat.transparency > 0) {
			transfCol = culcTransColors(mat, N, ray, intersectionPoint, recDepth);
		}

		Vector reflectionColor = new Vector(0,0,0);
		if (mat.reflection.x> 0 || mat.reflection.y > 0 || mat.reflection.z > 0) {
			reflectionColor = culcRefColors(ray, N, intersectionPoint, mat, recDepth);
		}

		col.x = (col.x * (1 - mat.transparency) + transfCol.x * mat.transparency + reflectionColor.x);
		col.y = (col.y * (1 - mat.transparency) + transfCol.y * mat.transparency + reflectionColor.y);
		col.z = (col.z * (1 - mat.transparency) + transfCol.z * mat.transparency + reflectionColor.z);
		col.checkRange();
		return col;
	}
	private Vector reflectVector(Ray ray, Vector normal ){
		Vector R = ray.v.add(normal.scalarMult(-2 * normal.dotProduct(ray.v)));   /// formula
		R.normalize();
		return R;
	}

	private Vector culcRefColors(Ray ray, Vector N, Vector IntersectionPoint, Material mat, int recDepth) {
		Vector col = new Vector(0,0,0);

		Vector R = ray.v.add(N.scalarMult(-2 * ray.v.dotProduct(N)));
		R.normalize();
		Ray refRay = new Ray(IntersectionPoint.add(R.scalarMult(0.001)), R);
		Intersection hit = Intersection.getIntersction(refRay, surfaces);

		if (hit.min_t == Double.MAX_VALUE) {

			col.x = (set.backgroundCol.x * mat.reflection.x);
			col.y = (set.backgroundCol.y * mat.reflection.y);
			col.z = (set.backgroundCol.z * mat.reflection.z);
		} else {
			Vector tempCol = color(hit, refRay, recDepth - 1);
			col.x = (tempCol.x * mat.reflection.x);
			col.y = (tempCol.y * mat.reflection.y);
			col.z = (tempCol.z * mat.reflection.z);
		}
		if (col.x > 1) {
			col.x = 1;
		}
		if (col.y > 1) {
			col.y = 1;
		}
		if (col.z > 1) {
			col.z = 1;
		}
		return col;
	}

	public Vector culcTransColors(Material mat, Vector N, Ray ray, Vector intersectionPoint, int recDepth) {
		Vector col = new Vector(0,0,0);
		Ray transRay = new Ray(intersectionPoint.add(ray.v.scalarMult(0.001)), ray.v);
		Intersection transHit = Intersection.getIntersction(transRay, newSurfaces);
		newSurfaces.remove(transHit.firstSurface);
		if (transHit.min_t != Double.MAX_VALUE) {
			Vector tempCol = color(transHit, transRay, recDepth - 1);
			col.x = tempCol.x;
			col.y = tempCol.y;
			col.z = tempCol.z;
		} else {
			col.x = set.backgroundCol.x;
			col.y = set.backgroundCol.y;
			col.z = set.backgroundCol.z;
		}
		if (col.x > 1) {
			col.x = 1;
		}
		if (col.y > 1) {
			col.y = 1;
		}
		if (col.y > 1) {
			col.y = 1;
		}
		return col;
	}

	private double softShadow(Light light, Vector planeNormal, Vector intersectionPoint) {
		Plane plane = new Plane(planeNormal,planeNormal.dotProduct(light.position),1);
		Vector v_vec = plane.findVec(light.position);
		Vector u_vec = planeNormal.crossProduct(v_vec);
		v_vec.normalize();
		u_vec.normalize();

		Vector corner = (light.position.add(v_vec.scalarMult(-0.5 * light.radius)))
				.add(u_vec.scalarMult(-0.5 * light.radius));
		Vector full_v = (corner.add(v_vec.scalarMult(light.radius))).add(corner.scalarMult(-1));
		Vector full_u = (corner.add(u_vec.scalarMult(light.radius))).add(corner.scalarMult(-1));
		double scalar = 1.0 / set.numShadowRays;
		Vector v = full_v.scalarMult(scalar);
		Vector u = full_u.scalarMult(scalar);
		double sum = 0;
		for (int i = 0; i < set.numShadowRays; i++) {
			for (int j = 0; j < set.numShadowRays; j++) {
				sum += pointOnlight(light, corner, v, u, i, j, intersectionPoint);
			}
		}
		return sum / (set.numShadowRays * set.numShadowRays);
	}

	private int pointOnlight(Light light, Vector corner, Vector v, Vector u, int i, int j, Vector intersectionPoint) {
		Random random1 = new Random();
		double num1 = random1.nextDouble();
		double num2 = random1.nextDouble();
		Vector point = corner.add(v.scalarMult(i + num1).add(u.scalarMult(j + num2)));
		Vector pointDirction = point.add(intersectionPoint.scalarMult(-1));
		double Plength = Math.sqrt(pointDirction.dotProduct(pointDirction));
		if (checkDirectLight(pointDirction, intersectionPoint, Plength)) {
			return 0;
		}
		return 1;
	}

	private boolean checkDirectLight(Vector L, Vector intersectionPoint, double Llength) {

		L.normalize();
		Ray lightRay = new Ray(intersectionPoint.add(L.scalarMult(0.001)), L);
		if (Intersection.isIntersect(lightRay, scene, Llength)==1){
			return true;
		}
		else{
			return false;
		}

	}
	//////////////////////// FUNCTIONS TO SAVE IMAGES IN PNG FORMAT
	//////////////////////// //////////////////////////////////////////

	/*
	 * Saves RGB data as an image in png format to the specified location.
	 */
	public static void saveImage(int width, byte[] rgbData, String fileName) {
		try {

			BufferedImage image = bytes2RGB(width, rgbData);
			ImageIO.write(image, "png", new File(fileName));

		} catch (IOException e) {
			System.out.println("ERROR SAVING FILE: " + e.getMessage());
		}

	}

	/*
	 * Producing a BufferedImage that can be saved as png from a byte array of RGB
	 * values.
	 */
	public static BufferedImage bytes2RGB(int width, byte[] buffer) {
		int height = buffer.length / width / 3;
		ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
		ColorModel cm = new ComponentColorModel(cs, false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
		SampleModel sm = cm.createCompatibleSampleModel(width, height);
		DataBufferByte db = new DataBufferByte(buffer, width * height);
		WritableRaster raster = Raster.createWritableRaster(sm, db, null);
		BufferedImage result = new BufferedImage(cm, raster, false, null);

		return result;
	}

	public static class RayTracerException extends Exception {
		public RayTracerException(String msg) {
			super(msg);
		}
	}

}