package pixelpaint;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import javax.imageio.ImageIO;

public class Word2Txt {
//路径：D:/CC-JAVA/IO，使用时请自行更改。
	public static void main(String[] argv) throws IOException {
		java.awt.image.BufferedImage img = ImageIO.read(new File("D:/CC-JAVA/IO/in.jpg"));
		File newfiFile = new File("D:/CC-JAVA/IO/file.txt");
		FileOutputStream fileOutputStream;
		int width = img.getWidth();
		int height = img.getHeight();
		int q = 1;
		int max = 0;
		try {
			fileOutputStream = new FileOutputStream(newfiFile);
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(newfiFile));
			BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

			for (int y = 0; y < height; y += q) {
				for (int x = 0; x < width; x += q) {
					int r = img.getRGB(x, y);

					int red = (r >> 16) & 0x0ff;
					int green = (r >> 8) & 0x0ff;
					int blue = r & 0x0ff;

					max = Math.max(red, Math.max(green, blue));
					if (max < 178) {
						bufferedWriter.write("8");
						bufferedWriter.write("\n");
					} else {
						bufferedWriter.write("9");
						bufferedWriter.write("\n");
					}
					
					//这里只有两种输出，就是单色的地图画，大家可以自行添加更多的输出。
					
				}
			}

			bufferedWriter.close();
			outputStreamWriter.close();
			fileOutputStream.close();
			System.out.println("ok");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
