package eu.trentorise.smartcampus.filestorage.utils;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import eu.trentorise.smartcampus.filestorage.model.Resource;

@Service
public class ResourceUtils {

	public Resource getResource(String rid, MultipartFile file)
			throws IOException {
		Resource res = getResource(file);
		res.setId(rid);
		return res;
	}

	public Resource getResource(MultipartFile file) throws IOException {
		Resource res = new Resource();
		res.setContent(file.getBytes());
		res.setContentType(file.getContentType());
		res.setName(file.getOriginalFilename());
		return res;
	}
}
