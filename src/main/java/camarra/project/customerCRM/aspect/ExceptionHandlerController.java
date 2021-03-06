package camarra.project.customerCRM.aspect;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.hibernate.HibernateException;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
@RequestMapping("/")

public class ExceptionHandlerController {

	static private final Logger myLogger = Logger.getLogger(ExceptionHandler.class.getName());
	private FileHandler handler;

	public ExceptionHandlerController() {

		Path path = FileSystems.getDefault().getPath("").toAbsolutePath();

		try {

			handler = new FileHandler(path.toString() + "src/main/resources/mylog.txt", true);
			handler.setFormatter(new SimpleFormatter());
			myLogger.addHandler(handler);
			handler.close();

		} catch (Exception exc) {
			System.out.println("couldnt initialize handler");
		}
	}

	// Handles any unauthorized requests.
	@GetMapping("/access-denied")
	public String accessedDeniedPage() {
		return "access-denied";

	}

	// handle 404 status
	@ExceptionHandler(NoHandlerFoundException.class)
	public String handle404Error(NoHandlerFoundException exc, Model theModel) {

		String message = exc.getLocalizedMessage();
		theModel.addAttribute("exception", message);

		return "testing";

	}

	// handle any hibernate exceptions
	@ExceptionHandler(HibernateException.class)
	public String handleHibernateException(HibernateException exc, Model theModel) {
		// "\r\n" means new line when writing to a file
		String message = "An error has occured: " + exc.getLocalizedMessage() + "\r\n";

		myLogger.warning(message);

		theModel.addAttribute("exception", message);

		return "testing";
	}

	// handle any other runtime/unchecked exception and log it

	@ExceptionHandler(RuntimeException.class)
	public String handleRuntimeExceptions(RuntimeException exc, Model theModel) {

		if (exc instanceof HibernateException) {
			
			return handleHibernateException((HibernateException) exc.getCause(),theModel);
		}
		String message = "An error has occured: " + exc.getLocalizedMessage() + "\n"
				+ exc.getCause().getCause().toString() + "\r\n";
		myLogger.warning(message);

		theModel.addAttribute("exception", message);

		return "testing";
	}
}
