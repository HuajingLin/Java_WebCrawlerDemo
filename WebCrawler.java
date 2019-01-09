/*
 * For Extra Credit Assignment: Web Crawler and Word Count Ranking
 * Student: Huajing Lin
 * date: 12/7/2017
 */
package webcrawler;

import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JScrollPane;

public class WebCrawler extends Frame {

    private Label lblParam1;
    private Label lblParam2;
    private Label lblUrl;
    private TextField tfParam1;
    private TextField tfParam2;
    private TextField tfUrl;
    private Button btnStart;
    private Button btnStop;
    private TextArea tfInfo;
    private Thread thread;
    private boolean bStop;

    public WebCrawler() {
        thread = null;
        bStop = false;
        setLayout(new FlowLayout());
        setResizable(false);

        lblParam1 = new Label("Connect Timeout:");
        add(lblParam1);

        tfParam1 = new TextField("1000", 60);
        add(tfParam1);

        lblParam2 = new Label("   Read Timeout:");
        add(lblParam2);

        tfParam2 = new TextField("3000", 60);
        add(tfParam2);

        lblUrl = new Label("Url:");
        add(lblUrl);

        tfUrl = new TextField("https://www.myccp.online/", 70);
        add(tfUrl);

        btnStart = new Button("Start");
        add(btnStart);

        btnStop = new Button("Stop");
        add(btnStop);

        tfInfo = new TextArea(22, 80);
        //JScrollPane scrollPane = new JScrollPane(tfInfo);
        tfInfo.setEditable(false);
        add(tfInfo);
        //add(scrollPane);

        BtnListener listener = new BtnListener();
        btnStart.addActionListener(listener);
        btnStop.addActionListener(listener);

        setTitle("Web Crawler - Huajing Lin");  // "super" Frame sets its title
        setSize(600, 500);        // "super" Frame sets its initial window size

        setVisible(true);         // "super" Frame shows
    }

    public static void main(String[] args) {
        WebCrawler app = new WebCrawler();
        app.addWindowListener(new WindowAdapter() //for closing window
        {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    /**
     * BtnListener is a named inner class used as ActionEvent listener for all
     * the Buttons.
     */
    class BtnListener implements ActionListener, Runnable {

        @Override
        public void actionPerformed(ActionEvent evt) {
            Button source = (Button) evt.getSource();

            if (source == btnStop) {
                System.out.println("stop.");
                bStop = true;
            } else {
                System.out.println("start...");
                bStop = false;
                thread = new Thread(this, "start");
                thread.start();
            }
        }

        @Override
        public void run() {
            crawl();
        }

        private void crawl() {
            String s = tfParam1.getText();
            System.setProperty("sun.net.client.defaultConnectTimeout", s);
            
            s = tfParam2.getText();
            System.setProperty("sun.net.client.defaultReadTimeout", s);
            
            s = tfUrl.getText();
            if(s.length() <= 7){
                tfInfo.setText("URL error.");
                return;
            }
            
            // list of web pages to be examined
            Queue<String> queue = new LinkedList<String>();
            queue.offer(s);

            // set of examined web pages
            HashSet<String> marked = new HashSet<String>();
            marked.add(s);

            //variable to calculate the time of sum
            long startTime = 0;
            long endTime = 0;
            long duration = 0;
            double spendTime = 0;
            String v,w;
        
            // breadth first search crawl of web
            while (!queue.isEmpty() && !bStop) {
                v = queue.poll();
                //System.out.println(v);
                tfInfo.append("open " + v + "\n");
                
                // get the start time in nanoseconds
                startTime = System.nanoTime();
                String input = null;
                try {
                    In in = new In(v);
                    input = in.readAll();
                    if (input != null) {
                        input = input.toLowerCase();
                    } else {
                        tfInfo.append("\t[ could not open " + v + " ]\n");
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("\t[ could not open " + v + " ]");
                    continue;
                }

                if (input == null) {
                    continue;
                }
                System.out.println("-> " + v + ", Number of times each word:");
                /**
                 * ***********************************************************
                 * Find links of the form: http://xxx.yyy.com \\w+ for one or
                 * more alpha-numeric characters \\. for dot could take first
                 * two statements out of loop
                 * ***********************************************************
                 */
                String regexp = "(http|https)://(\\w+\\.)+(edu|com|gov|org)";
                Pattern pattern = Pattern.compile(regexp);

                Matcher matcher = pattern.matcher(input);

                tfInfo.append("\t[ links: ]\n");
                // find and print all matches
                while (matcher.find()) {
                    w = matcher.group();
                    if (!marked.contains(w)) {
                        queue.offer(w);
                        marked.add(w);
                        tfInfo.append("\t\t[ " + w + " ]\n");
                    }
                }
                
                v = String.format("\t[ data size: %d ]\n", input.length());
                tfInfo.append(v);
                
                //remove HTML characters
                input = HtmlUtil.getTextFromHtml(input);
                
                //System.out.println(input);
                v = String.format("\t[ After removing HTML, data size: %d ]\n", input.length());
                tfInfo.append(v);
                
                //remove stopwords
                input = Stopwords.removeStopWords(input);
                v = String.format("\t[ After removing Stopwords, data size: %d ]\n", input.length());
                tfInfo.append(v);
                
                //Remove non-ASCII characters
                input = input.replaceAll("[^\\x20-\\x7e]", "");
                input = input.replaceAll("&#39;", "");
                input = input.replaceAll("&quot;", "");
                input = input.replaceAll("\"", "");
                
                int count = 0;                
                String tokens[] = input.split("[\\s,.:]");
                HashMap<String, Integer> map = new HashMap<String, Integer>();
                for (int i = 0; i < tokens.length; i++) {
                    w = tokens[i];//.toLowerCase();
                    if(w.length() == 0)
                        continue;
                    if (map.containsKey(w)) {
                        count = map.get(w);
                        map.put(w, count + 1);
                        //System.out.printf("%s:%d,",w,count + 1);
                    } else {
                        map.put(w, 1);
                        //System.out.printf("%s:%d,",w, 1);
                    }
                }

                v = String.format("\t[ words size: %d ]\n", map.size());
                tfInfo.append(v);

                Iterator<Map.Entry<String, Integer>> it = map.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, Integer> pair = it.next();
                    System.out.printf("%s:%d,",pair.getKey(), pair.getValue());
                }
                System.out.printf("\n\n");
                //System.out.printf("\t[ words size: %d , %d]\n", map.size(),tokens.length);
                                
                // get the end time in nanoseconds
                endTime = System.nanoTime();

                // calculate elapsed time in nanoseconds
                duration = endTime - startTime;

                spendTime = (double) duration / 1.0e+09;
                w = String.format(" %12.8f seconds.", spendTime);
                tfInfo.append("\t[ Processing time: " + w + " ]\n");
            }
            System.out.println("=== web crawler over ===");
            tfInfo.append("=== web crawler over ===\n");
        }
    }
}
