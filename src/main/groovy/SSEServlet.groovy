import javax.servlet.ServletConfig
import javax.servlet.ServletException
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

/**
 *
 * Date: 15/07/2014
 * Time: 23:40
 * @author Geoffroy Warin (http://geowarin.github.io)
 */
@WebServlet(name = "SSEServlet", urlPatterns = ["/async"])
class SSEServlet extends HttpServlet {

    ExecutorService executor

    @Override
    public void init(ServletConfig config) throws ServletException {
        this.executor = Executors.newCachedThreadPool();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // super.doGet(req, resp);

        // make sure to sent the event source content type and encoding
        resp.setContentType("text/event-stream")
        resp.setCharacterEncoding("UTF-8")

        try {
            iterate(resp)
            writeEvent(resp, "status", "{\"complete\":true}")
        }  catch (Exception e) {
            e.printStackTrace()
        }


        // send finalized response
    }

    private void iterate(HttpServletResponse resp) {
        LongTask task = new LongTask(10);
        Future<Boolean> result = executor.submit(task);

        // wait until task is complete sending updates as events
        int last = task.getCurrent();
        while (!result.isDone()) {

            if (task.getCurrent() != last) {
                last = task.getCurrent();

                // send the JSON-encoded data
                StringBuilder data = new StringBuilder(128);
                data
                        .append('{"current":').append(last).append(",")
                        .append("\"total\":").append(task.getTotal()).append("}\n\n");
                System.out.println("DATA: " + data);
                writeEvent(resp, "status", data.toString());
            }
        }
    }

    protected void writeEvent(HttpServletResponse resp, String event, String message) throws IOException {

        PrintWriter writer = resp.getWriter();
//        writer.write("event: " + event + "\n\n");
        writer.write("data: " + message + "\n\n");

        writer.flush();
        resp.flushBuffer();
    }

    public static class LongTask implements Callable<Boolean> {

        private int current;
        private int total;

        public LongTask(int total) {
            this.total = total;
        }

        public int getCurrent() {
            return this.current;
        }

        public int getTotal() {
            return this.total;
        }

        @Override
        public Boolean call() throws Exception {
            while (++this.current < this.total) {
                Thread.sleep(1000);
            }
            return true;
        }

    }
}
