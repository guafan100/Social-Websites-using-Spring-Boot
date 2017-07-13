package com.guafan100.controllers;

import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import javax.validation.Valid;

import org.owasp.html.PolicyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.guafan100.exceptions.ImageTooSmallException;
import com.guafan100.exceptions.InvalidFileException;
import com.guafan100.model.FileInfo;
import com.guafan100.model.Interest;
import com.guafan100.model.Profile;
import com.guafan100.model.SiteUser;
import com.guafan100.service.FileService;
import com.guafan100.service.InterestService;
import com.guafan100.service.ProfileService;
import com.guafan100.service.UserService;
import com.guafan100.status.PhotoUploadStatus;

@Controller
public class ProfileController {

	@Autowired
	private UserService userService;

	@Autowired
	private ProfileService profileService;

	@Autowired
	private PolicyFactory htmlPolicy;
	
	@Autowired
	private FileService fileService;
	
	@Autowired
	private InterestService interestService;

	@Value("${photo.upload.directory}")
	private String photoUploadDirectory;
	
	@Value("${photo.upload.ok}")
	private String photoStatusOk;
	
	@Value("${photo.upload.invalid}")
	private String photoStatusInvalid;
	
	@Value("${photo.upload.ioexception}")
	private String photoStatusIOException;
	
	@Value("${photo.upload.toosmall}")
	private String photoStatusTooSmall;

	private SiteUser getUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();

		return userService.get(email);
	}
	
	private ModelAndView showProfile(SiteUser user) {
		
		ModelAndView mav = new ModelAndView();
		if(user == null) {
			mav.setViewName("redirect:/");
			return mav;
		}
		
		Profile profile = profileService.getUserProfile(user);

		if (profile == null) {
			profile = new Profile();
			profile.setUser(user);
			profileService.save(profile);
		}

		Profile webProfile = new Profile();
		webProfile.saveCopyFrom(profile);

		mav.getModel().put("userId", user.getId());
		mav.getModel().put("profile", webProfile);
		mav.setViewName("app.profile");
		
		return mav;
	}

	@RequestMapping(value = "/profile")
	public ModelAndView showProfile() {

		SiteUser user = getUser();
		
		ModelAndView mav = showProfile(user);
		
		mav.getModel().put("ownProfile", true);
		
		return mav;
	}
	
	@RequestMapping(value = "/profile/{id}")
	public ModelAndView showProfile(@PathVariable("id") Long id) {	

		SiteUser user = userService.get(id);
		
		ModelAndView mav = showProfile(user);
		
		mav.getModel().put("ownProfile", false);

		return mav;
	}

	@RequestMapping(value = "/edit-profile-about", method = RequestMethod.GET)
	public ModelAndView editProfileAbout(ModelAndView mav) {

		SiteUser user = getUser();
		Profile profile = profileService.getUserProfile(user);

		Profile webProfile = new Profile();
		webProfile.saveCopyFrom(profile);

		mav.getModel().put("profile", webProfile);
		mav.setViewName("app.editProfileAbout");

		return mav;
	}

	@RequestMapping(value = "/edit-profile-about", method = RequestMethod.POST)
	public ModelAndView editProfileAbout(ModelAndView mav, @Valid Profile webProfile, BindingResult result) {

		mav.setViewName("app.editProfileAbout");

		SiteUser user = getUser();
		Profile profile = profileService.getUserProfile(user);

		profile.safeMergeFrom(webProfile, htmlPolicy);

		if (!result.hasErrors()) {
			profileService.save(profile);
			mav.setViewName("redirect:/profile");
		}

		return mav;
	}

	@RequestMapping(value = "/upload-profile-photo", method = RequestMethod.POST)
	@ResponseBody //return data in JSON format
	public ResponseEntity<PhotoUploadStatus> handlePhotoUploads(@RequestParam("file") MultipartFile file) {
		
		SiteUser user = getUser();
		Profile profile = profileService.getUserProfile(user);
		
		Path oldPhotoPath = profile.getPhoto(photoUploadDirectory);
		
		PhotoUploadStatus status = new PhotoUploadStatus(photoStatusOk);
		
		try {
			FileInfo photoInfo = fileService.saveImgFile(file, photoUploadDirectory, "photo", "p"+ user.getId(), 128, 128);
			
			profile.setPhotoDetails(photoInfo);
			profileService.save(profile);
			
			if(oldPhotoPath != null) {
				Files.delete(oldPhotoPath);
			}			
			
		} catch (InvalidFileException e) {
			status.setMessage(photoStatusInvalid);
			e.printStackTrace();
		} catch (IOException e) {
			status.setMessage(photoStatusIOException);
			e.printStackTrace();
		} catch (ImageTooSmallException e) {
			status.setMessage(photoStatusTooSmall);
			e.printStackTrace();
		}

		return new ResponseEntity<PhotoUploadStatus>(status, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/profilephoto/{id}", method = RequestMethod.GET)
	@ResponseBody
	ResponseEntity<InputStreamResource> servePhoto(@PathVariable("id") Long id) throws IOException {
		SiteUser user = userService.get(id);
		Profile profile = profileService.getUserProfile(user);

		Path photoPath = Paths.get(photoUploadDirectory, "default", "sss.jpg");

		if (profile != null && profile.getPhoto(photoUploadDirectory) != null) {
			photoPath = profile.getPhoto(photoUploadDirectory);
		}

		return ResponseEntity.ok().contentLength(Files.size(photoPath))
				.contentType(MediaType.parseMediaType(URLConnection.guessContentTypeFromName(photoPath.toString())))
				.body(new InputStreamResource(Files.newInputStream(photoPath, StandardOpenOption.READ)));
	}
	
	@RequestMapping(value="/save-interest", method=RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> saveInterest(@RequestParam("name") String interestName) {
		System.out.println("========run save");
		SiteUser user = getUser();
		Profile profile = profileService.getUserProfile(user);
		
		String cleanedInterestName = htmlPolicy.sanitize(interestName);
		
		Interest interest = interestService.createIfNotExists(cleanedInterestName);
		
		profile.addInterest(interest);
		profileService.save(profile);
		
		return new ResponseEntity<>(null, HttpStatus.OK);
	}
	
	@RequestMapping(value="/delete-interest", method=RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> deleteInterest(@RequestParam("name") String interestName) {
		
		System.out.println("================run delete");
		SiteUser user = getUser();
		Profile profile = profileService.getUserProfile(user);
	
		profile.removeInterest(interestName);
	
		profileService.save(profile);
		
		return new ResponseEntity<>(null, HttpStatus.OK);

	}
}
