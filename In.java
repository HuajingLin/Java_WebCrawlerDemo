/*
 * For Extra Credit Assignment: Web Crawler and Word Count Ranking
 * Student: Huajing Lin
 * date: 12/7/2017
 */
package webcrawler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class In {
    private String strUrl;
    In(String url){
        strUrl = url;
    }
    public String readAll(){
        String str = "";
        String strTemp = "";
        try {
            URL my_url = new URL(strUrl);
            BufferedReader br = new BufferedReader(new InputStreamReader(my_url.openStream()));
            
            while (null != (strTemp = br.readLine())) {
                //System.out.println(strTemp);
                str += strTemp;
            }
        } catch (Exception ex) {
            //ex.printStackTrace();
            return null;
        }
        return str;
    }
}
