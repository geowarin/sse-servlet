import javax.servlet.ServletException
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 *
 * Date: 15/07/2014
 * Time: 23:40
 * @author Geoffroy Warin (http://geowarin.github.io)
 */
@WebServlet(name = "MyServlet", urlPatterns = ["/hello"])
class HelloServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.writer;
        writer.println "Hello from servlet"
        writer.close();
    }

}
