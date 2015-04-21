import java.util.List;
import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;

import static com.googlecode.objectify.ObjectifyService.ofy;


public class DeleteAll extends HttpServlet {
	static{
		ObjectifyService.register(PointStatistic.class);
	}
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    	List<Key<PointStatistic>> keys = ofy().load().type(PointStatistic.class).keys().list();
    	ofy().delete().keys(keys).now();    }

}
