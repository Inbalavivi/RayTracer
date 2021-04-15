
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
	List<Primitive> primitives = new ArrayList<Primitive>();
	List<Primitive> UpdatedPrimitives = new ArrayList<Primitive>();
	List<Material> materials = new ArrayList<Material>();
	List<Light> lights = new ArrayList<Light>();
	Camera cam = new Camera();
	Settings set = new Settings();
	Scene scene = new Scene();

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
					cam.setcamPosition(new Vector(Double.parseDouble(params[0]), Double.parseDouble(params[1]),
							Double.parseDouble(params[2])));


					cam.setCamLookAtDirection((new Vector(Double.parseDouble(params[3]), Double.parseDouble(params[4]),
							Double.parseDouble(params[5]))).add(cam.camPosition.scalarMult(-1)));

					cam.SetCamUpVector(new Vector(Double.parseDouble(params[6]), Double.parseDouble(params[7]),
							Double.parseDouble(params[8])));

					cam.SetCamScreenDistance(Float.parseFloat(params[9]));

					cam.SetCamScreenWidth(Float.parseFloat(params[10]));

					System.out.println(String.format("Parsed camera parameters (line %d)", lineNum));
				} else if (code.equals("set")) {
					double[] backgroundColor = { Float.parseFloat(params[0]), Float.parseFloat(params[1]),
							Float.parseFloat(params[2]) };
					set.setBackGroundColor(backgroundColor);

					set.setNumberOfShadowRays(Integer.parseInt(params[3]));

					set.setMaxNumberOfRecursion(Integer.parseInt(params[4]));

					set.setSuperSamplingLevel(1);


					System.out.println(String.format("Parsed general settings (line %d)", lineNum));
				} else if (code.equals("mtl")) {
					Material material = new Material();

					Vector diffuseColor = new Vector( Double.parseDouble(params[0]), Double.parseDouble(params[1]),
							Double.parseDouble(params[2]) );
					material.setDiffuseColor(diffuseColor);

					Vector specularColor = new Vector(Double.parseDouble(params[3]), Double.parseDouble(params[4]),
							Double.parseDouble(params[5]) );
					material.setSpecularColor(specularColor);

					Vector reflectionColor = new Vector( Double.parseDouble(params[6]), Double.parseDouble(params[7]),
							Double.parseDouble(params[8]) );
					material.setReflectionColor(reflectionColor);

					material.setPhongSpecularityCoefficient(Float.parseFloat(params[9]));

					material.setTransparency(Float.parseFloat(params[10]));

					// material.setBonus(Float.parseFloat(params[11]));

					materials.add(material);

					System.out.println(String.format("Parsed material (line %d)", lineNum));
				} else if (code.equals("sph")) {

					Sphere sphere = new Sphere();
					sphere.setCenter(params[0], params[1], params[2]);
					sphere.setRadius(params[3]);
					sphere.setMaterial(params[4]);

					primitives.add(sphere);

					System.out.println(String.format("Parsed sphere (line %d)", lineNum));
				} else if (code.equals("pln")) {

					Plane plane = new Plane();

					Vector normal = new Vector(Double.parseDouble(params[0]), Double.parseDouble(params[1]),
							Double.parseDouble(params[2]));
					plane.setNormal(normal);

					plane.setOffset(Float.parseFloat(params[3]));

					plane.setMaterialIndex(Integer.parseInt(params[4]));

					primitives.add(plane);

					System.out.println(String.format("Parsed plane (line %d)", lineNum));
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
		scene.setCam(cam);
		scene.setSet(set);
		scene.setMaterials(materials);
		scene.setPrimitives(primitives);
		scene.setLights(lights);
		if (!scene.checkValid()) {
			System.out.println("Error in Scene file");
		}

		System.out.println("Finished parsing scene file " + sceneFileName);

	}

	/**
	 * Renders the loaded scene and saves it to the specified file location.
	 */
	public void renderScene(String outputFileName) {
		long startTime = System.currentTimeMillis();

		// Create a byte array to hold the pixel data:
		byte[] rgbData = new byte[this.imageWidth * this.imageHeight * 3];
		int ssl = set.SuperSamplingLevel;
		int ssHeight = imageHeight * ssl;
		int ssWidth = imageWidth * ssl;

		double[][] M = computeNewCoordinate();
		Vector Vx = new Vector(M[0][0],M[0][1],M[0][2]);
		Vector Vy = new Vector(M[1][0],M[1][1],M[1][2]);
		Vector Vz = new Vector(M[2][0],M[2][1],M[2][2]);
		Vx.normalize();
		Vy.normalize();
		Vz.normalize();
		Vector P = (Vz.scalarMult(cam.camScreenDistance)).add(cam.camPosition);
		double aspectRatio = imageHeight / imageWidth;
		double camScreenHeight = aspectRatio * cam.camScreenWidth;

		Vector P0 = ((Vy.scalarMult(-1 * camScreenHeight / 2)).add(Vx.scalarMult(-1 * cam.camScreenWidth / 2)))
				.add(P);

		double pixelWidth = (cam.camScreenWidth / ssWidth);
		double pixelHeight = (camScreenHeight / ssHeight);

		for (int y = 0; y < imageHeight; y++) {
			P = P0;
			for (int x = 0; x < imageWidth; x++) {
				Vector finalcolor = new Vector(0.0, 0.0, 0.0);
				Vector ssP = P;
				for (int sRaw = 0; sRaw < ssl; sRaw++) {
					for (int sCol = 0; sCol < ssl; sCol++) {
						double heightOffset = ((ssl > 1) ? ((new Random().nextDouble() + sRaw) * (pixelWidth)) : 0);
						double widthOffset = ((ssl > 1) ? ((new Random().nextDouble() + sCol) * (pixelHeight)) : 0);
						ssP = (P.add(Vy.scalarMult(heightOffset))).add(Vx.scalarMult(widthOffset));
						Ray ray = new Ray(cam.camPosition, ssP.add(cam.camPosition.scalarMult(-1)));
						ray.directionVector.normalize();
						Intersection hit = Intersection.FindIntersction(ray, primitives);
						if (hit.min_t == Double.MAX_VALUE) {
							finalcolor.x += set.backgroundColor[0];
							finalcolor.y += set.backgroundColor[1];
							finalcolor.z += set.backgroundColor[2];
						} else {
							UpdatedPrimitives = new ArrayList<Primitive>(primitives);
							UpdatedPrimitives.remove(hit.min_primitive);
							Vector col = color(hit, ray, set.MaxNumberOfRecursion);
							finalcolor.x += col.x;
							finalcolor.y += col.y;
							finalcolor.z += col.z;
						}
					}
				}
				rgbData[(this.imageWidth * (this.imageHeight - y - 1) + imageWidth - x - 1)
						* 3] = (byte) (finalcolor.x * 255 / (ssl * ssl));
				rgbData[(this.imageWidth * (this.imageHeight - y - 1) + imageWidth - x - 1) * 3
						+ 1] = (byte) (finalcolor.y * 255 / (ssl * ssl));
				rgbData[(this.imageWidth * (this.imageHeight - y - 1) + imageWidth - x - 1) * 3
						+ 2] = (byte) (finalcolor.z * 255 / (ssl * ssl));

				P = P.add(Vx.scalarMult(cam.camScreenWidth / imageWidth));

			}
			P0 = P0.add(Vy.scalarMult(camScreenHeight / imageHeight));
		}
		// Write pixel color values in RGB format to rgbData:
		// Pixel [x, y] red component is in rgbData[(y * this.imageWidth + x) * 3]
		// green component is in rgbData[(y * this.imageWidth + x) * 3 + 1]
		// blue component is in rgbData[(y * this.imageWidth + x) * 3 + 2]
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
			Vector col = new Vector(set.backgroundColor[0], set.backgroundColor[1], set.backgroundColor[2]);
			return col;
		}
		Vector intersectionPoint = ray.basePoint.add(ray.directionVector.scalarMult(hit.min_t));
		Vector N = hit.min_primitive.findNormal(intersectionPoint);
		if (N.dotProduct(ray.directionVector) > 0) {
			N = N.scalarMult(-1);
		}
		N.normalize();
		Vector col = new Vector(0,0,0);
		Material mat = materials.get(hit.min_primitive.getMterialIndex() - 1);

		for (Light light : scene.lights) {
			Vector L = light.lightPosition.add(intersectionPoint.scalarMult(-1));
			double rTemp = 0;
			double gTemp = 0;
			double bTemp = 0;
			L.normalize();
			double cTeta = N.dotProduct(L);
			if (cTeta < 0) {
				continue;
			}
			rTemp += mat.diffuseColor.x * light.lightColor.x * cTeta;
			gTemp += mat.diffuseColor.y * light.lightColor.y * cTeta;
			bTemp += mat.diffuseColor.z * light.lightColor.z * cTeta;
			Vector R = N.scalarMult((L.scalarMult(2).dotProduct(N))).add(L.scalarMult(-1));
			double sTeta = Math.pow(R.dotProduct(ray.directionVector.scalarMult(-1)),
					mat.phongSpecularityCoefficient);
			rTemp += mat.specularColor.x * light.lightColor.x * sTeta * (light.specularIntensity);
			gTemp += mat.specularColor.y * light.lightColor.y * sTeta * (light.specularIntensity);
			bTemp += mat.specularColor.z * light.lightColor.z * sTeta * (light.specularIntensity);

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
		if (mat.reflectionColor.x> 0 || mat.reflectionColor.y > 0 || mat.reflectionColor.z > 0) {
			reflectionColor = culcRefColors(ray, N, intersectionPoint, mat, recDepth);
		}

		col.x = (col.x * (1 - mat.transparency) + transfCol.x * mat.transparency + reflectionColor.x);
		col.y = (col.y * (1 - mat.transparency) + transfCol.y * mat.transparency + reflectionColor.y);
		col.z = (col.z * (1 - mat.transparency) + transfCol.z * mat.transparency + reflectionColor.z);
		col.checkRange();
		return col;
	}
	private Vector reflectVector(Ray ray, Vector normal ){
		Vector R = ray.directionVector.add(normal.scalarMult(-2 * normal.dotProduct(ray.directionVector)));   /// formula
		R.normalize();
		return R;
	}

	private Vector culcRefColors(Ray ray, Vector N, Vector IntersectionPoint, Material mat, int recDepth) {
		Vector col = new Vector(0,0,0);

		Vector R = ray.directionVector.add(N.scalarMult(-2 * ray.directionVector.dotProduct(N)));
		R.normalize();
		Ray refRay = new Ray(IntersectionPoint.add(R.scalarMult(0.001)), R);
		Intersection hit = Intersection.FindIntersction(refRay, primitives);

		if (hit.min_t == Double.MAX_VALUE) {

			col.x = (set.backgroundColor[0] * mat.reflectionColor.x);
			col.y = (set.backgroundColor[1] * mat.reflectionColor.y);
			col.z = (set.backgroundColor[2] * mat.reflectionColor.z);
		} else {
			Vector tempCol = color(hit, refRay, recDepth - 1);
			col.x = (tempCol.x * mat.reflectionColor.x);
			col.y = (tempCol.y * mat.reflectionColor.y);
			col.z = (tempCol.z * mat.reflectionColor.z);
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
		Ray transRay = new Ray(intersectionPoint.add(ray.directionVector.scalarMult(0.001)), ray.directionVector);
		Intersection transHit = Intersection.FindIntersction(transRay, UpdatedPrimitives);
		UpdatedPrimitives.remove(transHit.min_primitive);
		if (transHit.min_t != Double.MAX_VALUE) {
			Vector tempCol = color(transHit, transRay, recDepth - 1);
			col.x = tempCol.x;
			col.y = tempCol.y;
			col.z = tempCol.z;
		} else {
			col.x = set.backgroundColor[0];
			col.y = set.backgroundColor[1];
			col.z = set.backgroundColor[2];
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
		Plane lightArea = new Plane();
		lightArea.setNormal(planeNormal);
		lightArea.setOffset(lightArea.findOffset(light.lightPosition));
		Vector v_vec = lightArea.findVecOnPlane(light.lightPosition);
		Vector u_vec = planeNormal.crossProduct(v_vec);
		v_vec.normalize();
		u_vec.normalize();

		Vector corner = (light.lightPosition.add(v_vec.scalarMult(-0.5 * light.lightRadius)))
				.add(u_vec.scalarMult(-0.5 * light.lightRadius));
		Vector full_v = (corner.add(v_vec.scalarMult(light.lightRadius))).add(corner.scalarMult(-1));
		Vector full_u = (corner.add(u_vec.scalarMult(light.lightRadius))).add(corner.scalarMult(-1));
		double scalar = 1.0 / set.NumberOfShadowRays;
		Vector v = full_v.scalarMult(scalar);
		Vector u = full_u.scalarMult(scalar);
		double sum = 0;
		for (int i = 0; i < set.NumberOfShadowRays; i++) {
			for (int j = 0; j < set.NumberOfShadowRays; j++) {
				sum += pointOnlight(light, corner, v, u, i, j, intersectionPoint);
			}
		}
		return sum / (set.NumberOfShadowRays * set.NumberOfShadowRays);
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
		return Intersection.isIntersect(lightRay, scene, Llength);

	}

	private double[][] computeNewCoordinate() {
		double[][] M = new double[3][3];
		Vector Vx = cam.lookAtDirection.crossProduct(cam.camUpVector);
		Vx.normalize();
		cam.camUpVector = (Vx).crossProduct(cam.lookAtDirection);
		cam.camUpVector.normalize();
		double[] data={Vx.x,Vx.y,Vx.z};
		double[] upVecData={cam.camUpVector.x,cam.camUpVector.y,cam.camUpVector.z};
		double[] lookAtData={cam.lookAtDirection.x,cam.lookAtDirection.y,cam.lookAtDirection.z};
		M[0] = data;
		M[1] = upVecData;
		M[2] = lookAtData;
		return M;
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