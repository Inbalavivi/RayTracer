package RayTracing;

import java.awt.Transparency;
import java.awt.color.*;
import java.awt.image.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

/**
 *  Main class for ray tracing exercise.
 */
public class RayTracer {

	public int imageWidth;
	public int imageHeight;
	Scene scene = null;
	Camera cam = null;
	GeneralSettings settings = null;
	List<Surface> surfaces = new ArrayList<Surface>();
	List<Material> materials = new ArrayList<Material>();
	List<Light> lights = new ArrayList<Light>();

	/**
	 * Runs the ray tracer. Takes scene file, output image file and image size as input.
	 */
	public static void main(String[] args) {

		try {

			RayTracer tracer = new RayTracer();

                        // Default values:
			tracer.imageWidth = 500;
			tracer.imageHeight = 500;

			if (args.length < 2)
				throw new RayTracerException("Not enough arguments provided. Please specify an input scene file and an output image file for rendering.");

			String sceneFileName = args[0];
			String outputFileName = args[1];

			if (args.length > 3)
			{
				tracer.imageWidth = Integer.parseInt(args[2]);
				tracer.imageHeight = Integer.parseInt(args[3]);
			}


			// Parse scene file:
			tracer.parseScene(sceneFileName);

			// Render scene:
			tracer.renderScene(outputFileName);

//		} catch (IOException e) {
//			System.out.println(e.getMessage());
		} catch (RayTracerException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}


	}

	/**
	 * Parses the scene file and creates the scene. Change this function so it generates the required objects.
	 */
	public void parseScene(String sceneFileName) throws IOException, RayTracerException
	{
		FileReader fr = new FileReader(sceneFileName);

		BufferedReader r = new BufferedReader(fr);
		String line = null;
		int lineNum = 0;
		System.out.println("Started parsing scene file " + sceneFileName);

		while ((line = r.readLine()) != null)
		{
			line = line.trim();
			++lineNum;

			if (line.isEmpty() || (line.charAt(0) == '#'))
			{  // This line in the scene file is a comment
				continue;
			}
			else
			{
				String code = line.substring(0, 3).toLowerCase();
				// Split according to white space characters:
				String[] params = line.substring(3).trim().toLowerCase().split("\\s+");

				if (code.equals("cam"))
				{	
					Vector position = new Vector(Double.parseDouble(params[0]),Double.parseDouble(params[1]),Double.parseDouble(params[2]));
					Vector lookAt = new Vector(Double.parseDouble(params[3]),Double.parseDouble(params[4]),Double.parseDouble(params[5]));
					Vector upVec = new Vector(Double.parseDouble(params[6]),Double.parseDouble(params[7]),Double.parseDouble(params[8]));
					float distance=Float.parseFloat(params[9]);
					float width=Float.parseFloat(params[10]);
					boolean fisheye=Boolean.parseBoolean(params[11]);
    				float fisheyeTransVal=Float.parseFloat(params[12]);
					cam =new Camera(position,lookAt,upVec,distance,width,fisheye,fisheyeTransVal); //add direction
					System.out.println(String.format("Parsed camera parameters (line %d)", lineNum));
				}
				else if (code.equals("set"))
				{ 	
					
					Vector backgroundCol = new Vector (Double.parseDouble(params[0]), Double.parseDouble(params[1]),Double.parseDouble(params[2]));
					int numRays=Integer.parseInt(params[3]);
					int numRec=Integer.parseInt(params[4]);
					settings = new GeneralSettings(backgroundCol ,numRays, numRec);
					System.out.println(String.format("Parsed general settings (line %d)", lineNum));
				}
				else if (code.equals("mtl"))
				{
					Vector diffuseColor = new Vector(Double.parseDouble(params[0]), Double.parseDouble(params[1]),Double.parseDouble(params[2]) );
					Vector specularColor = new Vector( Double.parseDouble(params[3]), Double.parseDouble(params[4]),Double.parseDouble(params[5]) );
					Vector reflectionColor = new Vector( Double.parseDouble(params[6]), Double.parseDouble(params[7]),Double.parseDouble(params[8]) );
					float shininess=Float.parseFloat(params[9]);
					float transparency=Float.parseFloat(params[10]);
					Material material = new Material(diffuseColor,specularColor,reflectionColor,shininess,transparency);
					materials.add(material);
					System.out.println(String.format("Parsed material (line %d)", lineNum));
				}
				else if (code.equals("sph"))
				{	Vector center = new Vector(Double.parseDouble(params[0]), Double.parseDouble(params[1]),Double.parseDouble(params[2]));
                    Sphere sphere = new Sphere(center, Double.parseDouble(params[3]),Integer.parseInt(params[4]));
					surfaces.add(sphere);
					System.out.println(String.format("Parsed sphere (line %d)", lineNum));
				}
				else if (code.equals("pln"))
				{
					Vector normal=new Vector(Double.parseDouble(params[0]), Double.parseDouble(params[1]),Double.parseDouble(params[2]));
					double offset=Double.parseDouble(params[3]);
					int index=Integer.parseInt(params[4]);
					Plane plane = new Plane(normal,offset,index);
					surfaces.add(plane);
					System.out.println(String.format("Parsed plane (line %d)", lineNum));
				}
				else if (code.equals("lgt"))
				{
					Vector lightPos=new Vector(Double.parseDouble(params[0]), Double.parseDouble(params[1]),Double.parseDouble(params[2]));
					Vector color=new Vector( Double.parseDouble(params[3]), Double.parseDouble(params[4]),Double.parseDouble(params[5]) );
					float specularIntensity=Float.parseFloat(params[6]);
					float shadowIntensity=Float.parseFloat(params[7]);
					float radius=Float.parseFloat(params[8]);
					Light light = new Light(lightPos, color, specularIntensity, shadowIntensity, radius);
					lights.add(light);
					System.out.println(String.format("Parsed light (line %d)", lineNum));
				}
				else
				{
					System.out.println(String.format("ERROR: Did not recognize object: %s (line %d)", code, lineNum));
				}
			}
		}
		// It is recommended that you check here that the scene is valid,
        // for example camera settings and all necessary materials were defined.
		if ((cam == null) || (settings == null) || (surfaces.size() == 0) || (lights.size() == 0) || (materials.size() == 0)){
			System.out.println("Scene is not valid");
		}
		scene = new Scene(cam, settings, surfaces, lights, materials);		
		System.out.println("Finished parsing scene file " + sceneFileName);
	}

	/**
	 * Renders the loaded scene and saves it to the specified file location.
	 */
	public void renderScene(String outputFileName)
	{
		long startTime = System.currentTimeMillis();

		// Create a byte array to hold the pixel data:
		byte[] rgbData = new byte[this.imageWidth * this.imageHeight * 3];


                // Put your ray tracing code here!
                //
                // Write pixel color values in RGB format to rgbData:
                // Pixel [x, y] red component is in rgbData[(y * this.imageWidth + x) * 3]
                //            green component is in rgbData[(y * this.imageWidth + x) * 3 + 1]
                //             blue component is in rgbData[(y * this.imageWidth + x) * 3 + 2]
                //
                // Each of the red, green and blue components should be a byte, i.e. 0-255


		long endTime = System.currentTimeMillis();
		Long renderTime = endTime - startTime;

                // The time is measured for your own conveniece, rendering speed will not affect your score
                // unless it is exceptionally slow (more than a couple of minutes)
		System.out.println("Finished rendering scene in " + renderTime.toString() + " milliseconds.");

                // This is already implemented, and should work without adding any code.
		saveImage(this.imageWidth, rgbData, outputFileName);

		System.out.println("Saved file " + outputFileName);

	}




	//////////////////////// FUNCTIONS TO SAVE IMAGES IN PNG FORMAT //////////////////////////////////////////

	/*
	 * Saves RGB data as an image in png format to the specified location.
	 */
	public static void saveImage(int width, byte[] rgbData, String fileName)
	{
		try {

			BufferedImage image = bytes2RGB(width, rgbData);
			ImageIO.write(image, "png", new File(fileName));

		} catch (IOException e) {
			System.out.println("ERROR SAVING FILE: " + e.getMessage());
		}

	}

	/*
	 * Producing a BufferedImage that can be saved as png from a byte array of RGB values.
	 */
	public static BufferedImage bytes2RGB(int width, byte[] buffer) {
	    int height = buffer.length / width / 3;
	    ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
	    ColorModel cm = new ComponentColorModel(cs, false, false,
	            Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
	    SampleModel sm = cm.createCompatibleSampleModel(width, height);
	    DataBufferByte db = new DataBufferByte(buffer, width * height);
	    WritableRaster raster = Raster.createWritableRaster(sm, db, null);
	    BufferedImage result = new BufferedImage(cm, raster, false, null);

	    return result;
	}

	public static class RayTracerException extends Exception {
		public RayTracerException(String msg) {  super(msg); }
	}


}
