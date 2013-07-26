package eu.trentorise.smartcampus.filestorage.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;

import org.apache.activemq.util.ByteArrayInputStream;
import org.apache.commons.io.FileUtils;
import org.imgscalr.Scalr;

public class ImageUtils {

	static public byte[] imageCompression(byte[] src) throws IOException {
		BufferedImage immagine = null;

		immagine = ImageIO.read(new ByteArrayInputStream(src));

		immagine = Scalr.resize(immagine, Scalr.Method.SPEED,
				Scalr.Mode.AUTOMATIC, 1024, 768, Scalr.OP_ANTIALIAS);

		// fix for Dropbox
		// immagine = Scalr.rotate(immagine, Rotation.CW_90, null);
		Iterator<ImageWriter> it = ImageIO.getImageWritersByFormatName("jpg");
		ImageWriter jpgWriter = it.next();
		ImageWriteParam parametri = jpgWriter.getDefaultWriteParam();
		parametri.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		parametri.setCompressionQuality(0.5f); // 0 max compression

		File temp = File.createTempFile("dropbox", "");
		FileImageOutputStream out = new FileImageOutputStream(temp);
		jpgWriter.setOutput(out);
		jpgWriter.write(null, new IIOImage(immagine, null, null), parametri);
		jpgWriter.dispose();
		out.close();
		byte[] compressed = FileUtils.readFileToByteArray(temp);
		temp.delete();
		return compressed;
	}
}
